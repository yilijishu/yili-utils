package com.yilijishu.utils;

import java.util.Random;

/**
 * H5随机码生成工具
 */
public class RandomUtil {

    private static final String BASE = "abcdefghijklmnopqrstuvwxyz0123456789";

    /**
     * 生成固定位数的随机数字
     *
     * @param size 随机数长度
     * @param flag 是否生成字符与数字的随机组合
     * @return String淄川
     */
    public static String getRandomNum(int size, boolean flag) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            if (flag) {
                sb.append(BASE.charAt(random.nextInt(BASE.length() - 1)));
            } else {
                sb.append(random.nextInt(9) + 1);
            }
        }
        return sb.toString();
    }

    /**
     * 获取4位随机数
     *
     * @return Integer 数字
     */
    public static Integer getRandomNum() {
        return Integer.parseInt(getRandomNum(4, false));
    }

    public static void main(String[] args) {
        for (int i = 0; i < 24; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println(getRandomNum());
                    //System.out.println(getRandomNum(4, true));
                }
            }).start();
        }

    }
}
