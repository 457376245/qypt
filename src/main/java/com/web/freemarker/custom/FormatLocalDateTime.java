package com.web.freemarker.custom;

import freemarker.ext.beans.StringModel;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * localDateTime格式化
 * </p>
 *
 * @package: com.web.freemarker.custom
 * @description: localDateTime格式化
 * @author: wangzx8
 * @date: 2020/5/29 16:24
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */
public class FormatLocalDateTime implements TemplateMethodModelEx {

    private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    @Override
    @SuppressWarnings("unchecked")
    public Object exec(List args) throws TemplateModelException {

        if (args.size() != 2) {
            throw new TemplateModelException("Wrong arguments");
        }
        if(args.get(0) == null){
            return "";
        }
        TemporalAccessor time = (TemporalAccessor) ((StringModel) args.get(0)).getWrappedObject();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(((SimpleScalar) args.get(1)).getAsString());
        return formatter.format(time);


//        LocalDateTime date = LocalDateTime.now();
//        String pattern=DEFAULT_PATTERN;
//        if(args!=null && args.size()>0){
//            pattern = args.get(0).toString();
//        }
//
//
//        try {
//            return DateTimeFormatter.ofPattern(pattern).format(date);
//        } catch (RuntimeException e) {
//            return DateTimeFormatter.ofPattern(DEFAULT_PATTERN).format(date);
//        }
    }
}
