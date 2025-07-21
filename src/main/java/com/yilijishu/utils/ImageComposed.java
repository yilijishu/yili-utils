package com.yilijishu.utils;

import lombok.Data;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 图片构造
 */
public class ImageComposed {


    private static final String DIR = "/Users/zhangyang/Documents/images4/";
    private static final String TARGET_DIR = "/Users/zhangyang/Documents/images4/target/";
    private static final String SRC_NAME = "beijing_";
    private static final String SRC_SUFFIX = ".png";
    private static final String XING = "/Users/zhangyang/Documents/images4/wh21_37_top744_left_601.png";
    private static final int PIX = 21;

    //694
    private static final int NAME_PIX = 2;
    private static int initPng = 1; //初始化数字
    private static int initX = 694;
    private static int initY = 747;
    private static int endPng = 120; //张数

    /**
     * 加载字体
     * //第一个参数是外部字体名，第二个是字体大小
     * @param fontFileName 外部字体文件名
     * @param fontSize 字体大小
     * @return 字体
     */
    public static Font loadFont(String fontFileName, float fontSize)
    {
        try {
            File file = new File(fontFileName);
            FileInputStream aixing = new FileInputStream(file);
            Font dynamicFont = Font.createFont(Font.TRUETYPE_FONT, aixing);
            Font dynamicFontPt = dynamicFont.deriveFont(fontSize);
            aixing.close();
            return dynamicFontPt;
        } catch (Exception e)//异常处理
        {
            e.printStackTrace();
            return new Font("宋体", Font.PLAIN, 12);
        }
    }

