package com.web.model;

import com.web.util.MD5;


public class TemplateDatagram {

    /** 操作 add/del/update */
    private String action = "";

    /** 文件类型 D-目录 F-文件 */
    private String fileType = "";

    private String file = "";

    private String fileOld = "";

    /** 传输密码 **/
    private String encryptWords;

    /**
     * encryptWords
     * @return the encryptWords
     * @since 1.0.0
     */

    public String getEncryptWords() {
        return MD5.md5(encryptWords + "wss_ttp").toUpperCase();
    }

    /**
     * @param encryptWords the encryptWords to set
     */
    public void setEncryptWords(String encryptWords) {
        this.encryptWords = encryptWords;
    }

    public String buildXML() {
        StringBuilder rt = new StringBuilder("");
        rt.append("<root>");
        rt.append("    <encryptWords>")
                .append(null == encryptWords ? "" : getEncryptWords())
                .append("</encryptWords>");
        rt.append("    <action>").append(null == action ? "" : getAction())
                .append("</action>");
        rt.append("    <fileType>")
                .append(null == fileType ? "" : getFileType())
                .append("</fileType>");
        rt.append("    <file>").append(null == file ? "" : getFile())
                .append("</file>");
        rt.append("    <fileOld>").append(null == fileOld ? "" : getFileOld())
                .append("</fileOld>");
        rt.append("</root>");

        return rt.toString();
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getFileOld() {
        return fileOld;
    }

    public void setFileOld(String fileOld) {
        this.fileOld = fileOld;
    }

    public static void main(String[] args) {
    	System.out.println(MD5.md5("ttp_webwss_ttp"));;
	}
}
