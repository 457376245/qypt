package com.web.bmo;

import com.web.model.CoImg;

import java.util.List;

/**
 * <p>
 * 图片处理
 * </p>
 *
 * @package: com.web.bmo
 * @description: 图片处理
 * @author: wangzx8
 * @date: 2020/3/18 11:25
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */
public interface ImgBmo {

    int deleteByPrimaryKey(Long imgId);

    int insert(CoImg record);

    int insertSelective(CoImg record);

    CoImg selectByPrimaryKey(Long imgId);

    int updateByPrimaryKeySelective(CoImg record);

    int updateByPrimaryKey(CoImg record);

    int insertAndUpdateSelective(CoImg record);

    CoImg insertAndUpdateSelective2(CoImg record);

    List<CoImg> selectSelective(CoImg record);
}
