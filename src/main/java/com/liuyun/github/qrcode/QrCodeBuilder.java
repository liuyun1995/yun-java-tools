package com.liuyun.github.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Base64OutputStream;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: liuyun18
 * @Date: 2019/3/7 下午6:43
 */
@Slf4j
public class QrCodeBuilder {

    /** 默认编码 */
    private static final String DEFAULT_CHARSET = "UTF-8";
    /** 默认边距 */
    private static final int DEFAULT_MARGIN = 0;
    /** 默认宽度 */
    private static final int DEFAULT_WIDTH = 250;
    /** 默认高度 */
    private static final int DEFAULT_HEIGHT = 250;
    /** 默认黑色 */
    private static final int BLACK_COLOR = 0xFF000000;
    /** 默认白色 */
    private static final int WHITE_COLOR = 0xFFFFFFFF;

    /** 文本数据 */
    private String data;
    /** 字符编码 */
    private String charset;
    /** 二维码宽度 */
    private Integer width;
    /** 二维码高度 */
    private Integer height;
    /** logo路径 */
    private String logoUrl;
    /** 配置信息 */
    private Map<EncodeHintType, Object> hints = new HashMap(16);

    /**
     * 静态工厂方法
     * @return
     */
    public static QrCodeBuilder instance() {
        return new QrCodeBuilder();
    }

    /**
     * 设置文本数据
     * @param data
     * @return
     */
    public QrCodeBuilder setData(String data) {
        this.data = data;
        return this;
    }

    /**
     * 设置字符编码
     * @param charset
     * @return
     */
    public QrCodeBuilder setCharset(String charset) {
        this.charset = charset;
        this.hints.put(EncodeHintType.CHARACTER_SET, charset);
        return this;
    }

    /**
     * 设置二维码宽度
     * @param width
     * @return
     */
    public QrCodeBuilder setWidth(int width) {
        this.width = width;
        return this;
    }

    /**
     * 设置二维码高度
     * @param height
     * @return
     */
    public QrCodeBuilder setHeight(int height) {
        this.height = height;
        return this;
    }

    /**
     * 设置二维码logo
     * @param logo
     * @return
     */
    public QrCodeBuilder setLogo(String logo) {
        this.logoUrl = logo;
        return this;
    }

    /**
     * 设置白边边距
     * @param margin
     * @return
     */
    public QrCodeBuilder setMargin(int margin) {
        this.hints.put(EncodeHintType.MARGIN, margin);
        return this;
    }

    /**
     * 生成二维码图片
     * @return
     */
    public BufferedImage createQRCode() {
        try {
            //设置默认值
            setDefaultValue();
            //获取比特矩阵
            BitMatrix matrix = new MultiFormatWriter().encode(new String(data.getBytes(charset), charset),
                    BarcodeFormat.QR_CODE, width, height, hints);
            //获取二维码图片
            BufferedImage qrcode = toBufferedImage(matrix);
            //若设置了logo图片
            if(logoUrl != null) {
                BufferedImage logo = buildBufferedImage(logoUrl);
                int x = (qrcode.getWidth() - logo.getWidth()) / 2;
                int y = (qrcode.getHeight() - logo.getHeight()) / 2;
                Graphics2D g = qrcode.createGraphics();
                g.drawImage(logo.getScaledInstance(logo.getWidth(), logo.getHeight(), Image.SCALE_SMOOTH), x, y, null);
                g.dispose();
            }
            return qrcode;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 设置默认值
     */
    private void setDefaultValue() {
        if(this.data == null) {
            throw new RuntimeException("必须输入文本信息");
        }
        if(this.charset == null) {
            this.charset = DEFAULT_CHARSET;
        }
        if(this.width == null) {
            this.width = DEFAULT_WIDTH;
        }
        if(this.height == null) {
            this.height = DEFAULT_HEIGHT;
        }
        if(!this.hints.containsKey(EncodeHintType.ERROR_CORRECTION)) {
            this.hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        }
        if(!this.hints.containsKey(EncodeHintType.CHARACTER_SET)) {
            this.hints.put(EncodeHintType.CHARACTER_SET, DEFAULT_CHARSET);
        }
        if(!this.hints.containsKey(EncodeHintType.MARGIN)) {
            this.hints.put(EncodeHintType.MARGIN, DEFAULT_MARGIN);
        }
    }

    /**
     * 根据路径获取图片
     * @param path
     * @return
     */
    private static BufferedImage buildBufferedImage(String path) {
        try {
            if(path.startsWith("http://") || path.startsWith("https://")) {
                return ImageIO.read(new URL(path));
            }
            return ImageIO.read(new File(path));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("获取logo资源错误");
        }
    }

    /**
     * 绘制二维码图像
     * @param matrix
     * @return
     */
    public static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? BLACK_COLOR : WHITE_COLOR);
            }
        }
        return image;
    }

    /**
     * 将图像转为base64字符串
     * @param image
     * @return
     */
    public static String imageToBase64(BufferedImage image) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            OutputStream b64 = new Base64OutputStream(os);
            ImageIO.write(image, "png", b64);
            return os.toString("UTF-8");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 将图像转为base64字符串
     * @param path
     * @return
     */
    public static String imageToBase64(String path) {
        try {
            if(path == null) { return null; }
            //根据路径获取图片
            BufferedImage image = buildBufferedImage(path);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            OutputStream b64 = new Base64OutputStream(os);
            ImageIO.write(image, "png", b64);
            return os.toString("UTF-8");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 将base64字符串解码为图像
     * @param base64ImageString
     * @param file
     */
    public static void base64ToImage(String base64ImageString, File file) throws IOException {
        FileOutputStream os = null;
        try {
            Base64 d = new Base64();
            byte[] bs = d.decode(base64ImageString);
            os = new FileOutputStream(file.getAbsolutePath());
            os.write(bs);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if(os != null) { os.close(); }
        }
    }

}
