package com.web.log;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * 日志中输出http-session的id
 * 
 * @author Alai
 *
 *         2018年7月15日
 */
public class Converter_sid extends ClassicConverter implements
		RecursLock.Unit<String> {

	private static final RecursLock<String> sLock = new RecursLock<>("RCSid");

	@Override
	public String convert(ILoggingEvent event) {
		return sLock.with(this);
	}

	@Override
	public String onRecursLocked() {
		try {
			return whatHttpSessionId();
		} catch (Throwable ignore) {
			return "NoSid";
		}
	}

	private String whatHttpSessionId() {
		// 需配置org.springframework.web.context.request.RequestContextListener
		RequestAttributes attr = RequestContextHolder.getRequestAttributes();
		HttpServletRequest req = ((ServletRequestAttributes) attr).getRequest();
		return req.getSession().getId();
	}

}
