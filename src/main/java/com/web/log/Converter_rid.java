package com.web.log;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * 日志中输出http-request的id
 * 
 * @author Alai
 *
 *         2018年7月15日
 */
public class Converter_rid extends ClassicConverter implements
		RecursLock.Unit<String> {

	private static final String sKey = "rid:" + Converter_rid.class.getName();
	private static final int sLimit = 8;
	private static final RecursLock<String> sLock = new RecursLock<>("RCRid");

	@Override
	public String convert(ILoggingEvent event) {
		return sLock.with(this);
	}

	@Override
	public String onRecursLocked() {
		try {
			return whatHttpRequestId();
		} catch (Throwable ignore) {
			return "NoRid";
		}
	}

	private String whatHttpRequestId() {
		// 需配置org.springframework.web.context.request.RequestContextListener
		RequestAttributes attr = RequestContextHolder.getRequestAttributes();
		HttpServletRequest req = ((ServletRequestAttributes) attr).getRequest();
		Object o = req.getAttribute(sKey);
		String rid = (String) (o instanceof String ? o : null);
		if (rid == null) {
			String s = UUID.randomUUID().toString().replace("-", "");
			rid = s.length() <= sLimit ? s : s.substring(0, sLimit);
			req.setAttribute(sKey, rid);
		}
		return rid;
	}

}
