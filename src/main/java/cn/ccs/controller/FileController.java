package cn.ccs.controller;

import cn.ccs.pojo.FileCustom;
import cn.ccs.pojo.Result;
import cn.ccs.service.impl.FileServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.List;

@Controller
@RequestMapping("/file")
public class FileController {
    private final HttpServletRequest request;
    private final FileServiceImpl fileService;

    @Autowired
    public FileController(FileServiceImpl fileService, HttpServletRequest request) {
        this.fileService = fileService;
        this.request = request;
    }

    /**
     * 获取文件列表
     *
     * @param path 路径
     * @return Json对象
     */
    @RequestMapping("/getFiles")
    public @ResponseBody Result<List<FileCustom>> getFiles(String path) {
        //根据项目路径及用户名、文件名获取上传文件的真实路径
        // WEB-INF/file/ username/ fileName
        String realPath = fileService.getFileName(request, path);
        //获取路径下所有的文件信息
        List<FileCustom> listFile = fileService.listFile(realPath);
        //将文件信息封装成Json对象
        Result<List<FileCustom>> result = new Result<>(325, true, "获取成功");
        result.setData(listFile);
        return result;
    }

    /**
     * 文件下载
     *
     * @param currentPath 当前路径
     * @param downPath    文件名
     * @param username    用户名
     * @return 文件下载流
     */
    @RequestMapping("/download")
    public ResponseEntity<byte[]> download(String currentPath, String[] downPath, String username) {
        try {
			// 获取文件名
            String down = request.getParameter("downPath");
			// 获取文件
            File downloadFile = fileService.downPackage(request, currentPath, downPath, username);
			// 设置文件头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			// 设置文件名
            String fileName = new String(downloadFile.getName().getBytes("utf-8"), "iso-8859-1");
            headers.setContentDispositionFormData("attachment", fileName);
			// 读取文件
            byte[] fileToByteArray = org.apache.commons.io.FileUtils.readFileToByteArray(downloadFile);
            fileService.deleteDownPackage(downloadFile);
			// 返回文件流
            return new ResponseEntity<byte[]>(fileToByteArray, headers, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
