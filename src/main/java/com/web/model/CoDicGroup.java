package com.web.model;

/**
 * 数据字典组
 *
 * @author
 * @create 2020-03-13
 **/
public class CoDicGroup {
    //组id
    private Integer groupId;
    //组编码
    private String groupCode;
    //组标题
    private String groupTitle;
    //组描述
    private String groupDesc;
    //组状态
    private String statusCd;

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public String getGroupDesc() {
        return groupDesc;
    }

    public void setGroupDesc(String groupDesc) {
        this.groupDesc = groupDesc;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }
}
