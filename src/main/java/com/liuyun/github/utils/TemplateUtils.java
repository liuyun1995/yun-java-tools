package com.liuyun.github.utils;

import com.google.common.io.Resources;
import freemarker.template.Configuration;
import freemarker.template.Template;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.List;

public class TemplateUtils {

    private static final Configuration CONFIGURATION = new Configuration(Configuration.VERSION_2_3_22);

    /**
     * 获取渲染后的字符串
     * @param path
     * @param fragment
     * @param model
     * @return
     */
    public static String getRenderString(String path, String fragment, Object model) {
        try {
            String template = getTemplateString(path, fragment);
            StringWriter out = new StringWriter();
            new Template(fragment, template, CONFIGURATION).process(model, out);
            return out.toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取模板字符串
     * @param path
     * @param fragment
     * @return
     */
    public static String getTemplateString(String path, String fragment) {
        try {
            List<String> lines = Resources.readLines(Resources.getResource(path), Charset.forName("UTF-8"));
            StringBuilder sb = new StringBuilder();
            int startLine = Integer.MAX_VALUE;
            for (int i = 0; i < lines.size(); i++) {
                if(lines.get(i).contains("<" + fragment + ">")) {
                    startLine = i + 1;
                }
                if(lines.get(i).contains("</" + fragment + ">")) {
                    startLine = Integer.MAX_VALUE;
                }
                if(i >= startLine) {
                    sb.append(lines.get(i)).append("\n");
                }
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

}
