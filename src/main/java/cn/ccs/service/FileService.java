package cn.ccs.service;

import cn.ccs.pojo.FileCustom;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface FileService {
    //添加新的命名空间
    void addNewNameSpace(HttpServletRequest request, String namespace);
    //获取根目录
    String getRootPath(HttpServletRequest request);
    //获取文件路径
    String getFileName(HttpServletRequest request, String fileName);
    //根据用户名获取文件路径
    String getFileName(HttpServletRequest request, String username, String fileName);
    //获取路径下所有文件
    List<FileCustom> listFile(String path);
}

