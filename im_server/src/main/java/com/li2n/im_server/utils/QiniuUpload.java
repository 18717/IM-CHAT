package com.li2n.im_server.utils;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * 七牛云上传文件
 *
 * @author 一杯香梨
 * @version 1.0
 * @date 2022-02-04 20:51
 */
public class QiniuUpload {

    /**
     * AK
     */
    private static final String ACCESS_KEY = "aJ-MKqFuu5SFDcoC-iSGdySExr51Mbu6q4Nl8Yr3";
    /**
     * SK
     */
    private static final String SECRET_KEY = "s0dId_qRpYHVZxhe2oy5lftJCzAPX-eQTMI5Lu_F";
    /**
     * 存储空间名
     */
    private static final String BUCKET = "li2n";
    /**
     * 文件存储域名
     */
    private static final String DOMAIN_OF_BUCKET = "cdn.lrxya.icu";


    /**
     * 获取上传凭证
     *
     * @return 上传凭证
     */
    public static String getUploadCredential() {
        return Auth.create(ACCESS_KEY, SECRET_KEY).uploadToken(BUCKET);
    }

    /**
     * 上传文件1
     *
     * @param zone
     * @param uploadToken
     * @param localFilePath
     * @param saveFileName
     * @return
     */
    public static String fileUpload(Zone zone, String uploadToken, String localFilePath, String saveFileName) {
        // 构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(zone);
        UploadManager uploadManager = new UploadManager(cfg);
        // 默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = "im_avatar/" + saveFileName;
        try {
            Response response = uploadManager.put(localFilePath, key, uploadToken);
            // 解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            System.out.println(putRet.key);
            System.out.println(putRet.hash);
            return BUCKET + saveFileName;
        } catch (QiniuException e) {
            Response response = e.response;
            System.out.println(response.toString());
            try {
                System.out.println(response.bodyString());
            } catch (QiniuException ignored) {

            }
        }
        return null;
    }

    /**
     * 上传文件2
     *
     * @param file
     * @param zone
     * @param uploadToken
     * @param fileName    保存文件名
     * @return 文件名
     * @throws Exception
     */
    public static String updateFile(MultipartFile file, Zone zone, String uploadToken, String fileName, String folder) {

        // 获取到后缀名的开始索引值
        int index = fileName.lastIndexOf(".");
        String notSuffixFileName = fileName.substring(0, index);
        String fileSuffix = fileName.substring(index);

        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = folder + fileName;
        //判断空间中是否存在同名文件，存在则重新命名
        if (fileIsExist(publicFile(key))) {
            key = folder + notSuffixFileName + "【" + System.currentTimeMillis() + "】" + fileSuffix;
        }

        try {
            InputStream inputStream = file.getInputStream();
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            //buff用于存放循环读取的临时数据
            byte[] buff = new byte[1024];
            int rc;
            while ((rc = inputStream.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }

            byte[] uploadBytes = swapStream.toByteArray();

            try {
                Configuration cfg = new Configuration(zone);
                UploadManager uploadManager = new UploadManager(cfg);
                Response response = uploadManager.put(uploadBytes, key, uploadToken);
                //解析上传成功的结果
                DefaultPutRet putRet;
                putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                // 返回文件名
                return putRet.key;

            } catch (QiniuException ex) {
                Response r = ex.response;
                System.err.println(r.toString());
                try {
                    System.err.println(r.bodyString());
                } catch (QiniuException ignored) {
                }
            }
        } catch (IOException ignored) {
        }
        return null;
    }


    /**
     * 公有空间返回文件URL
     *
     * @param fileName 文件名
     * @return
     */
    public static String publicFile(String fileName) {
        if (fileName == null) {
            return "服务器错误，上传失败";
        }
        String encodedFileName = null;
        try {
            encodedFileName = URLEncoder.encode(fileName, "utf-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            System.err.println(e.getMessage());
        }
        String finalUrl = String.format("%s/%s", DOMAIN_OF_BUCKET, encodedFileName);
        return "http://" + finalUrl;
    }

    /**
     * 判断文件是否已经存在
     *
     * @param url 文件路径
     * @return 存在true，不存在false
     */
    public static Boolean fileIsExist(String url) {
        Object content = null;
        try {
            URL u = new URL(url);
            content = u.getContent();
        } catch (IOException ignored) {
        }
        return content != null;
    }

}
