package com.web.dao;

import com.web.model.CoLogBean;
import com.web.model.CoMember;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository("com.web.dao.CoMemberDao")
public interface CoMemberDao {
    //
    void insertCoMember(CoMember coMember);
    Integer getCoMemberCount(CoMember coMember);
    void updateCoMember(CoMember coMember);
    Map<String,Object> getCoMember(CoMember coMember);
    Map<String,Object> qryCoMember(CoMember coMember);
    void insertCoLogBean(CoLogBean coLogBean);
}
