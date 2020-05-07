package com.liuyun.github.alarmpush.config;

import lombok.Data;
import java.util.List;

/**
 * @Author: liuyun18
 * @Date: 2019/2/19 下午5:09
 */
@Data
public class AlarmConfig {

    /** 推送周期(毫秒) */
    private long period;
    /** 推送阀值 */
    private int threshold;
    /** 邮件配置信息 */
    private EmailConfig emailConfig;
    /** 成员列表信息 */
    private List<Member> memberList;

    /**
     * 获取邮件消息配置
     * @return
     */
    public EmailConfig getEmailConfig() {
        return emailConfig;
    }

    @Data
    public static class EmailConfig {
        /** 是否禁用 */
        private Boolean disable;
        /** 服务器主机 */
        private String serverHost;
        /** 服务器端口 */
        private String serverPort;
        /** 发送者 */
        private String fromAddress;
        /** 用户名 */
        private String userName;
        /** 密码 */
        private String password;
    }

    @Data
    public static class Member {
        /** 成员姓名 */
        private String name;
        /** 岗位类型 0-老大, 1-后端*/
        private Integer jobType;
        /** mis号 */
        private String misId;
        /** 微信号 */
        private String weixin;
        /** 邮箱号 */
        private String email;
    }

}
