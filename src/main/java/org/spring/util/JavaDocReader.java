package org.spring.util;

import com.sun.javadoc.RootDoc;
import com.sun.tools.javadoc.Main;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JavaDocReader {

    private static RootDoc root;

    public static class Doclet {
        public static boolean start(RootDoc root) {
            JavaDocReader.root = root;
            return true;
        }
    }

    /**
     * 获取java源码文档注释
     *
     * @param classpath classpath
     * @param javaResourcePath 源码文件路径
     */
    public static RootDoc getDocClass(String classpath, String javaResourcePath) {
        Main.execute(new String[] {
            "-doclet", Doclet.class.getName(),
            "-encoding", "utf-8",
            "-classpath", classpath,
            javaResourcePath
        });
        return root;
    }

    /**
     * 获取java源码文档注释
     *
     * @param classpath classpath
     * @param javaRootPath 源码根路径
     * @param qualifiedName 权限的类名
     */
    public static RootDoc getDocClass(String classpath, String javaRootPath, String qualifiedName) {
        String javaResourcePath = Paths.get(javaRootPath, qualifiedName.split("\\.")).toString();
        return getDocClass(classpath, javaResourcePath + ".java");
    }
}