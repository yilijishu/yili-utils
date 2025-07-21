package com.yilijishu.utils;

/**
 * Created with IntelliJ IDEA.
 * User: djchen
 * Date: 2016/11/12
 * Time: 18:36
 * Description:
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 加密类
 */
public class MD5Utils {
    public static final byte WRITE_CASE_MODE_UPPER = 1;
    public static final byte WRITE_CASE_MODE_LOWER = 2;
    // 加的盐
    private static final String SALT = "HXWcjvQWVG1wI4FQBLZpQ3pWj48AV63d";
    private static Logger logger = LoggerFactory.getLogger(MD5Utils.class);

    /**
     * MD5加密
     * @param context 加密内容
     * @return MD5串
     */
    public static String encrypByMd5(String context) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(context.getBytes());// update处理
            byte[] encryContext = md.digest();// 调用该方法完成计算

            int i;
            StringBuffer buf = new StringBuffer();
            // 做相应的转化（十六进制）
            for (int offset = 0; offset < encryContext.length; offset++) {
                i = encryContext[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;

    }

    /**
     * 加密 By Md5
     * @param buf buf
     * @return 加密后的串
     */
    public static String encoderByMd5(String buf) {
        try {
            MessageDigest digist = MessageDigest.getInstance("MD5");
            byte[] rs = digist.digest(buf.getBytes());
            StringBuffer digestHexStr = new StringBuffer();
            for (int i = 0; i < 16; i++) {
                digestHexStr.append(byteHEX(rs[i]));
            }
            return digestHexStr.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
        }
        return null;

    }

    /*public static void main(String args[]) {
        System.out.println(MD5Utils.encodeByMd5AndSalt("passsdfsword"));
    }*/

    /**
     * 加盐的md5值。这样即使被拖库，仍然可以有效抵御彩虹表攻击
     *
     * @param inbuf 需做md5的字符串
     * @return 返回加密后的
     */
    public static String encodeByMd5AndSalt(String inbuf) {
        return encoderByMd5(encoderByMd5(inbuf) + SALT);
    }

    /**
     * HEX加密
     * @param ib 字节
     * @return 字符串
     */
    public static String byteHEX(byte ib) {
        char[] digit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
                'B', 'C', 'D', 'E', 'F'};
        char[] ob = new char[2];
        ob[0] = digit[(ib >>> 4) & 0X0F];
        ob[1] = digit[ib & 0X0F];
        String s = new String(ob);
        return s;
    }

    /**
     * MD5加密
     * @param plainText 文件
     * @param writeCaseMode  是否写
     * @return 加密后的串
     */
    public static String mysqlMd5(String plainText, Byte writeCaseMode) {
        if (null == plainText) {
            plainText = "";
        }
        String md5Str = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte[] b = md.digest();
            int i;
            StringBuilder builder = new StringBuilder(32);
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    builder.append("0");
                }
                builder.append(Integer.toHexString(i));
            }
            md5Str = builder.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (writeCaseMode == null || md5Str == null || md5Str.trim().length() <= 0) {
            return md5Str;
        }
        if (writeCaseMode.equals(WRITE_CASE_MODE_UPPER)) {
            return md5Str.toUpperCase();
        } else if (writeCaseMode.equals(WRITE_CASE_MODE_LOWER)) {
            return md5Str.toLowerCase();
        }
        return md5Str;

    }

    public static void main(String[] args) {
        System.out.println(encrypByMd5("18929346225").toUpperCase());
    }

}
