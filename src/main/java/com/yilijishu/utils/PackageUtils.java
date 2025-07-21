package com.yilijishu.utils;

/**
 * 包操作类
 */
public class PackageUtils {

    /**
     * 获取指定类的包路径
     *
     * @param clazz 目标类
     * @return 包路径
     */
    public static String getPackageName(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class cannot be null");
        }
        return clazz.getPackage().getName();
    }

    /**
     * 获取指定类的全限定名（包括包路径）
     *
     * @param clazz 目标类
     * @return 全限定名
     */
    public static String getFullyQualifiedName(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class cannot be null");
        }
        return clazz.getName();
    }
}