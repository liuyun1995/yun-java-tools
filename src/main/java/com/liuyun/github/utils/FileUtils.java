package com.liuyun.github.utils;

import com.google.common.io.Resources;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

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

    public static String getFileString(String path) {
        try {
            List<String> lines = Resources.readLines(Resources.getResource(path), Charset.forName("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < lines.size(); i++) {
                sb.append(lines.get(i)).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

}
