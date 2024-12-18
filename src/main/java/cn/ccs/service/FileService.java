package cn.ccs.service;

import cn.ccs.pojo.FileCustom;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface FileService {
    //添加新的命名空间
    void addNewNameSpace(HttpServletRequest request, String namespace);
    //获取根目录
    String getRootPath(HttpServletRequest request);
    public String getFileName(HttpServletRequest request, String fileName) ;
    public String getFileName(HttpServletRequest request, String fileName, String username) ;
    public List<FileCustom> listFile(String realPath) ;



    }

