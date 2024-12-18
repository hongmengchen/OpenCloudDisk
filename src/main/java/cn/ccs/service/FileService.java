package cn.ccs.service;

import cn.ccs.pojo.File;
import cn.ccs.pojo.FileCustom;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface FileService {
    //添加新的命名空间
    void addNewNameSpace(HttpServletRequest request, String namespace);
    //获取根目录
    String getRootPath(HttpServletRequest request);
    //获取文件名
    String getFileName(HttpServletRequest request, String fileName) ;
    //根据用户名获得文件名
    String getFileName(HttpServletRequest request, String fileName, String username) ;
    //获取文件列表
    List<FileCustom> listFile(String realPath) ;
    //新建文件夹
    boolean addDirectory(HttpServletRequest request, String currentPath, String directoryName);
}

