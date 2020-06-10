package com.web.model;

public class UploadOutVo {

    // 上传状态
    private boolean successFlag;

    // 文件上传路径
    private String uploadPath;

    public boolean isSuccessFlag() {
        return successFlag;
    }

    public void setSuccessFlag(boolean successFlag) {
        this.successFlag = successFlag;
    }

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

}
