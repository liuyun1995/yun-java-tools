package com.liuyun.github.alarmpush.dingding;

import com.liuyun.github.utils.SpringBeanUtils;
import com.liuyun.github.utils.ThreadPoolRepo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author: lewis
 * @create: 2020/4/30 上午10:13
 */
@Slf4j
public class DingDingUtils {

    public static String webhook = "";

    public static String secret = "";

    private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");

    public static void sendMsg(Throwable cause) {
        ThreadPoolRepo.getThreadPool().execute(()->doSendMsg(cause));
    }

    public static boolean doSendMsg(Throwable cause) {
        String url = buildUrl();
        if(url == null) {
            return false;
        }
        String template = "{\"msgtype\":\"text\",\"text\":{\"content\":\"%s\"}}";
        String content = alarmMsg(cause);
        String text = String.format(template, content);
        //TODO 发送HTTP请求
        return true;
    }

    private static String buildUrl() {
        try {
            Long timestamp = System.currentTimeMillis();
            String stringToSign = timestamp + "\n" + secret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
            String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)),"UTF-8");
            return String.format("%s&timestamp=%s&sign=%s", webhook, timestamp, sign);
        } catch (Exception e) {
            return null;
        }
    }

    private static String alarmMsg(Throwable cause) {
        try {
            String projectName = getProjectName();
            String environment = getEnvironment();
            String errorTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            StringBuilder description = new StringBuilder();
            if (environment != null) {
                description.append(LINE_SEPARATOR);
                description.append("### 告警环境: ");
                description.append(environment);
            }
            if(projectName != null) {
                description.append(LINE_SEPARATOR);
                description.append("### 出错项目: ");
                description.append(projectName);
            }
            if(errorTime != null) {
                description.append(LINE_SEPARATOR);
                description.append("### 出错时间: ");
                description.append(errorTime);
            }
            if (cause != null) {
                description.append(LINE_SEPARATOR);
                description.append("### 异常堆栈: ");
                description.append(ExceptionUtils.getStackTrace(cause));
            }
            return description.toString();
        } catch (Exception e) {
            log.error("错误上下文调用toString方法出错", e);
            return "";
        }
    }

    private static String getProjectName() {
        String projectName = null;
        try {
            projectName = SpringBeanUtils.getProperty("spring.application.name");
        } catch (Exception e) {
            log.error("获取项目名异常", e);
        }
        return projectName == null ? "null" : projectName;
    }

    private static String getEnvironment() {
        String property = null;
        try {
            property = SpringBeanUtils.getProperty("ihr360.config.brand");
        } catch (Exception e) {
            log.error("获取环境异常", e);
        }
        return property == null ? "null" : property;
    }

}
