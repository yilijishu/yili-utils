package com.yilijishu.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * 代码路径操作类
 */
public class YiliCodePathUtils {

    /**
     * 路径调整
     * @param paths  路径数组
     * @return 返回完整路径
     */
    public static String join(String... paths) {
        String result = "";
        for ( int i = 0; i < paths.length; i ++ ) {
            result = result.concat(paths[i].startsWith("/") ? paths[i] : "/" + paths[i]);

            if(result.endsWith("/")) {
                result = result.substring(0, result.length() - 1);
            }
        }
        return result;
    }

    /**
     * 包名转换成路径
     * @param packageName 包名  com.yilijishu.cc.user
     * @return 返回路径 com/yilijishu/cc/user
     */
    public static String convertToPath(String packageName) {
        return packageName.replaceAll("\\.", "/");
    }

    /**
     * 路径转换成包名
     * @param path 路径 com/yilijishu/cc/user
     * @return 返回包名 com.yilijishu.cc.user
     */
    public static String convertToPackage(String path) {
        return path.replaceAll("/", ".");
    }

    /**
     * 路径去除
     * @param path 路径
     * @param sub 从尾部去掉的层数
     * @return 返回路径
     */
    public static String rectification(String path, int sub) {
        if(sub > 0) {
            String[] names = path.split("/");
            StringBuffer result = new StringBuffer();
            int c = names.length - sub;
            if (c > 0) {
                for (int i = 0; i < c; i++) {
                    String tmp = names[i];
                    if (StringUtils.isNotBlank(tmp)) {
                        result.append(tmp);
                        if (i < c - 1) {
                            result.append("/");
                        }
                    }
                }
            }
            return result.toString();
        } else {
            return path.endsWith("/")?path.substring(0, path.length()-1):path;
        }
    }
}
