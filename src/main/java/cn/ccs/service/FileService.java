package cn.ccs.service;

import cn.ccs.pojo.FileCustom;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.List;

public interface FileService {
    //添加新的命名空间
    void addNewNameSpace(HttpServletRequest request, String namespace);
    //获取根目录
    String getRootPath(HttpServletRequest request);
    public String getFileName(HttpServletRequest request, String fileName) ;
    public String getFileName(HttpServletRequest request, String fileName, String username) ;
    public List<FileCustom> listFile(String realPath) ;
    //新建文件夹
    boolean addDirectory(HttpServletRequest request, String currentPath, String directoryName);
    public void uploadFilePath(HttpServletRequest request, MultipartFile[] files, String currentPath) throws Exception;
    public void delDirectory(HttpServletRequest request, String currentPath, String[] directoryName) throws Exception;
    // 下载文件打包
    public File downPackage(HttpServletRequest request, String currentPath, String[] fileNames, String username) throws Exception;
    // 删除压缩文件包
    public void deleteDownPackage(File downloadFile);
    //获取文件大小
    void reSize(HttpServletRequest request);
    //计算用户已上传文件大小
    String countFileSize(HttpServletRequest request);
    //递归计算文件大小
    long countFileSize(java.io.File srcFile);
    //搜索文件
    List<FileCustom> searchFile(HttpServletRequest request,String currentPath,String reg,String regType);
    //搜索文件名
    String getSearchFileName(HttpServletRequest request, String fileName);
    //递归搜索文件
    void matchFile(HttpServletRequest request, List<FileCustom> list, File dirFile, String reg, String regType);
}

