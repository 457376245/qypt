package com.web.filter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.oro.text.perl.Perl5Util;
import org.springframework.web.filter.OncePerRequestFilter;

import com.web.util.BrowserUtil;


/**
 * Cross Site Scripting Filter
 * 通过过虑方式去除特殊字符
 * 只有XSS
 * <P>
 * 
 * @author liusd
 * @version V1.0 2015-3-31
 * @createDate 2015-3-31 下午12:07:08
 * @modifyDate 2015-07-01 liusd
 * @copyRight 亚信联创电信EC研发部
 */
public class XSSFilter extends OncePerRequestFilter {
    /** 资源文件. */
    private Set<String> resourceTypes = new HashSet<String>();
    /** 过虑地址. */
    private final Set<String> specUrls = new HashSet<String>();
    /** 特殊字符 过虑*/
    private String specCharacter;
    /** 不过虑的地址*/
    private String specUrl;
    

    public void setResourceTypes(String resourceType) {
        String[] values = resourceType.split(",");
        for (String value : values) {
            this.resourceTypes.add(value.trim());
        }
    }

    @Override
    public void initFilterBean() throws ServletException {
        if (resourceTypes.isEmpty()) {
            resourceTypes = BrowserUtil.resourceTypes;
        }
        if (StringUtils.isBlank(this.specCharacter)) {
            this.specCharacter = "'<>\"";
        }
    }

    /**
     * 执行过虑器方法.
     * 
     * @param request
     *            HttpServletRequest
     * @param response
     *            HttpServletResponse
     * @param filterChain
     *            FilterChain
     * @exception ServletException
     *                ServletException
     * @exception IOException
     *                IOException
     */
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // skip non-http requests
        if (!(request instanceof HttpServletRequest)) {
            filterChain.doFilter(request, response);
            return;
        }
        // clear session if session id in URL
        if (request.isRequestedSessionIdFromURL()) {
            HttpSession session = request.getSession();
            if (session != null) {
                session.invalidate();
            }
        }
        String uri = request.getRequestURI();
        boolean isResourceUrl = false;
        if (uri.lastIndexOf(".") > 0) {
            String resourceType = uri.substring(uri.lastIndexOf(".") + 1);
            int paramIndex = resourceType.indexOf("?");
            if (paramIndex > 0) {
                resourceType = resourceType.substring(0, paramIndex);
            }
            isResourceUrl = resourceTypes.contains(resourceType);
        }
        //过虑静态资源
        if (isResourceUrl) {
            filterChain.doFilter(request, response);
            return;
        }

        String path = request.getServletPath();
        if (request.getPathInfo() != null) {
            path = path + request.getPathInfo();
        }
        boolean skipCheck = true;
        if (null != this.specUrls && !this.specUrls.isEmpty()) {
            Perl5Util matcher = new Perl5Util();
            for (String entryPoint : this.specUrls) {
                if (!entryPoint.endsWith("$")) {
                    entryPoint = entryPoint + "$";
                }
                if (!entryPoint.startsWith("^")) {
                    entryPoint = "^" + entryPoint;
                }

                if (matcher.match("/" + entryPoint + "/i", path)) {
                    skipCheck = false;
                    break;
                }
            }
        } else {
            //不配置特殊过虑地址，则所有请求地址进行过虑判断
            skipCheck = false;
        }
        //跳过指定过虑路径
        if (!skipCheck) {
            filterChain.doFilter(request, response);
            return;
        }
        //过虑特殊字符，如：' <> 
//        XssHttpServletRequestWrapper xhsw=new XssHttpServletRequestWrapper(request);
//        filterChain.doFilter(xhsw, response);
        request = new ParameterRequestWrapper((HttpServletRequest) request, this.specCharacter);
        filterChain.doFilter(request, response);

    }

    /**
     * @return the specCharacter
     */
    public String getSpecCharacter() {
        return specCharacter;
    }

    /**
     * @param specCharacter the specCharacter to set
     */
    public void setSpecCharacter(String specCharacter) {
        this.specCharacter = specCharacter;
    }

    public void setSpecUrl(String specUrl) {
        String[] values = specUrl.split(",");
        for (String value : values) {
            this.specUrls.add(value.trim());
        }
    }

    public String getSpecUrl() {
        return specUrl;
    }
    

    /**
     * 内部类，处理特殊字符
     * @createDate 2013-08-05
     * @author liusd
     *
     */
    class ParameterRequestWrapper extends HttpServletRequestWrapper {

        private Map<String, String[]> params;

        public ParameterRequestWrapper(HttpServletRequest request, String specCharacter) {
            super(request);
            this.params = new HashMap<String, String[]>(request.getParameterMap());
            renewParameterMap(request, specCharacter);
        }

        public String getParameter(String name) {
            String result = "";
            Object v = params.get(name);
            if (v == null) {
                result = null;
            } else if (v instanceof String[]) {
                String[] strArr = (String[]) v;
                if (strArr.length > 0) {
                    result = strArr[0];
                } else {
                    result = null;
                }
            } else if (v instanceof String) {
                result = (String) v;
            } else {
                result = v.toString();
            }
            return result;
        }

        public Map<String, String[]> getParameterMap() {
            return params;
        }

        public Enumeration<String> getParameterNames() {
            return new Vector<String>(this.params.keySet()).elements();
        }
        
        public String getHeader(String name) {
    		String value = super.getHeader(name);
    		if (value == null)
    			return null;
    		return cleanXSS(value);
    	}

        public String[] getParameterValues(String name) {
            String[] result = null;
            Object v = this.params.get(name);
            if (v == null) {
                result = null;
            } else if (v instanceof String[]) {
                result = (String[]) v;
            } else if (v instanceof String) {
                result = new String[] { (String) v };
            } else {
                result = new String[] { v.toString() };
            }
            return result;
        }

        private void renewParameterMap(HttpServletRequest req, String specCharacter) {
            String value = "";
            for (String key : this.params.keySet()) {
                String[] t = (String[]) this.params.get(key);
                String[] newT = new String[t.length];
                for (int j = 0; j < t.length; j++) {
                	value=t[j];
//                    newT[j] = value.replaceAll("[" + specCharacter + "]", "");
                    newT[j] = cleanXSS(value);
                }
                this.params.put(key, newT);
            }
        }
        private String cleanXSS(String value) {
    		// You'll need to remove the spaces from the html entities below
    		value = value.replaceAll("<", "& lt;").replaceAll(">", "& gt;");
    		value = value.replaceAll("\\(", "& #40;").replaceAll("\\)", "& #41;");
    		value = value.replaceAll("'", "& #39;");
    		value = value.replaceAll("eval\\((.*)\\)", "");
    		value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']","\"\"");
    		value = value.replaceAll("script", "");
    		return value;
    	}
    }
}
