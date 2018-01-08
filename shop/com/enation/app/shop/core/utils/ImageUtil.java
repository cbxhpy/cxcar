package com.enation.app.shop.core.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
  
/** 
 * @author yexf 
 * @date 2016-05-12
 * 图片压缩工具类 提供的方法中可以设定生成的 缩略图片的大小尺寸、压缩尺寸的比例、图片的质量等 
 */  
public class ImageUtil {  
  
    /** 
     * * 图片文件读取 
     *  
     * @param srcImgPath 
     * @return 
     */  
    private static BufferedImage InputImage(String srcImgPath) {  
  
        BufferedImage srcImage = null;  
        try {  
            // 构造BufferedImage对象  
            File file = new File(srcImgPath);  
            FileInputStream in = new FileInputStream(file);  
            byte[] b = new byte[5];  
            in.read(b);  
            srcImage = javax.imageio.ImageIO.read(file);  
        } catch (IOException e) {  
            System.out.println("读取图片文件出错！" + e.getMessage());  
            e.printStackTrace();  
        }  
        return srcImage;  
    }  
  
    /** 
     * * 将图片按照指定的图片尺寸、源图片质量压缩(默认质量为1) 
     *  
     * @param srcImgPath 
     *            :源图片路径 
     * @param outImgPath 
     *            :输出的压缩图片的路径 
     * @param new_w 
     *            :压缩后的图片宽 
     * @param new_h 
     *            :压缩后的图片高 
     */  
    public static void Tosmallerpic(String srcImgPath, String outImgPath,  
            int new_w, int new_h) {  
        Tosmallerpic(srcImgPath, outImgPath, new_w, new_h, 1F);  
    }  
  
    /** 
     * 将图片按照指定的尺寸比例、源图片质量压缩(默认质量为1) 
     *  
     * @param srcImgPath 
     *            :源图片路径 
     * @param outImgPath 
     *            :输出的压缩图片的路径 
     * @param ratio 
     *            :压缩后的图片尺寸比例 
     * @param per 
     *            :百分比 
     */  
    public static void Tosmallerpic(String srcImgPath, String outImgPath,  
            float ratio) {  
        Tosmallerpic(srcImgPath, outImgPath, ratio, 1F);  
    }  
  
    /** 
     * 将图片按照指定长或者宽的最大值来压缩图片(默认质量为1) 
     *  
     * @param srcImgPath 
     *            :源图片路径 
     * @param outImgPath 
     *            :输出的压缩图片的路径 
     * @param maxLength 
     *            :长或者宽的最大值 
     * @param per 
     *            :图片质量 
     */  
    public static void Tosmallerpic(String srcImgPath, String outImgPath,  
            int maxLength) {  
        Tosmallerpic(srcImgPath, outImgPath, maxLength, 1F);  
    }  
  
    /** 
     * * 将图片按照指定的图片尺寸、图片质量压缩 
     *  
     * @param srcImgPath 
     *            :源图片路径 
     * @param outImgPath 
     *            :输出的压缩图片的路径 
     * @param new_w 
     *            :压缩后的图片宽 
     * @param new_h 
     *            :压缩后的图片高 
     * @param per 
     *            :百分比 
     */  
    public static void Tosmallerpic(String srcImgPath, String outImgPath,  
            int new_w, int new_h, float per) {  
        // 得到图片  
        BufferedImage src = InputImage(srcImgPath);  
        int old_w = src.getWidth();  
        // 得到源图宽  
        int old_h = src.getHeight();  
        // 得到源图长  
        // 根据原图的大小生成空白画布  
        BufferedImage tempImg = new BufferedImage(old_w, old_h,  
                BufferedImage.TYPE_INT_RGB);  
        // 在新的画布上生成原图的缩略图  
        Graphics2D g = tempImg.createGraphics();  
        g.setColor(Color.white);  
        g.fillRect(0, 0, old_w, old_h);  
        g.drawImage(src, 0, 0, old_w, old_h, Color.white, null);  
        g.dispose();  
        BufferedImage newImg = new BufferedImage(new_w, new_h,  
                BufferedImage.TYPE_INT_RGB);  
        newImg.getGraphics().drawImage(  
                tempImg.getScaledInstance(new_w, new_h, Image.SCALE_SMOOTH), 0,  
                0, null);  
        // 调用方法输出图片文件  
        OutImage(outImgPath, newImg, per);  
    }  
  
    /** 
     * * 将图片按照指定的尺寸比例、图片质量压缩 
     *  
     * @param srcImgPath 
     *            :源图片路径 
     * @param outImgPath 
     *            :输出的压缩图片的路径 
     * @param ratio 
     *            :压缩后的图片尺寸比例 
     * @param per 
     *            :百分比 
     */  
    public static void Tosmallerpic(String srcImgPath, String outImgPath,  
            float ratio, float per) {  
        // 得到图片  
        BufferedImage src = InputImage(srcImgPath);  
        int old_w = src.getWidth();  
        // 得到源图宽  
        int old_h = src.getHeight();  
        // 得到源图长  
        int new_w = 0;  
        // 新图的宽  
        int new_h = 0;  
        // 新图的长  
        BufferedImage tempImg = new BufferedImage(old_w, old_h,  
                BufferedImage.TYPE_INT_RGB);  
        Graphics2D g = tempImg.createGraphics();  
        g.setColor(Color.white);  
        // 从原图上取颜色绘制新图g.fillRect(0, 0, old_w, old_h);  
        g.drawImage(src, 0, 0, old_w, old_h, Color.white, null);  
        g.dispose();  
        // 根据图片尺寸压缩比得到新图的尺寸new_w = (int) Math.round(old_w * ratio);
        new_w = Math.round(old_w * ratio); 
        new_h = Math.round(old_h * ratio);  
        BufferedImage newImg = new BufferedImage(new_w, new_h,  
                BufferedImage.TYPE_INT_RGB);  
        newImg.getGraphics().drawImage(  
                tempImg.getScaledInstance(new_w, new_h, Image.SCALE_SMOOTH), 0,  
                0, null);  
        // 调用方法输出图片文件OutImage(outImgPath, newImg, per);  
    }  
  
