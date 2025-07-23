package com.yilijishu.utils;


import lombok.Data;
import lombok.SneakyThrows;

public class Test {

    @SneakyThrows
    public static void main(String[] args) {

        Abc a = new Abc();
        Class clz = Boolean.class;
        Object obj = clz.cast("true");
        a.getClass().getMethod("setDelTag", clz).invoke(a, obj);
        a.getClass().getMethod("getDelTag").invoke(a);
        System.out.println(a.getDelTag());

    }

    @Data
    public static class Abc {
        private String abc;

        private Boolean delTag;
    }
}
