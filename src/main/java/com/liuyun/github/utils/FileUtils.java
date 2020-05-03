package com.liuyun.github.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @Author: liuyun18
 * @Date: 2018/8/30 上午9:53
 */
@Slf4j
public class FileUtils {

    /**
     * 根据路径创建新文件
     * @param filePath
     * @return
     */
    public static File newFile(String filePath) {
        File file = new File(filePath);
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                log.error("新建文件出现错误", e);
            }
        }
        return file;
    }

    public static boolean writeBytes(byte[] data, String path) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            fos.write(data);
            fos.flush();
            return true;
        } catch (Exception e) {
            log.error("写入文件出错", e);
            return false;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    log.error("关闭文件流出错", e);
                }
            }
        }
    }

}
