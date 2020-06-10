package com.web.model;

import java.util.Date;
import java.util.List;

public class CoMenu {
    //菜单ID
    private int id;
    private String name;
    //角色管理
    private String roleManage;
    private int pid;
    private int sort_seq;
    private int is_type;
    private String descpt;
    private String code;
    private int icon;
    private String page_url;
    private Date status_date;
    private Date update_date;
    private String status_cd;
    private List<CoMenu> secondMenu;

    @Override
    public String toString() {
        return "CoMenu{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", roleManage='" + roleManage + '\'' +
                ", pid=" + pid +
                ", sort_seq=" + sort_seq +
                ", is_type=" + is_type +
                ", descpt='" + descpt + '\'' +
                ", code='" + code + '\'' +
                ", icon=" + icon +
                ", page_url='" + page_url + '\'' +
                ", status_date=" + status_date +
                ", update_date=" + update_date +
                ", status_cd='" + status_cd + '\'' +
                ", secondMenu=" + secondMenu +
                '}';
    }

    public CoMenu(int id, String name, String roleManage, int pid, int sort_seq, int is_type, String descpt, String code, int icon, String page_url, Date status_date, Date update_date, String status_cd, List<CoMenu> secondMenu) {
        this.id = id;
        this.name = name;
        this.roleManage = roleManage;
        this.pid = pid;
        this.sort_seq = sort_seq;
        this.is_type = is_type;
        this.descpt = descpt;
        this.code = code;
        this.icon = icon;
        this.page_url = page_url;
        this.status_date = status_date;
        this.update_date = update_date;
        this.status_cd = status_cd;
        this.secondMenu = secondMenu;
    }

    public CoMenu() {
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoleManage() {
        return roleManage;
    }

    public void setRoleManage(String roleManage) {
        this.roleManage = roleManage;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getSort_seq() {
        return sort_seq;
    }

    public void setSort_seq(int sort_seq) {
        this.sort_seq = sort_seq;
    }

    public int getIs_type() {
        return is_type;
    }

    public void setIs_type(int is_type) {
        this.is_type = is_type;
    }

    public String getDescpt() {
        return descpt;
    }

    public void setDescpt(String descpt) {
        this.descpt = descpt;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getPage_url() {
        return page_url;
    }

    public void setPage_url(String page_url) {
        this.page_url = page_url;
    }

    public Date getStatus_date() {
        return status_date;
    }

    public void setStatus_date(Date status_date) {
        this.status_date = status_date;
    }

    public Date getUpdate_date() {
        return update_date;
    }

    public void setUpdate_date(Date update_date) {
        this.update_date = update_date;
    }

    public String getStatus_cd() {
        return status_cd;
    }

    public void setStatus_cd(String status_cd) {
        this.status_cd = status_cd;
    }

    public List<CoMenu> getSecondMenu() {
        return secondMenu;
    }

    public void setSecondMenu(List<CoMenu> secondMenu) {
        this.secondMenu = secondMenu;
    }
}
