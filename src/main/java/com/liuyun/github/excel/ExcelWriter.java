package com.liuyun.github.excel;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * @author: lewis
 * @create: 2020/1/8 上午10:38
 */
public class ExcelWriter {

    private static ThreadLocal<HSSFWorkbook> workbookLocal = new ThreadLocal();

    private String sheetName;
    private List<Object> headerData;
    private List<List<Object>> rowData;

    private StyleConsumer<HSSFRow, List<Object>> headerStyleConsumer;
    private StyleConsumer<HSSFRow, List<Object>> rowStyleConsumer;

    public static HSSFWorkbook getWorkbook() {
        HSSFWorkbook wb = workbookLocal.get();
        if(wb == null) {
            wb = new HSSFWorkbook();
            workbookLocal.set(wb);
        }
        return wb;
    }

    public static ExcelWriter write() {
        return new ExcelWriter();
    }

    public ExcelWriter setSheetName(String sheetName) {
        this.sheetName = sheetName;
        return this;
    }

    public ExcelWriter setHeaderData(List<Object> headerData) {
        this.headerData = headerData;
        return this;
    }

    public ExcelWriter setRowData(List<List<Object>> rowData) {
        this.rowData = rowData;
        return this;
    }


    public ExcelWriter setHeaderStyle(StyleConsumer<HSSFRow, List<Object>> styleConsumer) {
        this.headerStyleConsumer = styleConsumer;
        return this;
    }

    public ExcelWriter setRowStyle(StyleConsumer<HSSFRow, List<Object>> styleConsumer) {
        this.rowStyleConsumer = styleConsumer;
        return this;
    }

    public void to(File file) {
        HSSFSheet sheet = getWorkbook().createSheet(sheetName == null ? "sheet0" : sheetName);
        buildHeader(sheet);
        buildRow(sheet);
        export(file);
    }

    private void buildHeader(HSSFSheet sheet) {
        if(headerData != null && headerData.size() != 0) {
            HSSFRow header = sheet.createRow(0);
            for (int i = 0; i < headerData.size(); i++) {
                header.createCell(i).setCellValue(headerData.get(i) + "");
            }
            headerStyleConsumer.accept(header, headerData);
        }
    }

    private void buildRow(HSSFSheet sheet) {
        if(null != rowData) {
            for (int i = 0; i < rowData.size(); i++) {
                HSSFRow row = sheet.createRow(i + 1);
                for (int j = 0; j < rowData.get(i).size(); j++) {
                    row.createCell(j).setCellValue(rowData.get(i).get(j) + "");
                }
                rowStyleConsumer.accept(row, rowData.get(i));
            }
        }
    }

    private void export(File file) {
        try {
            if(file.exists()) {
                file.delete();
            } else {
                file.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(file);
            getWorkbook().write(out);
        } catch (Exception e) {
            throw new RuntimeException("导出失败！");
        }
    }

    public interface StyleConsumer<T, U> {
        U accept(T t, U u);
    }

}
