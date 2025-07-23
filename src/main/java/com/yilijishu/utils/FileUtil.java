package com.yilijishu.utils;

import com.yilijishu.utils.exceptions.BizException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by yilijishu on 16/11/30.
 */
@Slf4j
public class FileUtil {

    /**
     * Base64解码、
     *
     * @param baseCode base64 码
     * @return 返回字节数组
     */
    public static byte[] base64Upload(String baseCode) {
        return Base64.getDecoder().decode(baseCode);
    }


    /**
     * 文件上传
     *
     * @param bytes    字节码
     * @param filePath 文件路径
     */
    public static void fileUpload(byte[] bytes, String filePath) throws BizException {
        File file = new File(filePath);
        mkdir(file);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bytes);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            throw new BizException("找不到文件");
        } catch (IOException e) {
            throw new BizException("文件读写错误");
        }
    }

    /**
     * 文件下载后上传
     *
     * @param urlString url地址
     * @param savePath  保存的本地路径
     * @throws IOException IO错误
     */
    public static void download(String urlString, String savePath) throws IOException {
        File file = new File(savePath);
        mkdir(file);
        // 构造URL
        URL url = new URL(urlString);
        // 打开连接
        URLConnection con = url.openConnection();
        //设置请求超时为20s
        con.setConnectTimeout(20 * 1000);
        //jdk 1.7 新特性自动关闭
        try (InputStream in = con.getInputStream();
             OutputStream out = new FileOutputStream(savePath)) {
            //创建缓冲区
            byte[] buff = new byte[1024];
            int n;
            // 开始读取
            while ((n = in.read(buff)) >= 0) {
                out.write(buff, 0, n);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 文件下载
     *
     * @param outputStream 下载流
     * @param filePath     文件路径
     * @throws BizException 抛出通用错误
     */
    public static void fileDownload(OutputStream outputStream, String filePath) throws BizException {
        File file = new File(filePath);
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] bytes = new byte[1024];
            int read = 0;
            while ((read = fileInputStream.read(bytes)) > 0) {
                outputStream.write(bytes, 0, read);
            }
            outputStream.flush();
            outputStream.close();
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            throw new BizException("找不到文件");
        } catch (IOException e) {
            throw new BizException("文件读写错误");
        }
    }

    /**
     * 压缩、水印。
     * 优先以width进行等比压缩。
     * 如果width ＝ 0 走height压缩
     * 如果width ＝ 0 并且 height ＝ 0 不压缩。
     *
     * @param bytes     图片字节码
     * @param width     宽度
     * @param height    高度
     * @param filePath  文件路径
     * @param watermark 是否水印
     * @param text      文本
     * @throws BizException 抛出自定义错误
     */
    public static void compressAndWatermark(byte[] bytes, int width, int height, String filePath, boolean watermark, String text) throws BizException {

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Map<String, Object> map = new HashMap<>();
        try {
            Image image = ImageIO.read(byteArrayInputStream);
            String suffix = filePath.substring(filePath.lastIndexOf(".") + 1).toLowerCase();
            BufferedImage buffImg = null;
            if ("png".equals(suffix)) {
                buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            } else {
                buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            }
            Graphics2D graphics = buffImg.createGraphics();
            graphics.setBackground(new Color(255, 255, 255));
            graphics.setColor(new Color(255, 255, 255));
            graphics.fillRect(0, 0, width, height);
            graphics.drawImage(image.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);

            //-----水印
            if (watermark) {
                //---文字水印
                int x = width - 10 * 5;
                int y = height - 10 * 2;
                int x1 = width - 10 * 7;
                int y1 = height - 10;
                graphics.setColor(Color.BLACK);
                graphics.setFont(new Font("Serif", Font.PLAIN, 10));
                graphics.drawString(text, x, y);
                //graphics.drawString(StaticContants.WATERMARK_TEXT_URL, x1, y1);

                //-----图片水印
//                Image img = ImageIO.read(FileUtil.class.getResourceAsStream(""));
//                float alpha = 0.5f; // 透明度
//                graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
//                int y2 = height - img.getHeight(null);
//                int x2 = width - img.getWidth(null);
//                graphics.drawImage(img, x2, y2, null);
//                graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
//                graphics.dispose();

            }
            //-----水印结束

            ImageIO.write(buffImg, suffix, new File(filePath));

        } catch (IOException e) {
            log.error("图片压缩出现错误", e);
            throw new BizException("图片压缩出现错误");
        }
    }

    /**
     * 压缩并且水印
     *
     * @param bytes     字节码
     * @param width     宽度
     * @param height    高度
     * @param watermark 水印
     * @param text      水印文字
     * @return 返回压缩后的字节码
     */
    public static byte[] compressAndWatermark(byte[] bytes, int width, int height, boolean watermark, String text) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        try {
            Image image = ImageIO.read(byteArrayInputStream);

            BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = buffImg.createGraphics();
            graphics.setBackground(new Color(255, 255, 255));
            graphics.setColor(new Color(255, 255, 255));
            graphics.fillRect(0, 0, width, height);

            graphics.drawImage(image.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
            //-----水印
            if (watermark) {
                int y = height - 10 * 2;
                int x = width - 10 * 5;
                int x1 = width - 10 * 7;
                int y1 = height - 10;
                graphics.setColor(Color.BLACK);
                graphics.setFont(new Font("Serif", Font.PLAIN, 10));
                graphics.drawString(text, x, y);
//                graphics.drawString(StaticContants.WATERMARK_TEXT_URL, x1, y1);

                //-----图片水印
//                Image img = ImageIO.read(FileUtil.class.getResourceAsStream(StaticContants.LOGO_NAME));
//                float alpha = 0.5f; // 透明度
//                graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
//                int x2 = width - img.getWidth(null);
//                int y2 = height - img.getHeight(null);
//                graphics.drawImage(img, x2, y2, null);
//                graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
//                graphics.dispose();
            }
            //-----水印结束
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(buffImg, "jpeg", out);
            byte[] result = out.toByteArray();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 压缩
     *
     * @param bytes     字节码
     * @param width     宽度
     * @param height    高度
     * @param filePath  文件路径
     * @param watermark 水印
     * @param text      文字
     * @return 压缩后的字段 map
     * @throws BizException 抛出自定义错误
     */
    public static Map<String, Object> compress(byte[] bytes, int width, int height, String filePath, boolean watermark, String text) throws BizException {
        Map<String, Object> map = new HashMap<>();
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            Image image = ImageIO.read(byteArrayInputStream);
            int w = image.getWidth(null);
            int h = image.getHeight(null);
            map.put("orgWidthPixel", w);
            map.put("orgHeightPixel", h);
            double bili = 0D;
            boolean isZip = true;
            if ((width == 0 && height == 0) || width > w || height > h) {
                height = h;
                width = w;
                isZip = false;
            } else {
                if (width > 0) {
                    bili = width / (double) w;
                    height = (int) (h * bili);
                } else {
                    if (height > 0) {
                        bili = height / (double) h;
                        width = (int) (w * bili);
                    }
                }
            }
            map.put("imageWidthPixel", width);
            map.put("imageHeightPixel", height);
            if (!isZip && !watermark) {
                fileUpload(bytes, filePath);
            } else {
                String suffix = filePath.substring(filePath.lastIndexOf(".") + 1).toLowerCase();
                if ("gif".equals(suffix)) {
                    fileUpload(bytes, filePath);
                } else {
                    compressAndWatermark(bytes, width, height, filePath, watermark, text);
                }
            }
            return map;
        } catch (Exception e) {
            log.error("压缩出现错误", e);
            throw new BizException("压缩错误问题." + e.getMessage());
        }

    }


    /**
     * 给出基础路径，且文件后缀。创建文件，并返回文件路径
     *
     * @param basePath 路径
     * @param suffix   后缀
     * @return 文件路径
     */
    public static String getRelativePath(String basePath, String suffix) {
        String relativePath = getDatePath(null);
        String filePath = CompareUtil.concatUrl(basePath, relativePath);
        String fileName = getFileName(suffix);
        File file = new File(filePath);
        mkdir(file, false);
        return CompareUtil.concatUrl(relativePath, fileName);
    }

    /**
     * 按照后缀生成随即文件名
     *
     * @param suffix 后缀
     * @return 返回文件名
     */
    public static String getFileName(String suffix) {
        long time = new Date().getTime();
        String timeFile = String.valueOf(time).substring(3);
        Random random = new Random();
        String fileName = timeFile.
                concat(String.valueOf(random.nextInt(10))).concat(String.valueOf(random.nextInt(10)))
                .concat(".").concat(suffix.startsWith(".") ? suffix.substring(1) : suffix);
        return fileName;
    }

    /**
     * 获取日期路径
     *
     * @param createTime 创建时间
     * @return 返回日期路径
     */
    public static String getDatePath(Date createTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String relativePath = "";
        if (createTime != null) {
            relativePath = sdf.format(createTime).concat("/");

        } else {
            Calendar calendar = Calendar.getInstance();
            relativePath = sdf.format(calendar.getTime()).concat("/");
        }
        return relativePath;
    }

    /**
     * 创建文件夹
     * @param file 文件夹路径
     */
    public static void mkdir(File file) {
        mkdir(file, false);
    }
    /**
     * 创建文件夹,默认的时候认为是文件
     *
     * @param file   文件
     * @param isFile true 文件 false 文件夹
     */
    public static void mkdir(File file, boolean isFile) {
        if (isFile) {
            File tmp = new File(file.getParent());
            if (!tmp.exists()) {
                tmp.mkdirs();
            }
        } else {
            if (!file.exists()) {
                file.mkdirs();
            }
        }
    }

    /**
     * 分页获取文件名，(横杠版)
     *
     * @param fileName 必须事XXX.xxx格式
     * @param page     页数
     * @return 返回页数文件名
     */
    public static String getFileNameByPage(String fileName, Integer page) {
        if (page != null && page > 1) {
            String[] arr = fileName.split("\\.");
            fileName = arr[0].concat("-").concat(String.valueOf(page)).concat(".").concat(arr[1]);
        }
        return fileName;
    }

    /**
     * 获取文件名前缀
     *
     * @param fileName 获取文件名的前缀名
     * @return 前缀名
     */
    public static String getFileNamePrefix(String fileName) {
        String tmp = null;
        if (CompareUtil.isNotBlank(fileName) && fileName.indexOf(".") > -1) {
            String[] arr = fileName.split("\\.");
            tmp = arr[0];
            if (tmp.indexOf("/") > -1) {
                tmp = tmp.substring(tmp.lastIndexOf("/") + 1);
            } else if (tmp.indexOf("\\") > -1) {
                tmp = tmp.substring(tmp.lastIndexOf("\\") + 1);
            }
            if (tmp.indexOf("-") > -1) {
                tmp = tmp.substring(0, tmp.indexOf("-"));
            }
        }
        return tmp;
    }

    /**
     * 获取文件名后缀
     *
     * @param fileName 获取文件名的后缀
     * @return 后缀名
     */
    public static String getFileNameSuffix(String fileName) {
        if (CompareUtil.isNotBlank(fileName) && fileName.indexOf(".") > -1) {
            String[] arr = fileName.split("\\.");
            return arr[1];
        }
        return null;
    }

    /**
     * 分页获取文件名，（下划线版）
     *
     * @param fileName 文件名
     * @param page     页数
     * @return 带分页的文件名。
     */
    public static String getFileNameByPageLine(String fileName, Integer page) {
        if (page != null && page > 1) {
            String[] arr = fileName.split("\\.");
            fileName = arr[0].concat("_").concat(String.valueOf(page)).concat(".").concat(arr[1]);
        }
        return fileName;
    }

    /**
     * 文件拷贝
     *
     * @param fromFile 来源文件
     * @param toFile   存储文件
     */
    public static void copyFile(String fromFile, String toFile) {
        try {
            FileReader fileReader = new FileReader(new File(fromFile));
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuffer stringBuffer = new StringBuffer();
            String tmp;
            while ((tmp = bufferedReader.readLine()) != null) {
                stringBuffer.append(tmp);
            }

            File file = new File(toFile);
            mkdir(file);
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(stringBuffer.toString());
            fileWriter.flush();
            fileWriter.close();
        } catch (FileNotFoundException e) {
            log.error("找不到文件", e);
        } catch (IOException e) {
            log.error("文件读写失败", e);
        }
    }

    /**
     * 文件写入.文本写入
     *
     * @param content 内容
     * @param toFile  存入文件
     */
    public static void writeFile(String content, String toFile) {

        try {
            File file = new File(toFile);
            mkdir(file, true);
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            log.error("文件读写失败", e);
        }
    }

    /**
     * 文件写入.文本写入
     *
     * @param content 内容
     * @param toFile  存入文件
     */
    public static void writeFileWhenFileNotFound(String content, String toFile) {

        try {
            File file = new File(toFile);
            if(!file.exists()) {
                mkdir(file, true);
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(content);
                fileWriter.flush();
                fileWriter.close();
            }
        } catch (IOException e) {
            log.error("文件读写失败", e);
        }
    }

    /**
     * 文件读取（文本读取）
     *
     * @param fromFile 文件名
     * @return 返回字符串
     */
    public static String readFile(String fromFile) {
        try {
            FileReader fileReader = new FileReader(new File(fromFile));
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuffer stringBuffer = new StringBuffer();
            String tmp;
            while ((tmp = bufferedReader.readLine()) != null) {
                stringBuffer.append(tmp);
            }
            return stringBuffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从文件中读取二进制数据
     *
     * @param fromFile 文件
     * @return 返回二进制数组
     */
    @SneakyThrows
    public static byte[] readFileByByte(String fromFile) {
        File file = new File(fromFile);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
        byte[] result = bufferedInputStream.readAllBytes();
        bufferedInputStream.close();
        return result;
    }

    /**
     * 写入二进制数据
     *
     * @param bytes    字节数组
     * @param fromFile 文件
     */
    @SneakyThrows
    public static void writeFileByByte(byte[] bytes, String fromFile) {
        File file = new File(fromFile);
        mkdir(file);
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
    }


    /**
     * 给域名添加后缀 ／
     *
     * @param url url
     * @return 添加好后缀/的url
     */
    public static String addSuffix(String url) {
        if (CompareUtil.isNotBlank(url) && !url.endsWith("/") && !url.endsWith("\\")) {
            url = url + "/";
        }
        return url;
    }

    /**
     * 去掉相对路径的前缀 /
     *
     * @param url url
     * @return 去掉斜杠的url
     */
    public static String delPrefix(String url) {
        if (CompareUtil.isNotBlank(url) && (url.startsWith("/") || url.startsWith("\\"))) {
            url = url.substring(1);
        }
        return url;
    }

}
