package com.yilijishu.utils;

import lombok.SneakyThrows;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * RSA、SHA256工具类
 */
public class RsaSha256Util {

    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    private static final String KEY_ALGORITHM = "RSA";
    private static final int KEY_SIZE = 2048;//设置长度
    private static final String PUBLIC_KEY = "publicKey";
    private static final String PRIVATE_KEY = "privateKey";

    /**
     * 生成公、私钥
     * 根据需要返回String或byte[]类型
     *
     * @return 公私钥
     */
    private static Map<String, String> createRSAKeys() {
        Map<String, String> keyPairMap = new HashMap<>();
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            keyPairGenerator.initialize(KEY_SIZE, new SecureRandom());
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            //获取公、私钥值
            String publicKeyValue = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            String privateKeyValue = Base64.getEncoder().encodeToString(privateKey.getEncoded());

            //存入
            keyPairMap.put(PUBLIC_KEY, publicKeyValue);
            keyPairMap.put(PRIVATE_KEY, privateKeyValue);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return keyPairMap;
    }

    /**
     * 解码PublicKey
     *
     * @param key key
     * @return 公钥
     */
    public static PublicKey getPublicKey(String key) {
        try {
            byte[] byteKey = Base64.getDecoder().decode(key);
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(byteKey);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            return keyFactory.generatePublic(x509EncodedKeySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 解码PrivateKey
     *
     * @param key key
     * @return 私钥
     */
    public static PrivateKey getPrivateKey(String key) {
        try {
            byte[] byteKey = Base64.getDecoder().decode(key);
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(byteKey);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

            return keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 签名
     *
     * @param key         私钥
     * @param requestData 请求参数
     * @return 签名字符串
     */
    public static String sign(String key, String requestData) {
        String signature = null;
        byte[] signed = null;
        try {
            PrivateKey privateKey = getPrivateKey(key);

            Signature sign = Signature.getInstance(SIGNATURE_ALGORITHM);
            sign.initSign(privateKey);
            sign.update(requestData.getBytes());
            signed = sign.sign();

            signature = Base64.getEncoder().encodeToString(signed);
            System.out.println("===签名结果：" + signature);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return signature;
    }

    /**
     * 验签
     *
     * @param key         公钥
     * @param requestData 请求参数
     * @param signature   签名
     * @return 成功失败
     */
    public static boolean verifySign(String key, String requestData, String signature) {
        boolean verifySignSuccess = false;
        try {
            PublicKey publicKey = getPublicKey(key);

            Signature verifySign = Signature.getInstance(SIGNATURE_ALGORITHM);
            verifySign.initVerify(publicKey);
            verifySign.update(requestData.getBytes());

            verifySignSuccess = verifySign.verify(Base64.getDecoder().decode(signature));
            System.out.println("===验签结果：" + verifySignSuccess);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return verifySignSuccess;
    }

    public static void main(String[] args) {
        String verifySign = "12324132323123" + "\n"
                + "12324132323123" + "\n"
                + "12324132323123" + "\n"
                + "12324132323123" + "\n"
                + Base64Utils.encode(readFileByByte("/Users/zhangyang/Downloads/01eb58573d268f32f8757cb94e1be6.jpg")) + "\n";
        System.out.println(sign("MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCSQG1oUKkahEHAEEdJC3iibncvWPQt6CluDf12GY/mpZLhwO4KeW/1NA81SEyj3uH1wq3Q9vVqpampRpsvIQGHTR5yDcmbHtzPOSQJeisS5MlGcRm461ElJjz+t4mxjQb7SGTTfCNispE6gW5o7Tz0L4eUQpoMl89Ge8HxJk/bfHQ11IioU8ZDCzSStMrB+R4DVrBDdBXcGaH8losDWyyDP/+TUsX/x6QBXoa/apSLfl9mehSX4w4ap7BB+GQMsyVwT3jipsg/e1uZJK4OTeiOWpYODRDEzZSB6bl8sN/vHsnN1xLI3TuvstIGH4NNsFDrFwaQ46Vo5nqBbTb2hev5AgMBAAECggEAeQ8rgPWDhJeFdV4EI8qDCLE3ptGTDYzm/lpGO8PdAaZ8drIkWDc9HVMCY7B9AAg2Mh/7kMVbT/PhBGWVfuVrRnvnf7E5+FULodxCoDWoAfOvR8fG6vKeMIn+Yzm5mGaMKOQ70XMoN0JoRG1u3xvBIL9XEroumc3vnQBEAUEdSSfwExa/Oi0qZJC5XITwsbX2YdYhpdy9YSBH9H5rDDr1MnF+eGeFb4kTMSYLmYF3QgPvc0uSvFUX4YyXz6lSuHh39nvrihlVSRG4cMKwPmzT/Rqv44j0YLIvmIz3kDRWtvwKuUXruPxSp/glldTim6sP3Hu1QtIq8BGuAnAdeMK2oQKBgQDd3o0A7Z/spdY0yobErvwgdsmzu6s3++ed2asW0pSxwrlbrBe8rUGDLrmgYPRk0Agv1G8JvUQffECSWkVgUAw/s/f6Vlw3G2gARR3fPXrvYy25knr2mQRMZ6PEHVDW3XGw8wNBRqMc8mxiILupRyipBOHEM527jQp3wt3LtSKrOwKBgQCov/kIHxDZy3BucppPqQXv9YTtwBTPPOOPr13r94Oc7izI7fXFS/ZHNOh+Beh1tYT6V9B/W9k9ait/xwAOaZTP2Bv8nPR00K9X1+Gnlef9dpxvKON/Q7pn4JqGDbxYcmxElby6Xx3wgo6Eq2P2fQIlbbkLjfoA8+x62oGGxdxKWwKBgE51zVdusQd2/fFdTZtwZij02Q9+ZmY95wKjXyWLApp6SiJpEVvfyTMi6vIUV1jvWHXRohLHo7eesRcQZUhSBoLgmeklcSbrwjOWPaIck8TkRd9CNcnPAv4iCH/p/hR96x6wUY5b9ILgGAP7n2GVkIN0/oHr3vPcRL9lYhoPzFrRAoGAC9GWMXlrhrV25GVmZVnjLpE1hClFy9Xp4jBYFaS6NQ2BWXlBE1Zko0wTVHA5fbT+4UGxOppGNsTu0v4vBNNkNsJeA21CucO75B0cks4u91ONVUO52zn1Hj1e1TC1FkS9THhoRW1P/X5HWQdf8u2CbvimUybYcaSj1V7kX3oQGa0CgYAJHmyHMWsAGUESSzMZeaR3Ye5DldQJD1jw3PObqfxcxWfFjdBpJPikqDYcWXIlmKFY7MioiQTGDnEOSHm2tvB7NcnbGjSfsTkp4Y3XDRWlXw5Z5sPk5jRn1ZDtaJcwrD7SPeQrFLhjQyHYSczECAyltdhsLCs+i/7O8hnyZrCeOw==", verifySign));
        System.out.println(verifySign("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkkBtaFCpGoRBwBBHSQt4om53L1j0Legpbg39dhmP5qWS4cDuCnlv9TQPNUhMo97h9cKt0Pb1aqWpqUabLyEBh00ecg3Jmx7czzkkCXorEuTJRnEZuOtRJSY8/reJsY0G+0hk03wjYrKROoFuaO089C+HlEKaDJfPRnvB8SZP23x0NdSIqFPGQws0krTKwfkeA1awQ3QV3Bmh/JaLA1ssgz//k1LF/8ekAV6Gv2qUi35fZnoUl+MOGqewQfhkDLMlcE944qbIP3tbmSSuDk3ojlqWDg0QxM2Ugem5fLDf7x7JzdcSyN07r7LSBh+DTbBQ6xcGkOOlaOZ6gW029oXr+QIDAQAB",
                verifySign, "a3QmkrKQiPrvJ7SKXs4Ubvb2BEdrv71+a5ufNOd/RylF0E6EksdLdcZZO1phZhbssJE3zio/KR5Lb/wjIX0PIqJmOw+HuwbrJa+bwZ912twmkWaieNBdfV5ZaSsJzRR44ZDmHeWSmG4lLoFKgsPEoaQwmEd295RxsuxX+Juw2DVLxGaDgQy9bb5IXyTHhAj80Sm2v8xLh/tEJNsxCcK7WFN9oJdTaCtlL2lUvWscSwul//kEW1+CJ2+7oOKV+3VhrW4xGNY8RHrWl+7n3lRspUZTF7425QJYEOX5rPEQaNKkpZ74mv64H7OF2Krkf9XKsjCbPATlWD8LiFDe2rd94g=="));


    }

    /**
     * 文件读取
     * @param fromFile 文件名
     * @return 字节数组
     */
    @SneakyThrows
    public static byte[] readFileByByte(String fromFile) {
        File file = new File(fromFile);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
        byte[] result = bufferedInputStream.readAllBytes();
        return result;
    }

}
