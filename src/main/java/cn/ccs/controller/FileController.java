package cn.ccs.controller;

import cn.ccs.pojo.FileCustom;
import cn.ccs.pojo.Result;
import cn.ccs.pojo.SummaryFile;
import cn.ccs.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/file")
public class FileController {

    // 注入request对象，用于获取请求相关的信息，比如会话、请求参数等
    @Autowired
    private HttpServletRequest request;
    // 注入文件服务层接口，通过它调用各种文件相关的业务逻辑方法
    @Autowired
    private FileService fileService;

    /**
     * 获取文件列表的方法
     *
     * @param path 文件路径参数，用于指定要获取文件列表的路径
     * @return 返回一个Result对象，其中封装了获取文件列表操作的结果状态以及实际获取到的文件列表信息（类型为List<FileCustom>）
     */
    @RequestMapping("/getFiles")
    public @ResponseBody Result<List<FileCustom>> getFiles(String path) {
        // 通过文件服务层获取指定路径对应的真实文件路径
        String realPath = fileService.getFileName(request, path);
        // 获取该真实路径下所有的文件信息，返回一个FileCustom类型的列表
        List<FileCustom> listFile = fileService.listFile(realPath);
        // 创建一个Result对象，用于封装操作结果相关信息，此处传入状态码325，表示获取成功的一种标识，同时传入操作成功的标识true以及提示信息"获取成功"
        Result<List<FileCustom>> result = new Result<List<FileCustom>>(325,
                true, "获取成功");
        // 将获取到的文件列表信息设置到Result对象中，方便后续返回给调用者
        result.setData(listFile);
        return result;
    }

    /**
     * 添加文件夹的方法
     *
     * @param currentPath    当前所在的文件路径，用于确定添加文件夹的父级路径
     * @param directoryName  要添加的文件夹名称
     * @return 返回一个Result对象，根据操作是否成功封装不同的状态码、操作结果标识以及提示信息，成功则返回状态码336、操作成功标识true以及"添加成功"提示信息，失败则返回相应的失败状态码、操作失败标识false以及"添加失败"提示信息
     */
    @RequestMapping("/addDirectory")
    public @ResponseBody Result<String> addDirectory(String currentPath, String directoryName) {
        try {
            // 调用文件服务层的方法来添加文件夹，传入当前路径和文件夹名称等信息
            fileService.addDirectory(request, currentPath, directoryName);
            return new Result<>(336, true, "添加成功");
        } catch (Exception e) {
            return new Result<>(331, false, "添加失败");
        }
    }

    /**
     * 文件上传的方法
     *
     * @param files      要上传的文件数组，通过@RequestParam注解指定参数名"files"来接收前端传递过来的多个文件
     * @param currentPath 当前所在的文件路径，用于确定文件上传的目标路径
     * @return 返回一个Result对象，若文件上传过程中出现异常则返回状态码301、操作失败标识false以及"上传失败"提示信息，若上传成功则返回状态码305、操作成功标识true以及"上传成功"提示信息
     */
    @RequestMapping("/upload")
    public @ResponseBody Result<String> upload(
            @RequestParam("files") MultipartFile[] files, String currentPath) {
        try {
            // 调用文件服务层的方法来执行文件上传操作，传入请求对象、文件数组以及目标路径等信息
            fileService.uploadFilePath(request, files, currentPath);
        } catch (Exception e) {
            return new Result<>(301, false, "上传失败");
        }
        return new Result<String>(305, true, "上传成功");
    }

