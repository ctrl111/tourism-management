package com.project.platform.service.impl;

import cn.hutool.core.net.url.UrlBuilder;
import com.project.platform.exception.CustomException;
import com.project.platform.service.FileService;
import com.project.platform.vo.FileInfoVO;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Value("${server.ip}")
    private String serverIp;
    @Value("${server.port}")
    private int serverPort;
    @Value("${files.uploads.path}")
    private String basePath;

    @Value("${files.uploads.baseUrl:}")
    private String fileBaseUrl;

    @Value("${files.uploads.max-size:10485760}")
    private long maxFileSize;

    @Value("${files.uploads.allowed-types:jpg,jpeg,png,gif,bmp,webp,pdf,doc,docx}")
    private String allowedTypes;


    public FileInfoVO upload(MultipartFile multipartFile) throws IOException {
        // 验证文件是否为空
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new CustomException("Файл не выбран");
        }

        // 验证文件大小
        if (multipartFile.getSize() > maxFileSize) {
            throw new CustomException("Размер файла превышает лимит: " + (maxFileSize / 1024 / 1024) + " МБ");
        }

        // 获取上传文件扩展名
        String fix = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        if (StringUtils.isBlank(fix)) {
            throw new CustomException("Файл должен иметь расширение");
        }

        // 验证文件类型
        List<String> allowedTypeList = Arrays.asList(allowedTypes.split(","));
        if (!allowedTypeList.contains(fix.toLowerCase())) {
            throw new CustomException("Неподдерживаемый тип файла. Разрешены: " + allowedTypes);
        }

        // 使用UUID生成唯一文件名，确保每个文件独立，删除时不会互相影响
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String newFileName = uuid + "." + fix;
        File newFile = createFile(newFileName);
        
        // 直接转移文件到指定路径
        multipartFile.transferTo(new File(newFile.getAbsolutePath()));
        
        FileInfoVO fileInfoVO = new FileInfoVO();
        fileInfoVO.setUrl(getServer() + "/" + newFileName);
        fileInfoVO.setName(newFileName);
        return fileInfoVO;
    }


    private File createFile(String fileName) throws IOException {
        File file = new File(Paths.get(basePath, fileName).toString());
        if (file.exists()) {
            return file;
        }
        // 判断配置的文件目录是否存在，若不存在则创建一个新的文件目录
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        return file;
    }


    private String getServer() {
        if (StringUtils.isNotEmpty(fileBaseUrl)) {
            return fileBaseUrl;
        }
        String buildUrl = UrlBuilder.create()
                .setScheme("http")
                .setHost(serverIp)
                .setPort(serverPort)
                .addPath("file")
                .build();
        return buildUrl;
    }


    private String getFilePath(String fileName) {
        return basePath + fileName;

    }

    public File getFile(String fileName) throws IOException {
        File file = new File(getFilePath(fileName));
        return file;
    }

    /**
     * 删除文件
     */
    @Override
    public boolean deleteFile(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return false;
        }

        try {
            File file = new File(getFilePath(fileName));
            if (file.exists() && file.isFile()) {
                return file.delete();
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从URL中提取文件名并删除
     */
    @Override
    public boolean deleteFileByUrl(String fileUrl) {
        if (StringUtils.isBlank(fileUrl)) {
            return false;
        }

        try {
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            return deleteFile(fileName);
        } catch (Exception e) {
            return false;
        }
    }
}
