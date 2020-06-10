package com.web.freemarker.custom;

import java.util.List;   

import org.apache.commons.lang3.math.NumberUtils;

import freemarker.template.TemplateMethodModel;   
import freemarker.template.TemplateModelException;

/**  
 * 截取长度，并把截取掉部分用特殊字符代替
 * 页面调用${subStr('21321321',2,3,'X')}  
 * @see com.yourcompany.ExtendedFreemarkerManager#createConfiguration  
 * @author Sunshine  
 *  
 */   
public class SubStrMethod implements TemplateMethodModel {   
   
	/**
	 * @param arg0 原始字符串,arg1被截掉开始第几位(1开始),arg2结束第几位或被替换字串
	 */
    @SuppressWarnings("unchecked")   
    public Object exec(List args) throws TemplateModelException { 
       int start=-1;
       int end=-1;
       String inputStr=null;
       String replace=null;
        if(args!=null && args.size()>0){
        	inputStr= args.get(0).toString();
        	start = Integer.parseInt(args.get(1).toString());
        	if(args.size()>2){
        		String t=args.get(2).toString();
        		if(!NumberUtils.isDigits(t)){
        			replace =t;
        		}else{
        			end = Integer.parseInt(args.get(2).toString());
        		}
        	}
        	if(args.size()>3){
        		replace = args.get(3).toString();
        	}
        }else{
        	 return "";
        }
        StringBuilder str=new StringBuilder("");
        int len=inputStr.length();
        if(start>0 && len>=start){
        	str.append(inputStr.substring(0, start-1));
        	 if(start>0 && end>=start){
        		 if(replace!=null){
        			 for(int s=start-1; s<end;s++){
        				 str.append(replace);
        			 }
             	}
        		 if(end<len){
        			 str.append(inputStr.substring(end));
        		 }
        	 }else{
        		 if(replace!=null){
        			 for(int s=start-1; s<len;s++){
        				 str.append(replace);
        			 }	
             	}
        	 }
        }
        return str.toString();
    }   
}   
