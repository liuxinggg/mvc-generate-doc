package org.spring.util;

import org.springframework.util.StringUtils;

/**
 * 处理spring mapping path
 */
public class PathUtils {

    private static final String DEFAULT_PATH_SEPARATOR = "/";
    private static final String ENDS_ON_WILD_CARD = DEFAULT_PATH_SEPARATOR + "*";
    private static final String ENDS_ON_DOUBLE_WILD_CARD = DEFAULT_PATH_SEPARATOR + "**";

    /**
     * 合并mapping路径
     *
     * @see org.springframework.util.AntPathMatcher#combine(String, String)
     */
    public static String combine(String pattern1, String pattern2) {
        if (!StringUtils.hasText(pattern1) && !StringUtils.hasText(pattern2)) {
            return "";
        }
        if (!StringUtils.hasText(pattern1)) {
            return pattern2;
        }
        if (!StringUtils.hasText(pattern2)) {
            return pattern1;
        }

        boolean pattern1ContainsUriVar = (pattern1.indexOf('{') != -1);

        //XXX spring 处理路径通配的过程比较复杂，实际中开发中使用通配的情况也比较少，我们简单的处理，可能与spring有出入
        if (!pattern1.equals(pattern2) && !pattern1ContainsUriVar) {
            String p1 = pattern1.startsWith(DEFAULT_PATH_SEPARATOR) ? pattern1 : DEFAULT_PATH_SEPARATOR + pattern1;
            if("/*".equals(p1) || "/*.*".equals(p1)) {
                return pattern2;
            }
        }
        //以下是Spring中的源码
//        if (!pattern1.equals(pattern2) && !pattern1ContainsUriVar && match(pattern1, pattern2)) {
//            // /* + /hotel -> /hotel ; "/*.*" + "/*.html" -> /*.html
//            // However /user + /user -> /usr/user ; /{foo} + /bar -> /{foo}/bar
//            return pattern2;
//        }

        // /hotels/* + /booking -> /hotels/booking
        // /hotels/* + booking -> /hotels/booking
        if (pattern1.endsWith(ENDS_ON_WILD_CARD)) {
            return concat(pattern1.substring(0, pattern1.length() - 2), pattern2);
        }

        // /hotels/** + /booking -> /hotels/**/booking
        // /hotels/** + booking -> /hotels/**/booking
        if (pattern1.endsWith(ENDS_ON_DOUBLE_WILD_CARD)) {
            return concat(pattern1, pattern2);
        }

        int starDotPos1 = pattern1.indexOf("*.");
        if (pattern1ContainsUriVar || starDotPos1 == -1 || DEFAULT_PATH_SEPARATOR.equals(".")) {
            // simply concatenate the two patterns
            return concat(pattern1, pattern2);
        }

        String ext1 = pattern1.substring(starDotPos1 + 1);
        int dotPos2 = pattern2.indexOf('.');
        String file2 = (dotPos2 == -1 ? pattern2 : pattern2.substring(0, dotPos2));
        String ext2 = (dotPos2 == -1 ? "" : pattern2.substring(dotPos2));
        boolean ext1All = (ext1.equals(".*") || ext1.equals(""));
        boolean ext2All = (ext2.equals(".*") || ext2.equals(""));
        if (!ext1All && !ext2All) {
            throw new IllegalArgumentException("Cannot combine patterns: " + pattern1 + " vs " + pattern2);
        }
        String ext = (ext1All ? ext2 : ext1);
        return file2 + ext;
    }

    private static String concat(String path1, String path2) {
        boolean path1EndsWithSeparator = path1.endsWith(DEFAULT_PATH_SEPARATOR);
        boolean path2StartsWithSeparator = path2.startsWith(DEFAULT_PATH_SEPARATOR);

        if (path1EndsWithSeparator && path2StartsWithSeparator) {
            return path1 + path2.substring(1);
        }
        else if (path1EndsWithSeparator || path2StartsWithSeparator) {
            return path1 + path2;
        }
        else {
            return path1 + DEFAULT_PATH_SEPARATOR + path2;
        }
    }

}
