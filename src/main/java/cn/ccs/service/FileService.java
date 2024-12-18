package cn.ccs.service;

import cn.ccs.pojo.FileCustom;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.List;

/**
 * 文件服务接口，提供文件相关的操作
 */

public interface FileService {
    //添加新的命名空间
    void addNewNameSpace(HttpServletRequest request, String namespace);

    //获取根目录
    String getRootPath(HttpServletRequest request);

    public String getFileName(HttpServletRequest request, String fileName);

    public String getFileName(HttpServletRequest request, String fileName, String username);

    public List<FileCustom> listFile(String realPath);

    // 下载文件打包
    public File downPackage(HttpServletRequest request, String currentPath, String[] fileNames, String username) throws Exception;

    // 删除压缩文件包
    public void deleteDownPackage(File downloadFile);


}

