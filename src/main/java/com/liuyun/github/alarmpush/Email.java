package com.liuyun.github.alarmpush;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import java.io.File;
import java.util.List;

/**
 * @Author: liuyun18
 * @Date: 2018/10/22 下午4:24
 */
@Slf4j
@Data
public class Email {

    /** 接收者列表 */
    private Address[] TOAddress;
    /** 抄送者列表 */
    private Address[] CCAddress;
    /** 暗抄者列表 */
    private Address[] BCCAddress;
    /** 邮件主题 */
    private String subject;
    /** 邮件内容 */
    private String content;
    /** 附件列表 */
    private List<File> attachments;

    /**
     * 设置接收者列表
     * @param toAddressArray
     */
    public void setTOAddress(String... toAddressArray) {
        try {
            TOAddress = new Address[toAddressArray.length];
            for (int i = 0; i < toAddressArray.length; i++) {
                TOAddress[i] = new InternetAddress(toAddressArray[i]);
            }
        } catch (Exception e) {
            log.error("设置接收者列表异常", e);
        }
    }

    /**
     * 设置抄送者列表
     * @param ccAddressArray
     */
    public void setCCAddress(String... ccAddressArray) {
        try {
            CCAddress = new Address[ccAddressArray.length];
            for (int i = 0; i < ccAddressArray.length; i++) {
                CCAddress[i] = new InternetAddress(ccAddressArray[i]);
            }
        } catch (Exception e) {
            log.error("设置抄送者列表异常", e);
        }
    }

    /**
     * 设置暗抄者列表
     * @param bccAddressArray
     */
    public void setBCCAddress(String... bccAddressArray) {
        try {
            BCCAddress = new Address[bccAddressArray.length];
            for (int i = 0; i < bccAddressArray.length; i++) {
                BCCAddress[i] = new InternetAddress(bccAddressArray[i]);
            }
        } catch (Exception e) {
            log.error("设置暗抄者列表异常", e);
        }
    }

}
