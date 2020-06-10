package com.web.fastdfs;

import com.web.common.PropertyToRedis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Component
public class ImageUtil {

    private static final Logger log = LoggerFactory.getLogger(FastDFSClient.class);

	@Resource
	private PropertyToRedis propertyToRedis;



    public String uploadImage(MultipartFile file) throws IOException {


        // String url = new FastDFSClient().saveFile(file);
        String url = new ImageUtil().saveFile(file);
//        String imageUrl = (String) propertyToRedis.getPropertyValue("image-url");
//        return imageUrl + url;
        return url;
    }

    /**
     * 上传文件demo
     *
     * @param multipartFile
     * @return
     * @throws IOException
     */
    public String saveFile(MultipartFile multipartFile) throws IOException {
        String[] fileAbsolutePath = {};
        String fileName = multipartFile.getOriginalFilename();
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        byte[] file_buff = null;
        InputStream inputStream = multipartFile.getInputStream();
//        String type = multipartFile.getContentType();
//
//        long size = multipartFile.getSize();

//        if (!"video/mp4".equals(type)) {
//            switch (uType) {
//                case "3":
//                case "4":
//                case "6":
//                case "8":
//                case "12":
//                case "14":
//                case "16":
//                    inputStream = this.imgCover(inputStream, size, 0);
//                    break;
//            }
//
//        }
        if (inputStream != null) {
            int len1 = inputStream.available();
            file_buff = new byte[len1];
            inputStream.read(file_buff);
        }
        inputStream.close();
        FastDFSFile file = new FastDFSFile(fileName, file_buff, ext);
        try {
            fileAbsolutePath = FastDFSClient.upload(file); // upload to fastdfs

        } catch (Exception e) {
            log.error("upload file Exception!", e);
        }
        if (fileAbsolutePath == null) {
            log.error("upload file failed,please upload again!");
            return "";
        }
        // String path = FastDFSClient.getTrackerUrl() + fileAbsolutePath[0] + "/" +
        // fileAbsolutePath[1];
        String path = fileAbsolutePath[0] + "/" + fileAbsolutePath[1];
        return path;
    }

    private String generateImage(String type, MultipartFile file) { // 对字节数组字符串进行Base64解码并生成图片
        if (file.isEmpty()) {
            return null;
        }

        try { // Base64解码
            byte[] b = file.getBytes();
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {// 调整异常数据
                    b[i] += 256;
                }
            }
            long dateTime = System.currentTimeMillis();
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            String prefix = "upload" + File.separator + type;
            String fileName = dateTime + "." + suffix;

            File dir = new File("upload" + File.separator + type);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File imageFile = new File(dir.getAbsolutePath(), fileName);

            file.transferTo(imageFile);

            prefix = prefix + fileName;
            return prefix;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 上传文件demo
     *
     * @param
     * @return
     * @throws IOException
     */
    public String saveFile(String fileName, byte[] file_buff, String ext) {
        String[] fileAbsolutePath = {};

        FastDFSFile file = new FastDFSFile(fileName, file_buff, ext);
        try {
            fileAbsolutePath = FastDFSClient.upload(file); // upload to fastdfs

        } catch (Exception e) {
            log.error("upload file Exception!", e);
            return null;
        }
        if (fileAbsolutePath == null) {
            log.error("upload file failed,please upload again!");
            return null;
        }
        // String path = FastDFSClient.getTrackerUrl() + fileAbsolutePath[0] + "/" +
        // fileAbsolutePath[1];
        String path = fileAbsolutePath[0] + "/" + fileAbsolutePath[1];
        return path;
    }

    /**
     * 删除单个文件
     *
     * @param sPath 被删除文件的文件名
     * @return -1失败,0成功 2为文件不存在
     */
    public int deleteFastDFSFile(String sPath) throws Exception {
        String groupName = sPath.substring(0, sPath.indexOf("/"));
        String fileUrl = sPath.substring(sPath.indexOf("/") + 1);
		FastDFSClient.deleteFile(groupName, fileUrl);
        return 0;

    }

    /**
     * 删除单个文件
     *
     * @param sPath 被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }



    /**
     * 获取图片的宽度和高度尺寸
     *
     * @param img
     * @return
     */
    public static int[] getSize(File img) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(img);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int width = image.getWidth();
        int height = image.getHeight();
        int[] size = {width, height};
        return size;
    }
}
