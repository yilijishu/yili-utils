package com.yilijishu.utils;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Lambda yili 工具
 */
public class LambdaYiliUtils {

    /**
     * 自定义lambda
     * @param consumer 自定义
     * @param <T> 泛型类
     * @return 返回lambda泛型类
     */
    public static <T> Consumer<T> consumerWithIndex(BiConsumer<T, Integer> consumer) {
        class Obj {
            int i;
        }
        Obj obj = new Obj();
        return t -> {
            int index = obj.i++;
            consumer.accept(t, index);
        };
    }
}
