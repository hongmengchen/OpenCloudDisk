package cn.ccs.service;

import cn.ccs.pojo.FileCustom;
import cn.ccs.pojo.RecycleFile;
import cn.ccs.pojo.SummaryFile;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 文件服务接口
 */

public interface FileService {
    // 添加新的命名空间
    void addNewNameSpace(HttpServletRequest request, String namespace);

    // 获取根目录
    String getRootPath(HttpServletRequest request);

    // 获取文件路径
    String getFileName(HttpServletRequest request, String fileName);

    // 获取文件路径
    String getFileName(HttpServletRequest request, String fileName, String username);

    // 获取文件列表
    List<FileCustom> listFile(String realPath);

    // 新建文件夹
    boolean addDirectory(HttpServletRequest request, String currentPath, String directoryName);

    // 上传文件
    void uploadFilePath(HttpServletRequest request, MultipartFile[] files, String currentPath) throws Exception;

    // 删除文件夹
    void delDirectory(HttpServletRequest request, String currentPath, String[] directoryName) throws Exception;

    // 下载文件打包
    File downPackage(HttpServletRequest request, String currentPath, String[] fileNames, String username) throws Exception;

    // 删除压缩文件包
    void deleteDownPackage(File downloadFile);

    // 获取文件大小
    void reSize(HttpServletRequest request);

    // 计算用户已上传文件大小
    String countFileSize(HttpServletRequest request);

    // 递归计算文件大小
    long countFileSize(java.io.File srcFile);

    // 搜索文件
    List<FileCustom> searchFile(HttpServletRequest request,String currentPath,String reg,String regType);

    // 搜索文件名
    String getSearchFileName(HttpServletRequest request, String fileName);

    // 递归搜索文件
    void matchFile(HttpServletRequest request, List<FileCustom> list, File dirFile, String reg, String regType);

    // 获取概览文件
    SummaryFile summarylistFile(String realPath, int number);

    // 复制到某一个目录
    void copyDirectory(HttpServletRequest request,String currentPath,String[] directoryName, String targetdirectorypath) throws Exception;

    // 复制文件
    void copyfile(File srcFile, File targetFile) throws IOException;

    // 重命名目录
    boolean renameDirectory(HttpServletRequest request, String currentPath, String srcName, String destName);

    // 移动目录
    void moveDirectory(HttpServletRequest request, String currentPath, String[] directoryName,String targetdirectorypath) throws Exception;

    // 删除文件
    void delFile(File srcFile) throws Exception;

    // 回收站显示所有删除文件
    List<RecycleFile> recycleFiles(HttpServletRequest request) throws Exception;

    // 还原的文件
    void revertDirectory(HttpServletRequest request, int[] fileId) throws Exception;

    // 删除回收站文件
    void delRecycle(HttpServletRequest request, int[] fileId) throws Exception;

    // 清空回收站文件
    void delAllRecycle(HttpServletRequest request) throws Exception;

    // 响应文件流
    void respFile(HttpServletResponse response, HttpServletRequest request, String currentPath, String fileName, String type) throws IOException;
}

