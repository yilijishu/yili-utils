package com.yilijishu.utils;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * 枚举集合工具
 */
@Data
public class EnumsSetUtil {

    /**
     * 获取对应枚举名的枚举类
     * @param cls 枚举泛型类
     * @param name 枚举名
     * @param <T> 泛型类
     * @return  返回枚举
     */
    @Deprecated
    public static <T extends Enum<T>> T set(Class<T> cls, String name) {
        if (StringUtils.isNotBlank(name)) {
            try {
                T t = Enum.valueOf(cls, name);
                return t;
            } catch (IllegalArgumentException il) {
                return null;
            }
        }
        return null;
    }

    /**
     * 获取对应枚举名的枚举类
     * @param cls 枚举泛型类
     * @param name 枚举名
     * @param <T> 泛型类
     * @return  返回枚举
     */
    public static <T extends Enum<T>> T get(Class<T> cls, String name) {
        if (StringUtils.isNotBlank(name)) {
            try {
                T t = Enum.valueOf(cls, name);
                return t;
            } catch (IllegalArgumentException il) {
                return null;
            }
        }
        return null;
    }

//    public static void main(String[] args) {
//        System.out.println(EnumsSetUtil.get(SourceEnums.class, null));
//    }
}
