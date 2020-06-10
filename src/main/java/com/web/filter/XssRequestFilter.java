package com.web.filter;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 
 * <b>项目名称：</b>ocPortal<br>
 * <b>类名称：</b>com.ai.ec.mall.filter.XssRequestFilter<br>
 * <b>类描述：</b>(xss过滤器)<br>
 * <b>创建人：</b>oc<br>
 * <b>创建时间：</b>2016年12月30日-下午5:13:12<br>
 * <b>@Copyright:</b>2016-亚信
 *
 */
public class XssRequestFilter extends OncePerRequestFilter {

	private String exclude = null; // 不需要过滤的路径集合
	private Pattern pattern = null; // 匹配不需要过滤路径的正则表达式

	public void setExclude(String exclude) {
		this.exclude = exclude;
		pattern = Pattern.compile(getRegStr(this.exclude));
	}

	/**
	 * XSS过滤
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String requestURI = request.getRequestURI();
		if (StringUtils.isNotBlank(requestURI)) {
			requestURI = requestURI.replace(request.getContextPath(), "");
		}

		if (pattern.matcher(requestURI).matches()){
			filterChain.doFilter(request, response);
		}else {
			XssHttpServletRequestWrapper xhsw=new XssHttpServletRequestWrapper(request);
			filterChain.doFilter(xhsw, response);
		}
	}

	/**
	 * 将传递进来的不需要过滤得路径集合的字符串格式化成一系列的正则规则
	 * 
	 * @param str
	 *            不需要过滤的路径集合
	 * @return 正则表达式规则
	 * */
	private String getRegStr(String str) {
		if (StringUtils.isNotBlank(str)) {
			String[] excludes = str.split(";"); // 以分号进行分割
			int length = excludes.length;
			for (int i = 0; i < length; i++) {
				String tmpExclude = excludes[i];
				// 对点、反斜杠和星号进行转义
				tmpExclude = tmpExclude.replace("\\", "\\\\").replace(".", "\\.").replace("*", ".*");

				tmpExclude = "^" + tmpExclude + "$";
				excludes[i] = tmpExclude;
			}
			return StringUtils.join(excludes, "|");
		}
		return str;
	}

}