    /**
     * 写入图片
     * 给图片添加文字
     * @param beans 文字
     */
    public static void writeImage(List<TextBean> beans) {
        for (TextBean textBean : beans) {
            FileOutputStream outImgStream = null;
            try {
                //读取原图片信息
                File srcImgFile = new File(textBean.getSrc());//得到文件
                Image srcImg = ImageIO.read(srcImgFile);//文件转化为图片
                int srcImgWidth = srcImg.getWidth(null);//获取图片的宽
                int srcImgHeight = srcImg.getHeight(null);//获取图片的高
                //添加文字:
                BufferedImage bufImg = new BufferedImage(srcImgWidth, srcImgHeight, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = bufImg.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
                g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g.drawImage(srcImg, 0, 0, srcImgWidth, srcImgHeight, null);
                for (TextBean.TextColor textColor : textBean.getTextColors()) {
                    switch (textColor.getTextColorType()) {
                        case IMG: {
                            File tmpFile = new File(textColor.getText());//得到文件
                            Image tmpImg = ImageIO.read(tmpFile);//文件转化为图片
                            g.drawImage(tmpImg, textColor.getX(), textColor.getY(), tmpImg.getWidth(null), tmpImg.getHeight(null), null);
                            break;
                        }
                        case TEXT: {
                            g.setColor(textColor.getColor());                                  //根据图片的背景设置水印颜色
                            g.setFont(textColor.getFont());                                    //设置字体
                            g.drawString(textColor.getText(), textColor.getX(), textColor.getY());   //画出水印
                            break;
                        }
                        default:
                            break;
                    }

                }
                g.dispose();
                // 输出图片
                outImgStream = new FileOutputStream(textBean.getTarget());
                ImageIO.write(bufImg, "jpg", outImgStream);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("==== 系统异常::{} ====");
            } finally {
                try {
                    if (null != outImgStream) {
                        outImgStream.flush();
                        outImgStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void main(String[] args) {
        String namesByFile = ReadFromFile.readFileByLines("/Users/zhangyang/Desktop/name");
        Random random = new Random();
        List<TextBean> list = new ArrayList<>();
        String[] names = namesByFile.split(",");
        int nameStart = 1400; //名字起始数。
        int start = 0;
        for (String name : names) {
            start++;
            // 如果起始名小于 1001 跳过
            if (start < nameStart) {
                continue;
            }
            //-- 填充路径
            TextBean textBean = new TextBean();
            int numer = random.nextInt(11);
            textBean.setSrc(DIR + SRC_NAME + numer + SRC_SUFFIX);
            textBean.setTarget(TARGET_DIR + initPng + SRC_SUFFIX);
            List<TextBean.TextColor> tmp = new ArrayList<>();
            textBean.setTextColors(tmp);
            list.add(textBean);
            //-- 填充内容
            // 填写名字
            System.out.println("name：" + name + "， 文字个数" + name.length());
            name = name.substring(1);
            // 26pt = 35px
            //for(int i = name.length() -1; i >= 0; i -- ){
            TextBean.TextColor tt = new TextBean.TextColor();
            tt.setX(initX - 35 * name.length());
            tt.setY(initY + 26);
            tt.setText(name);
            tt.setTextColorType(TextBean.TextColorType.TEXT);
            tt.setColor(new Color(152, 152, 152));
            Font font = loadFont("/Users/zhangyang/Library/Fonts/方正准圆.TTF", 40);
            tt.setFont(font);
            tmp.add(tt);
            //}
            // 填写名字完成
            //--填充星号
            TextBean.TextColor c = new TextBean.TextColor();
            tmp.add(c);
            c.setTextColorType(TextBean.TextColorType.IMG);
            c.setText(XING);
            c.setX(initX - name.length() * 35 - PIX);
            c.setY(initY - 3); // 图片top有7个像素。 initY有4个像素。所以减去3
            //c.setColor(new Color(63,63,63));
            //c.setFont(new Font("宋体", Font.PLAIN, 26));


            //---填充第一位身份证号 22px 16pt
            TextBean.TextColor c2 = new TextBean.TextColor();
            tmp.add(c2);
            c2.setTextColorType(TextBean.TextColorType.TEXT);
            c2.setText("4");
            c2.setX(662);
            c2.setY(931);
            c2.setColor(new Color(152, 152, 152));
            Font c2Font = loadFont("/Users/zhangyang/Library/Fonts/方正准圆.TTF", 38);
            c2.setFont(c2Font);

            //---填充身份证号完成

            //---填充最后一位身份证号 22px 16pt
            TextBean.TextColor c3 = new TextBean.TextColor();
            tmp.add(c3);
            c3.setTextColorType(TextBean.TextColorType.TEXT);
            c3.setText(String.valueOf(random.nextInt(10)));
            c3.setX(941);
            c3.setY(931);
            c3.setColor(new Color(152, 152, 152));
            Font c3Font = loadFont("/Users/zhangyang/Library/Fonts/方正准圆.TTF", 38);
            c3.setFont(c3Font);

            //---填充身份证号完成
            //---填充时间 22pt -- 29px
            TextBean.TextColor time = new TextBean.TextColor();
            time.setTextColorType(TextBean.TextColorType.TEXT);
            time.setText(timeRandHm());
            time.setX(493);
            time.setY(50);
            time.setColor(new Color(70, 70, 70));
            Font timeFont = loadFont("/Users/zhangyang/Library/Fonts/方正粗圆.ttf", 34);
            time.setFont(timeFont);
            tmp.add(time);

            //---填充地址。
            String text = "丁栾镇扬沙丘村";
            for (int i = 0; i < text.length(); i++) {
                TextBean.TextColor addr = new TextBean.TextColor();
                addr.setX(908 - (text.length() - i) * 37 - (text.length() - i - 1) * 2);
                addr.setY(1241);
                addr.setText(text.substring(i, i + 1));
                addr.setTextColorType(TextBean.TextColorType.TEXT);
                addr.setColor(new Color(152, 152, 152));
                Font addrFont = loadFont("/Users/zhangyang/Library/Fonts/方正准圆.TTF", 37);
                addr.setFont(addrFont);
                tmp.add(addr);
            }

            //---填充时间完成
            if (initPng == endPng) {
                break;
            }
            initPng++;
        }
        writeImage(list);
    }

    /**
     * 时间随机小时分钟
     * @return 格式化后的时间
     */
    public static String timeRandHm() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Random random = new Random();
            Date start = sdf.parse("2021-10-01 00:00:00");
            Date end = sdf.parse("2021-10-01 23:59:59");
            long tmp = start.getTime() + random.nextInt((int) (end.getTime() - start.getTime()));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");
            return simpleDateFormat.format(new Date(tmp));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }


    @Data
    public static class TextBean {
        private String src;
        private String target;
        private List<TextColor> textColors;

        public enum TextColorType {
            TEXT, IMG
        }

        @Data
        public static class TextColor {
            private TextColorType textColorType;
            private String text;
            private Color color;
            private Font font;
            private int x;
            private int y;
        }
    }

}