    /**
     * 文件下载的方法
     *
     * @param currentPath 当前路径，用于确定文件所在的目录路径
     * @param downPath    文件名（此处可能是文件名数组，根据实际需求来定，代码中是数组形式传入），用于指定要下载的具体文件
     * @param username    用户名，可能用于权限验证或者确定用户专属的文件路径等情况
     * @return 返回一个ResponseEntity<byte[]>对象，其中封装了文件的字节流数据以及相关的文件头信息，用于实现文件下载功能，如果出现异常则返回null
     */
    @RequestMapping("/download")
    public ResponseEntity<byte[]> download(String currentPath, String[] downPath, String username) {
        try {
            // 获取文件名，此处又通过request.getParameter方法获取了一次文件名，可能与downPath参数存在重复获取的情况，可根据实际需求检查是否需要保留此操作，这里暂时按原代码逻辑注释
            String down = request.getParameter("downPath");
            // 通过文件服务层获取要下载的文件对象，传入请求对象、当前路径、文件名数组以及用户名等信息
            File downloadFile = fileService.downPackage(request, currentPath, downPath, username);
            // 创建HttpHeaders对象，用于设置文件下载相关的头部信息
            HttpHeaders headers = new HttpHeaders();
            // 设置内容类型为APPLICATION_OCTET_STREAM，表示二进制流数据，适合文件下载场景
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            // 设置文件名，先将文件名从utf-8编码转换为字节数组，再转换为iso-8859-1编码，这是为了处理文件名在不同编码环境下的兼容性问题，确保文件名能正确显示在下载对话框中
            String fileName = new String(downloadFile.getName().getBytes("utf-8"), "iso-8859-1");
            headers.setContentDispositionFormData("attachment", fileName);
            // 将文件读取为字节数组，使用了Apache Commons IO库提供的方法来实现文件到字节数组的转换
            byte[] fileToByteArray = org.apache.commons.io.FileUtils.readFileToByteArray(downloadFile);
            // 调用文件服务层的方法删除下载相关的临时文件或者压缩包等（根据具体业务逻辑来定）
            fileService.deleteDownPackage(downloadFile);
            // 返回包含文件字节数组、文件头信息以及状态码为HttpStatus.CREATED（表示创建成功，此处可根据实际情况调整合适的状态码）的ResponseEntity对象，用于将文件流返回给客户端实现下载功能
            return new ResponseEntity<byte[]>(fileToByteArray, headers, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 删除文件夹的方法
     *
     * @param currentPath    当前所在的文件路径，用于确定要删除文件夹的位置
     * @param directoryName  要删除的文件夹名称数组，用于指定多个要删除的文件夹
     * @return 返回一个Result对象，若删除操作成功则返回状态码346、操作成功标识true以及"删除成功"提示信息，若操作过程中出现异常则返回状态码341、操作失败标识false以及"删除失败"提示信息
     */
    @RequestMapping("/delDirectory")
    public @ResponseBody Result<String> delDirectory(String currentPath, String[] directoryName) {
        try {
            // 调用文件服务层的方法来执行删除文件夹操作，传入请求对象、当前路径以及要删除的文件夹名称数组等信息
            fileService.delDirectory(request, currentPath, directoryName);
            return new Result<>(346, true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result<>(341, false, "删除失败");
        }
    }

    /**
     * 根据正则表达式和其他条件搜索文件
     *
     * @param reg         正则表达式，用于匹配文件名
     * @param currentPath 当前路径，指定在哪个目录下进行搜索
     * @param regType     正则表达式类型，可能用于细化搜索条件
     * @return 返回一个Result对象，包含搜索结果列表如果搜索成功，否则返回失败信息
     */
    @RequestMapping("/searchFile")
    public @ResponseBody Result<List<FileCustom>> searchFile(String reg, String currentPath, String regType) {
        try {
            // 调用fileService的searchFile方法执行实际的文件搜索逻辑
            List<FileCustom> searchFile = fileService.searchFile(request, currentPath, reg, regType);
            // 创建一个成功结果对象，包含状态码、是否成功和成功提示信息
            Result<List<FileCustom>> result = new Result<>(376, true, "查找成功");
            // 设置搜索结果到结果对象中
            result.setData(searchFile);
            // 返回成功结果对象
            return result;
        } catch (Exception e) {
            // 捕获异常并打印堆栈跟踪，便于调试和日志记录
            e.printStackTrace();
            // 返回一个失败结果对象，包含状态码、是否成功和失败提示信息
            return new Result<>(371, false, "查找失败");
        }
    }

    /**
     * 处理summarylist请求，展示摘要列表页面
     *
     * @param model Model对象，用于添加模型属性
     * @return 返回摘要列表页面的视图名称
     */
    @RequestMapping("/summarylist")
    public String summarylist(Model model) {
        String webrootpath = fileService.getFileName(request, "");
        int number = webrootpath.length();
        SummaryFile rootlist = fileService.summarylistFile(webrootpath, number);
        model.addAttribute("rootlist", rootlist);
        return "summarylist";
    }

    /**
     * 复制目录控制器
     *
     * 该方法通过接收当前目录路径、目录名称数组和目标目录路径作为参数，
     * 调用fileService的copyDirectory方法来实现目录的复制操作
     * 主要用于处理目录复制的请求
     *
     * @param currentPath 当前目录路径，表示需要复制的目录所在的位置
     * @param directoryName 目录名称数组，表示需要复制的一个或多个目录的名称
     * @param targetdirectorypath 目标目录路径，表示目录将被复制到的位置
     * @return 返回一个Result对象，包含复制操作的结果信息，包括状态码、是否成功和提示信息
     * @throws Exception 如果复制过程中发生IO异常，将被捕获并处理
     */
    @RequestMapping("/copyDirectory")
    public @ResponseBody Result<String> copyDirectory(String currentPath,String[] directoryName, String targetdirectorypath) throws Exception {
        try {
            fileService.copyDirectory(request, currentPath, directoryName,
                    targetdirectorypath);
            return new Result<>(366, true, "复制成功");
        } catch (IOException e) {
            return new Result<>(361, true, "复制失败");
        }
    }

     // 重命名
    @RequestMapping("/renameDirectory")
    public @ResponseBody Result<String> renameDirectory(String currentPath,    String srcName, String destName) {
        try {
            fileService.renameDirectory(request, currentPath, srcName, destName);
            return new Result<>(356, true, "重命名成功");
        } catch (Exception e) {
            return new Result<>(351, false, "重命名失败");
        }
    }

}