package com.web.freemarker.custom;


import java.text.SimpleDateFormat;
import java.util.Date;   
import java.util.List;   
   
import freemarker.template.TemplateMethodModel;   
import freemarker.template.TemplateModelException;   
   
/**  
 * 根 据传入的日期时间格式，在页面上直接取得当前时间的格式化结果  
 * 如果格式为空或者错误，将返回yyyy-MM-dd HH:mm:ss  
 * 页面调用${datetime("yyyy")}/${datetime('yyyy')}  
 * @see com.yourcompany.ExtendedFreemarkerManager#createConfiguration  
 * @author Sunshine  
 *  
 */   
public class DateTimeMethod implements TemplateMethodModel {   
   
    private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    @SuppressWarnings("unchecked")   
    public Object exec(List args) throws TemplateModelException {   
        Date date = new Date();  
        String pattern=DEFAULT_PATTERN;
        if(args!=null && args.size()>0){
        	pattern = args.get(0).toString();
        }  
        try {   
            return new SimpleDateFormat(pattern).format(date);   
        } catch (RuntimeException e) {   
            return new SimpleDateFormat(DEFAULT_PATTERN).format(date);   
        }   
    }   
}   
