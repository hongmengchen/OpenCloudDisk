package cn.ccs.controller;

import cn.ccs.pojo.FileCustom;
import cn.ccs.pojo.Result;
import cn.ccs.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/file")
public class FileController {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private FileService fileService;
    @RequestMapping("/getFiles")
    public @ResponseBody Result<List<FileCustom>> getFiles(String path) {
        String realPath = fileService.getFileName(request, path);
        //获取路径下所有的文件信息
        List<FileCustom> listFile = fileService.listFile(realPath);
        //将文件信息封装成Json对象
        Result<List<FileCustom>> result = new Result<List<FileCustom>>(325,
                true, "获取成功");
        result.setData(listFile);
        return result;
    }

    @RequestMapping("/upload")
    public @ResponseBody Result<String> upload(
            @RequestParam("files") MultipartFile[] files, String currentPath) {
        try {
            fileService.uploadFilePath(request, files, currentPath);
        } catch (Exception e) {
            return new Result<>(301, false, "上传失败");
        }
        return new Result<String>(305, true, "上传成功");
    }

}
