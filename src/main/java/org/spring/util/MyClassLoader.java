package org.spring.util;

import org.apache.commons.lang3.ClassUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * 自定义类加载器
 */
public class MyClassLoader {

    /**
     * 通过classspath和类名加载class对象
     *
     * @param classpath classpath
     * @param className 类全名
     * @return class对象
     */
    public static Class<?> LoaderClass(String classpath, String className)
            throws MalformedURLException, ClassNotFoundException {
                ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        URL classes = new URL("file:///" + classpath);
        ClassLoader custom = new URLClassLoader(new URL[]{classes}, systemClassLoader);
        return ClassUtils.getClass(custom, className, false);
    }
}