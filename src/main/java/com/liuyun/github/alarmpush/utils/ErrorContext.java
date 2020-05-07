package com.liuyun.github.alarmpush.utils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * 错误上下文
 * @author maoyan
 */
@Data
@Slf4j
public class ErrorContext {
	
	private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");

	/** 报错环境 */
	private String environment;
	/** 出错项目 */
	private String projectName;
	/** 出错类名 */
	private String className;
	/** 出错方法 */
	private String methodName;
	/** 出错行号 */
	private Integer lineNumber;
	/** 出错时间 */
	private String errorTime;
	/** 错误消息 */
	private String message;
	/** 涉及对象 */
	private List<Object> object;
	/** 异常堆栈 */
	private Throwable cause;

	/**
	 * 构造器
	 */
	private ErrorContext() {
		try {
			this.environment = getEnvironment();
			this.projectName = getProjectName();
			StackTraceElement ste = new Throwable().getStackTrace()[2];
			this.className = ste.getClassName();
			this.methodName = ste.getMethodName();
			this.lineNumber = ste.getLineNumber();
			this.errorTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		} catch (Exception e) {
			log.error("构造错误上下文出现异常", e);
		}
	}

	/**
	 * 构建实例
	 * @return
	 */
	public static ErrorContext instance() {
		return new ErrorContext();
	}

	/**
	 * 获取环境信息
	 * @return
	 */
	private static String getEnvironment() {
		return EnvUtils.getEnvironment();
	}

	/**
	 * 获取项目名
	 * @return
	 */
	private static String getProjectName() {
		try{
			Properties prop = new Properties();
			prop.load(ErrorContext.class.getResourceAsStream("/META-INF/app.properties"));
			return prop.getProperty("app.name");
		} catch (Exception e) {
			log.error("获取项目名失败", e);
			return null;
		}
	}

	/**
	 * 设置错误消息
	 * @param message
	 * @return
	 */
	public ErrorContext message(String message) {
		this.message = message;
		return this;
	}

	/**
	 * 设置涉及对象
	 * @param object
	 * @return
	 */
	public ErrorContext object(Object... object) {
		this.object = Lists.newArrayList(object);
		return this;
	}

	/**
	 * 设置异常堆栈
	 * @param cause
	 * @return
	 */
	public ErrorContext cause(Throwable cause) {
		this.cause = cause;
		return this;
	}
	
	@Override
	public String toString() {
		try {
			StringBuilder description = new StringBuilder();
			if (this.environment != null) {
				description.append(LINE_SEPARATOR);
				description.append("### 告警环境: ");
				description.append(this.environment);
			}
			if (this.message != null) {
				description.append(LINE_SEPARATOR);
				description.append("### 错误消息: ");
				description.append(this.message);
			}
			if(this.projectName != null) {
				description.append(LINE_SEPARATOR);
				description.append("### 出错项目: ");
				description.append(this.projectName);
			}
			if(this.className != null) {
				description.append(LINE_SEPARATOR);
				description.append("### 出错类名: ");
				description.append(this.className);
			}
			if(this.methodName != null) {
				description.append(LINE_SEPARATOR);
				description.append("### 出错方法: ");
				description.append(this.methodName);
			}
			if(this.lineNumber != null) {
				description.append(LINE_SEPARATOR);
				description.append("### 出错行号: ");
				description.append(this.lineNumber);
			}
			if(this.errorTime != null) {
				description.append(LINE_SEPARATOR);
				description.append("### 出错时间: ");
				description.append(this.errorTime);
			}
			if (this.object != null) {
				description.append(LINE_SEPARATOR);
				description.append("### 涉及对象: ");
				description.append(JSONObject.toJSONString(object));
			}
			if (this.cause != null) {
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

}
