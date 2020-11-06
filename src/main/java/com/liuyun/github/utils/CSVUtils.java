package com.liuyun.github.utils;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CSVUtils {

    private static List<String[]> dataList = Lists.newArrayList();

    /**
     * 读取CSV文件
     * @param filePath
     * @return
     */
    public static List<String[]> readCSV(String filePath) {
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader reader = null;
        try{
            File csv = newFile(filePath);
            fis = new FileInputStream(csv);
            isr = new InputStreamReader(fis, "UTF-8");
            reader = new BufferedReader(isr);
            String line;
            while ((line = reader.readLine()) != null) {
                String[] s = line.split("\t");
                dataList.add(s);
            }
            return dataList;
        } catch (Exception e) {
            log.error("读取CSV文件异常!", e);
        } finally {
            close(reader, isr, fis);
        }
        return new ArrayList(0);
    }

    /**
     * 写入CSV文件
     * @param filePath
     * @param dataList
     * @return
     */
    public static boolean writeCSV(String filePath, List<List<String>> dataList){
        FileOutputStream out= null;
        OutputStreamWriter osw = null;
        BufferedWriter bfw= null;
        try {
            File file = newFile(filePath);
            out = new FileOutputStream(file);
            osw = new OutputStreamWriter(out, "UTF-8");
            bfw = new BufferedWriter(osw);
            if(dataList != null && !dataList.isEmpty()){
                for(List<String> row : dataList){
                    for(String s : row) {
                        bfw.append(s).append("\t");
                    }
                    bfw.append("\n");
                }
            }
            return true;
        } catch (Exception e) {
            log.error("写入CSV文件异常!", e);
            return false;
        } finally {
            close(bfw, osw, out);
        }
    }

    /**
     * 新建文件
     * @param filePath
     * @return
     */
    public static File newFile(String filePath) {
        File file = new File(filePath);
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                log.error("创建文件异常!", e);
            }
        }
        return file;
    }

    public static void clear() {
        dataList.clear();
    }

    /**
     * 关闭流集合
     * @param csList
     */
    public static void close(Closeable... csList) {
        try{
            for (Closeable c : csList) {
                if(c != null) {c.close();}
            }
        } catch (Exception e) {
            log.error("关闭流异常!", e);
        }
    }

    /**
     * 获取数据
     * @return
     */
    public static List<String[]> getList() {
        return dataList;
    }

}
