package com.project.platform.service;

import com.project.platform.vo.FileInfoVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface FileService {

    FileInfoVO upload(MultipartFile file) throws IOException, NoSuchAlgorithmException;

    File getFile(String fileName) throws IOException;

    /**
     * 删除文件
     * @param fileName 文件名
     * @return 是否删除成功
     */
    boolean deleteFile(String fileName);

    /**
     * 从URL中提取文件名并删除
     * @param fileUrl 文件URL
     * @return 是否删除成功
     */
    boolean deleteFileByUrl(String fileUrl);
}
