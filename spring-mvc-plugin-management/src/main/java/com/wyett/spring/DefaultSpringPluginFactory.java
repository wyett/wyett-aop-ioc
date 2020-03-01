package com.wyett.spring;

import org.aopalliance.aop.Advice;
import org.aspectj.lang.JoinPoint;
import org.aspectj.util.FileUtil;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : wyettLei
 * @date : Created in 2020/2/29 18:32
 * @description: TODO
 */

public class DefaultSpringPluginFactory implements ApplicationContextAware {
    private Map<String, PluginConfig> configs = new HashMap<>();
    private Map<String, Advice> adviceCache = new HashMap<>();

    private ApplicationContext applicationContext;

    private String configPath;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 安装插件
     * @param config
     * @return
     */
    public Advice buildAdvice(PluginConfig config) throws Exception{
        if (adviceCache.containsKey(config.getClassName())) {
            return adviceCache.get(config.getClassName());
        }
        // 获取本地待加载的jar插件包路径
        URL targetUrl = new URL(config.getJarRemoteUrl());
        // 获取当前正在运行的项目加载了哪些jar包
        URLClassLoader loader = (URLClassLoader) getClass().getClassLoader();
        boolean isLoader = false;
        for (URL url : loader.getURLs()) {
            // 判断当前待加载的jar包，是否已被加载到loader
            if (url.equals(targetUrl)) {
                isLoader = true;
                break;
            }
        }
        // 如果插件jar包没有被加载到loader，则调用invoke将jar加载进来
        if (!isLoader) {
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] {URL.class});
            method.setAccessible(true);
            method.invoke(loader, targetUrl); //加载本地jar文件
        }

        // 将插件jar包里的类创建Class对象, Advice
        Class<?> adviceClass = loader.loadClass(config.getClassName());

        // adviceClass对象通过反射创建advice实例
        adviceCache.put(adviceClass.getName(), (Advice) adviceClass.newInstance());
        // 返回advice对象
        return adviceCache.get(adviceClass.getName());
    }


    /**
     * 激活插件
     * @param pluginId
     */
    public void activePlugin(String pluginId) {
        if (!configs.containsKey(pluginId)) {
            throw new RuntimeException(String.format("不存在插件{}", pluginId));
        }
        PluginConfig config = configs.get(pluginId);

        config.setActive(true);

        //便历所有spring controller, service
        //spring tomcat 加载启动的时候，收集所有bean的名称
        for (String name : applicationContext.getBeanDefinitionNames()) {
            Object bean = applicationContext.getBean(name);// UserController
            if (bean == this) { continue; }
            if (!(bean instanceof Advised)) { continue; } //是否被增强过
            // 检查bean里有没有通知
            if (findAdvice((Advised) bean, config.getClassName()) != null) {
                continue;
            }
            //controller
            Advice advice = null;
            try {
                advice = buildAdvice(config);
                ((Advised) bean).addAdvice(advice); //实现了拦截
            } catch (Exception e) {
                throw new RuntimeException("安装失败", e);
            }
        }
    }

    /**
     * 禁用插件
     * @param pluginId
     */
    public void disablePlugin(String pluginId) {
        if (!configs.containsKey(pluginId)) {
            throw new RuntimeException(String.format("指定插件{}不存在", pluginId));
        }
        PluginConfig config = configs.get(pluginId);
        // 设置为不生效
        config.setActive(false);
        // 便历所有beans，移除config
        for (String name : applicationContext.getBeanDefinitionNames()) {
            Object bean = applicationContext.getBean(name);
            if (bean instanceof Advised) {
                Advice advice = findAdvice((Advised) bean, config.getClassName());
                if (advice != null) {
                    ((Advised) bean).removeAdvice(advice);
                }
            }
        }
    }

    /**
     * 查找切面
     * @param advised
     * @param className
     * @return
     */
    public Advice findAdvice(Advised advised, String className) {
        for (Advisor advisor : advised.getAdvisors()) {
            if (advisor.getAdvice().getClass().getName().equals(className)) {
                return advisor.getAdvice();
            }
        }
        return null;
    }

    public void doBefore(JoinPoint joinPoint) {

    }

    public Collection<PluginConfig> flushConfigs() throws IOException {
        File configFile = new File(configPath);
        String configJson = FileUtil.readAsString(configFile);
        Plugins pluginConfigs = JSON.parsObject(configJson, Plugins.class);

        for (PluginConfig pluginConfig : pluginConfigs.getConfigs()) {
            if (configs.get(pluginConfig.getId()) == null) {
                configs.put(pluginConfig.getId(), pluginConfig);
            }
        }
        return configs.values();
    }


}
