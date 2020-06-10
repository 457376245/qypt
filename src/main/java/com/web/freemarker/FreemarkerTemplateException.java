package com.web.freemarker;

/**
 * FreemarkerException等价的异常类，不过继承之RuntimeException.
 * <P>
 * @author tang zheng yu
 * @version V1.0 2012-1-4
 * @createDate 2012-1-4 下午3:21:24
 * @modifyDate	 tang 2012-1-4 <BR>
 * @copyRight 亚信联创电信CRM研发部
 */
public class FreemarkerTemplateException extends RuntimeException {

	private static final long serialVersionUID = -3001339513837419069L;

	public FreemarkerTemplateException() {
		super();
	}

	public FreemarkerTemplateException(String message, Throwable cause) {
		super(message, cause);
	}

	public FreemarkerTemplateException(String message) {
		super(message);
	}

	public FreemarkerTemplateException(Throwable cause) {
		super(cause);
	}
	
}
