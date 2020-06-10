package com.web.dao;

import com.web.model.CoImg;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 文件保存
 * </p>
 *
 * @package: com.web.dao
 * @description: 文件保存
 * @author: wangzx8
 * @date: 2020/3/17 20:13
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */
@Repository("com.web.dao.ImgDao")
public interface ImgDao {
    int deleteByPrimaryKey(Long imgId);

    int insert(CoImg record);

    int insertSelective(CoImg record);

    CoImg selectByPrimaryKey(Long imgId);

    int updateByPrimaryKeySelective(CoImg record);

    int updateByPrimaryKey(CoImg record);

    int updateSelective(CoImg record);

    List<CoImg> selectSelective(CoImg record);
}