    /** 
     * * 指定长或者宽的最大值来压缩图片 
     *  
     * @param srcImgPath 
     *            :源图片路径 
     * @param outImgPath 
     *            :输出的压缩图片的路径 
     * @param maxLength 
     *            :长或者宽的最大值 
     * @param per 
     *            :图片质量 
     */  
    public static void Tosmallerpic(String srcImgPath, String outImgPath,  
            int maxLength, float per) {  
        // 得到图片  
        BufferedImage src = InputImage(srcImgPath);  
        int old_w = src.getWidth();  
        // 得到源图宽  
        int old_h = src.getHeight();  
        // 得到源图长  
        int new_w = 0;  
        // 新图的宽  
        int new_h = 0;  
        // 新图的长  
        BufferedImage tempImg = new BufferedImage(old_w, old_h,  
                BufferedImage.TYPE_INT_RGB);  
        Graphics2D g = tempImg.createGraphics();  
        g.setColor(Color.white);  
        // 从原图上取颜色绘制新图  
        g.fillRect(0, 0, old_w, old_h);  
        g.drawImage(src, 0, 0, old_w, old_h, Color.white, null);  
        g.dispose();  
        // 根据图片尺寸压缩比得到新图的尺寸  
        if (old_w > old_h) {  
            // 图片要缩放的比例  
            new_w = maxLength;  
            new_h = Math.round(old_h * ((float) maxLength / old_w));  
        } else {  
            new_w = Math.round(old_w * ((float) maxLength / old_h));  
            new_h = maxLength;  
        }  
        BufferedImage newImg = new BufferedImage(new_w, new_h,  
                BufferedImage.TYPE_INT_RGB);  
        newImg.getGraphics().drawImage(  
                tempImg.getScaledInstance(new_w, new_h, Image.SCALE_SMOOTH), 0,  
                0, null);  
        // 调用方法输出图片文件  
        OutImage(outImgPath, newImg, per);  
    }  
  
    /** 
     * * 将图片文件输出到指定的路径，并可设定压缩质量 
     *  
     * @param outImgPath 
     * @param newImg 
     * @param per 
     */  
    private static void OutImage(String outImgPath, BufferedImage newImg,  
            float per) {  
        // 判断输出的文件夹路径是否存在，不存在则创建  
        File file = new File(outImgPath);  
        if (!file.getParentFile().exists()) {  
            file.getParentFile().mkdirs();  
        }// 输出到文件流  
        try {  
            FileOutputStream newimage = new FileOutputStream(outImgPath);  
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(newimage);  
            JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(newImg);  
            // 压缩质量  
            jep.setQuality(per, true);  
            encoder.encode(newImg, jep);  
            newimage.close();  
        } catch (FileNotFoundException e) {  
            // TODO Auto-generated catch blocke.printStackTrace();  
        } catch (ImageFormatException e) {  
            // TODO Auto-generated catch blocke.printStackTrace();  
        } catch (IOException e) {  
            // TODO Auto-generated catch blocke.printStackTrace();  
        }  
    }  
  
    public static void main(String args[]) {  
        String f = "c:/img/";  
        File file = new File(f);  
        if (file.exists()) {  
            File[] filelist = file.listFiles();  
            for (int i = 0; i < filelist.length; i++) {  
                File fi = filelist[i];  
                System.out.println(fi.length());  
                String n = filelist[i].getName();  
                // Tosmallerpic(f, filelist[i], "_ratio_small", n,  
                // 0.303,(float)0.7);  
                // Tosmallerpic(f, filelist[i], "_ratio_smaller", n,  
                // 0.083,(float)0.7);  
            }  
        }  
        String srcImg = "C:/Users/Administrator/Desktop/11.png";  
        String tarDir = "C:/Users/Administrator/Desktop/";  
        long startTime = new Date().getTime();  
        /*Tosmallerpic(srcImg, tarDir + "car_1_maxLength_1.jpg", 400);  
        Tosmallerpic(srcImg, tarDir + "car_1_maxLength_2.jpg", 0.5F);  
        Tosmallerpic(srcImg, tarDir + "car_1_maxLength_3.jpg", 400, 500);  
        Tosmallerpic(srcImg, tarDir + "car_1_maxLength_11.jpg", 400, 0.7F);  
        Tosmallerpic(srcImg, tarDir + "car_1_maxLength_22.jpg", 0.5F, 0.8F); */ 
        Tosmallerpic(srcImg, tarDir + "car_1_maxLength_55.png", 300, 200, 1F);
        Tosmallerpic(srcImg, tarDir + "car_1_maxLength_33.png", 300, 200, 0.8F);
        Tosmallerpic(srcImg, tarDir + "car_1_maxLength_44.png", 300, 200, 0.1F);
        System.out.println(new Date().getTime() - startTime);  
    }  
}  