package com.web.bmo;

import com.web.dao.ImgDao;
import com.web.model.CoImg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 文件处理
 * </p>
 *
 * @package: com.web
 * @description: 文件处理
 * @author: wangzx8
 * @date: 2020/3/18 11:25
 * @copyright: Copyright (c) 2020
 * @version: V1.0
 * @modified: wangzx8
 */
@Service("com.web.bmo.ImgBmoImpl")
public class ImgBmoImpl implements ImgBmo {

    @Autowired
    private ImgDao imgDao;

    @Override
    public int deleteByPrimaryKey(Long imgId) {
        return imgDao.deleteByPrimaryKey(imgId);
    }

    @Override
    public int insert(CoImg record) {
        return imgDao.insert(record);
    }

    @Override
    public int insertSelective(CoImg record) {
        int i = imgDao.insertSelective(record);
        return i;
    }

    @Override
    public CoImg selectByPrimaryKey(Long imgId) {
        return imgDao.selectByPrimaryKey(imgId);
    }

    @Override
    public int updateByPrimaryKeySelective(CoImg record) {
        return imgDao.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(CoImg record) {
        return imgDao.updateByPrimaryKey(record);
    }

    @Override
    public int insertAndUpdateSelective(CoImg record) {
        CoImg editCoImg = new CoImg();
        editCoImg.setStatusCd("102");
        editCoImg.setStatusDate(new Date());
        editCoImg.setUpdateDate(new Date());
        editCoImg.setLinkId(record.getLinkId());
        editCoImg.setFileType(record.getFileType());
        imgDao.updateSelective(editCoImg);
        int i = imgDao.insertSelective(record);
        return i;
    }

    @Override
    public CoImg insertAndUpdateSelective2(CoImg record) {
        CoImg editCoImg = new CoImg();
        editCoImg.setStatusCd("102");
        editCoImg.setStatusDate(new Date());
        editCoImg.setUpdateDate(new Date());
        editCoImg.setLinkId(record.getLinkId());
        editCoImg.setFileType(record.getFileType());
        imgDao.updateSelective(editCoImg);
        int i = imgDao.insertSelective(record);
        if(i == 1) {
            return record;
        }else{
            return null;
        }
    }

    @Override
    public List<CoImg> selectSelective(CoImg record) {
        return imgDao.selectSelective(record);
    }
}
