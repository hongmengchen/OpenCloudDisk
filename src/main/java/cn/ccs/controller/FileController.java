package cn.ccs.controller;

import cn.ccs.pojo.File;
import cn.ccs.pojo.FileCustom;
import cn.ccs.pojo.Result;
import cn.ccs.service.impl.FileServiceImpl;
import org.apache.ibatis.annotations.ResultMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
@Controller
@RequestMapping("/file")
public class FileController {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private FileServiceImpl fileService;
    @RequestMapping("/getFiles")
    public @ResponseBody Result<List<FileCustom>> getFiles(String path){
        //根据项目路径及用户名、文件名获取上传文件的真实路径
        String realPath = fileService.getFileName(request, path);
        //获取路径下所有的文件信息
        List<FileCustom> listFile = fileService.listFile(realPath);
        //将文件信息封装成Json对象
        Result<List<FileCustom>> result = new Result<List<FileCustom>>(325, true, "获取成功");
        result.setData(listFile);
        return result;
    }
}
