package com.liuyun.github.alarmpush;

import com.google.common.collect.Maps;
import com.liuyun.github.alarmpush.config.AlarmConfig;
import com.liuyun.github.alarmpush.config.ConfigRepo;
import com.liuyun.github.alarmpush.email.EmailPusher;
import com.liuyun.github.alarmpush.utils.EnvUtils;
import com.liuyun.github.alarmpush.utils.ErrorContext;
import com.liuyun.github.alarmpush.utils.PushRecord;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: liuyun18
 * @Date: 2019/1/29 下午2:09
 */
@Slf4j
public class AlarmPusher {

    /** 邮件消息 */
    public static final int EMAIL = 1;
    /** 微信消息 */
    public static final int WECHAT = 2;

    private static AlarmConfig alarmConfig = ConfigRepo.getAlarmConfig();
    private static EmailPusher emailPusher = EmailPusher.instance(alarmConfig.getEmailConfig());
    private static Map<String, PushRecord> pushRecordMap = Maps.newConcurrentMap();

    /**
     * 推送消息
     * @param pushType
     * @param errorContext
     */
    public static void pushMsg(int pushType, ErrorContext errorContext) {
        try{
            if(needPush(errorContext)) {
                switch (pushType) {
                    case EMAIL:
                        pushEmailMsg(alarmConfig, errorContext); break;
                    case WECHAT:
                        pushWeChatMsg(alarmConfig, errorContext); break;
                    default: return;
                }
            }
        } catch (Exception e) {
            log.error(String.format("推送信息失败, [pushType]=%s, [errorContext]=%s", pushType, errorContext), e);
        }
    }

    /**
     * 判断是否需要推送
     * @param errorContext
     * @return
     */
    private static boolean needPush(ErrorContext errorContext) {
        String errorSite = getErrorSite(errorContext);
        PushRecord pushRecord = pushRecordMap.get(errorSite);
        //若推送记录为空，则直接推送
        if(pushRecord == null) {
            pushRecord = new PushRecord(alarmConfig.getPeriod(), alarmConfig.getThreshold());
            pushRecordMap.put(errorSite, pushRecord);
            return true;
        }
        //获取上次推送时间
        long lastPushTime = pushRecord.getLastPushTime();
        //获取当前推送时间
        long nowPushTime = System.currentTimeMillis();
        //计算推送时间间隔
        long i = nowPushTime - lastPushTime;
        //若在推送周期内，则增加计数
        if(i < pushRecord.getPeriod()) {
            pushRecord.incrementTimes();
        } else {
            pushRecord = new PushRecord(alarmConfig.getPeriod(), alarmConfig.getThreshold());
            pushRecordMap.put(errorSite, pushRecord);
        }
        //根据周期内的推送次数，判断是否推送
        return pushRecord.isAllow();
    }

    /**
     * 获取错误位置信息
     * @param errorContext
     * @return
     */
    private static String getErrorSite(ErrorContext errorContext) {
        return errorContext.getClassName() + "::" + errorContext.getMethodName() + "::" + errorContext.getLineNumber();
    }

    /**
     * 推送邮件消息
     * @param alarmConfig
     * @param errorContext
     */
    private static void pushEmailMsg(AlarmConfig alarmConfig, ErrorContext errorContext) {
        if(alarmConfig.getEmailConfig().getDisable()) { return; }
        if(!EnvUtils.isOnLine()) { return; }
        List<String> emailList = alarmConfig.getMemberList().stream().map((e) -> e.getEmail()).collect(Collectors.toList());
        emailPusher.sendTextMail(errorContext.toString(), emailList.toArray(new String[emailList.size()]));
    }

    /**
     * 推送微信消息
     * @param alarmConfig
     * @param errorContext
     */
    private static void pushWeChatMsg(AlarmConfig alarmConfig, ErrorContext errorContext) {
        return;
    }

}
