package com.yilijishu.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

/**
 * 比较工具包
 */
public class CompareUtil extends StringUtils {

    /**
     * 是否大于0
     * @param a 数字
     * @return 真假
     */
    public static boolean isTrue(Long a) {
        return a != null && a > 0;
    }

    /**
     * 集合是否不为空
     * @param collection 集合
     * @return  真假
     */
    public static boolean isNotEmpty(Collection collection) {
        return collection != null && collection.size() > 0;
    }

    /**
     *  集合是否为空
     * @param collection 集合
     * @return 真假
     */
    public static boolean isEmpty(Collection collection) {
        return !(collection != null && collection.size() > 0);
    }

    /**
     * 截取字符，如果超出则返回原值
     *
     * @param content  字符串
     * @param length  长度
     * @return  截取后的字符串
     */
    public static String sub(String content, int length) {
        if (StringUtils.isNotBlank(content) && content.length() > length) {
            return content.substring(0, length);
        }
        return content;
    }

    /**
     * 删除首个prefix
     *
     * @param content 字符串
     * @param prefix  要删除的开始的字符串
     * @return 删除后的字符串
     */
    public static String delFirstPrefix(String content, String prefix) {
        while (isNotBlank(content) && isNotBlank(prefix) && content.startsWith(prefix)) {
            content = content.substring(prefix.length());
        }
        return content;
    }

    /**
     * 拼接URL数据
     *
     * @param strings 链接数组
     * @return 拼接后的字符串
     */
    public static String concatUrl(String... strings) {
        StringBuffer sbf = new StringBuffer();
        if (strings != null && strings.length > 0) {
            for (int i = 0; i < strings.length; i++) {
                if (isNotBlank(strings[i])) {
                    if (i == 0) {
                        if (strings[i].endsWith("/")) {
                            sbf.append(strings[i]);
                        } else {
                            sbf.append(strings[i]);
                            sbf.append("/");
                        }
                    } else if (i + 1 == strings.length) {
                        if (strings[i].startsWith("/")) {
                            sbf.append(strings[i].substring(1));
                        } else {
                            sbf.append(strings[i]);
                        }
                    } else {
                        if (strings[i].startsWith("/")) {
                            if (strings[i].endsWith("/")) {
                                sbf.append(strings[i].substring(1));
                            } else {
                                sbf.append(strings[i].substring(1));
                                sbf.append("/");
                            }
                        } else {
                            if (strings[i].endsWith("/")) {
                                sbf.append(strings[i]);
                            } else {
                                sbf.append(strings[i]);
                                sbf.append("/");
                            }
                        }
                    }
                }
            }
        }
        return sbf.toString();
    }
}
