package com.wyett.mvc.servlet;

import com.wyett.mvc.annotation.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : wyettLei
 * @date : Created in 2020/2/27 17:17
 * @description: TODO
 */

public class DispatcherServlet extends HttpServlet {

    // save class info
    List<String> classNames = new ArrayList<>();
    // iocMap, spring with synchronized
    Map<String, Object> beans = new HashMap<>();
    // methodPath and method Object
    Map<String, Object> methodMap = new HashMap<>();


    // scan init bean
    public void init(ServletConfig config) {
        // scan
        scanPackage("com.wyett");
        // instance
        doInstance();
        // autowired
        doAutowired();
        // url http://localhost:8080/demo/query ---> method
        urlMapping();
    }

    /**
     * get all className in @basePackage
     * @param basePackage root package
     */
    public void scanPackage(String basePackage) {
        URL url = this.getClass().getClassLoader().getResource("/" + basePackage.replace("\\.", "/"));
        String fileStr = url.getFile();
        File file = new File(fileStr);

        String[] filesStr = file.list();

        for(String path: filesStr) {
            File filePath = new File(fileStr + path);
            if (filePath.isDirectory()) {
                // recursive if filePath is dir
                scanPackage(basePackage + "." + path);
            } else {
                // com.wyett....xxx.class
                classNames.add(basePackage + "." + filePath.getName());
            }
        }

    }

    /**
     * instance with @WyettController @WyettService
     */
    public void doInstance() {
        for(String className : classNames) {
            // remove ".class"
            String cName = className.replace(".class", "");

            try {
                Class<?> clazz = Class.forName(cName);

                if(clazz.isAnnotationPresent(WyettController.class)) {
                    Object instance = clazz.newInstance();
                    // get key
                    //WyettController wyettController = clazz.getAnnotation(WyettController.class);
                    //String key = wyettController.value();
                    WyettRequestMapping requestMapping = clazz.getAnnotation(WyettRequestMapping.class);
                    String key = requestMapping.value();

                    // save (key, instance) into iocMap
                    beans.put(key, instance);
                } else if (clazz.isAnnotationPresent(WyettService.class)){
                    Object instance = clazz.newInstance();
                    // get key
                    WyettService wyettService = clazz.getAnnotation(WyettService.class);
                    String key = wyettService.value();

                    // save (key, instance) into iocMap
                    beans.put(key, instance);
                } else {
                    continue;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * autowired bean
     */
    private void doAutowired() {
        for(Map.Entry<String, Object> entry : beans.entrySet()) {
            Object instance = entry.getValue();

            Class<?> clazz = instance.getClass();

            if (clazz.isAnnotationPresent(WyettController.class)
                    || clazz.isAnnotationPresent(WyettService.class)) {
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    if(field.isAnnotationPresent(WyettAutowired.class)) {
                        // get
                        WyettAutowired wyettAutowired = field.getAnnotation(WyettAutowired.class);
                        String key = wyettAutowired.value();

                        // modify field previlege
                        field.setAccessible(true);

                        try {
                            field.set(instance, field.get(key));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                continue;
            }
        }
    }

    //
    public void urlMapping() {
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            Object instance = entry.getValue();

            Class<?> clazz = instance.getClass();

            // only class with @WyettController has @WyettRequestMapping
            if (clazz.isAnnotationPresent(WyettController.class)) {
                // get class annotation
                WyettRequestMapping classRequestMapping = clazz.getAnnotation(WyettRequestMapping.class);
                String classPath = classRequestMapping.value();

                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if(method.isAnnotationPresent(WyettRequestMapping.class)) {
                        // get method annotation
                        WyettRequestMapping methodRequestMapping = method.getAnnotation(WyettRequestMapping.class);
                        String methodPath = methodRequestMapping.value();
                        methodMap.put(classPath + methodPath, method);
                    } else {
                        continue;
                    }
                }
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        this.doPost(request, response);
    }

    /**
     * http://localhost:8080/demo/wyett/query
     * @param request
     * @param response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        // get //demo/wyett/query
        String uri = request.getRequestURI();

        // get /demo
        String context = request.getContextPath();
        // get /wyett/query
        String path = uri.replace(context, "");
        Method method = (Method)methodMap.get(path);

        // beans.get("/wyett")
        Object instance = beans.get("/" +path.split("/")[1]);

        Object args[] = hand(request, response, method);

        try {
            method.invoke(instance, args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


    }

    // 可以用策略模式实现
    private static Object[] hand(HttpServletRequest request, HttpServletResponse response, Method method) {
        // get parameters of local method
        Class<?>[] paramClazzs = method.getParameterTypes();
        // create array and value all parameters
        Object[] args = new Object[paramClazzs.length];

        int arg_i = 0;
        int index = 0;
        for(Class<?> paramClazz : paramClazzs) {
            if (ServletRequest.class.isAssignableFrom(paramClazz)) {
                args[arg_i++] = request;
            }
            if (ServletResponse.class.isAssignableFrom(paramClazz)) {
                args[arg_i++] = response;
            }
            // 从0-3开始判断有没有RequestParam注解, 0 和1是方法名和返回类型
            Annotation[] paramAnnos = method.getParameterAnnotations()[index];
            if (paramAnnos.length > 0) {
                for (Annotation paramAnno : paramAnnos) {
                    if (WyettRequestParam.class.isAssignableFrom(paramAnno.getClass())) {
                        WyettRequestParam wrp = (WyettRequestParam) paramAnno;
                        args[arg_i++] = request.getParameter(wrp.value());
                    }
                }
            }
            index++;
        }
        return args;





    }



}

