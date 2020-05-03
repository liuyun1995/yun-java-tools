package com.liuyun.github.utils;

import lombok.extern.slf4j.Slf4j;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @Author: liuyun18
 * @Date: 2018/12/24 下午1:59
 */
@Slf4j
public class ImageUtil {

    /**
     * 图像转字节数组
     * @param bImage
     * @param format
     * @return
     */
    public static byte[] imageToBytes(BufferedImage bImage, String format) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(bImage, format, out);
        } catch (IOException e) {
            log.error("将图像转成字节数组出现错误", e);
        }
        return out.toByteArray();
    }

    /**
     * 字节数组转图像
     * @param bytes
     * @return
     */
    public static Image bytesToImage(byte[] bytes) {
        Image image = Toolkit.getDefaultToolkit().createImage(bytes);
        MediaTracker mt = new MediaTracker(new Label());
        mt.addImage(image, 0);
        try {
            mt.waitForAll();
        } catch (InterruptedException e) {
            log.error("将字节数组转成图像出现错误", e);
        }
        return image;
    }

}
