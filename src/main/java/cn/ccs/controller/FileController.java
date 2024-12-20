package cn.ccs.controller;

import cn.ccs.pojo.FileCustom;
import cn.ccs.pojo.RecycleFile;
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
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 控制器类，用于文件管理
 */

@Controller
@RequestMapping("/file")
public class FileController {
    // 注入HttpServletRequest对象
    private final HttpServletRequest request;
    // 注入文件服务层对象
    private final FileService fileService;

    /**
     * 构造函数，用于注入HttpServletRequest对象和文件服务层对象
     *
     * @param request
     * @param fileService
     */
    @Autowired
    public FileController(HttpServletRequest request, FileService fileService) {
        this.request = request;
        this.fileService = fileService;
    }

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
     * @param currentPath   当前所在的文件路径，用于确定添加文件夹的父级路径
     * @param directoryName 要添加的文件夹名称
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
     * @param files       要上传的文件数组，通过@RequestParam注解指定参数名"files"来接收前端传递过来的多个文件
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
     * @return 返回一个ResponseEntity<byte [ ]>对象，其中封装了文件的字节流数据以及相关的文件头信息，用于实现文件下载功能，如果出现异常则返回null
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
     * @param currentPath   当前所在的文件路径，用于确定要删除文件夹的位置
     * @param directoryName 要删除的文件夹名称数组，用于指定多个要删除的文件夹
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
        // 将摘要列表添加到模型中
        model.addAttribute("rootlist", rootlist);

        return "summarylist";
    }

    /**
     * 复制目录控制器
     * <p>
     * 该方法通过接收当前目录路径、目录名称数组和目标目录路径作为参数，
     * 调用fileService的copyDirectory方法来实现目录的复制操作
     * 主要用于处理目录复制的请求
     *
     * @param currentPath         当前目录路径，表示需要复制的目录所在的位置
     * @param directoryName       目录名称数组，表示需要复制的一个或多个目录的名称
     * @param targetdirectorypath 目标目录路径，表示目录将被复制到的位置
     * @return 返回一个Result对象，包含复制操作的结果信息，包括状态码、是否成功和提示信息
     * @throws Exception 如果复制过程中发生IO异常，将被捕获并处理
     */
    @RequestMapping("/copyDirectory")
    public @ResponseBody Result<String> copyDirectory(String currentPath, String[] directoryName, String targetdirectorypath) throws Exception {
        try {
            fileService.copyDirectory(request, currentPath, directoryName,
                    targetdirectorypath);
            return new Result<>(366, true, "复制成功");
        } catch (IOException e) {
            return new Result<>(361, true, "复制失败");
        }
    }

    /**
     * 重命名目录的请求映射
     *
     * @param currentPath 当前路径
     * @param srcName     源目录名
     * @param destName    目标目录名
     */
    @RequestMapping("/renameDirectory")
    public @ResponseBody Result<String> renameDirectory(String currentPath, String srcName, String destName) {
        try {
            fileService.renameDirectory(request, currentPath, srcName, destName);
            return new Result<>(356, true, "重命名成功");
        } catch (Exception e) {
            return new Result<>(351, false, "重命名失败");
        }
    }

    /**
     * 移动目录控制器
     * <p>
     * 该方法负责处理移动目录的请求，它接收当前路径、目录名称数组和目标路径作为参数，
     * 并调用FileService的moveDirectory方法来执行目录移动操作
     *
     * @param currentPath         当前路径，表示需要移动的目录所在的位置
     * @param directoryName       目录名称数组，表示需要移动的一个或多个目录的名称
     * @param targetdirectorypath 目标路径，表示目录移动的目的地
     * @return 返回一个Result对象，包含移动操作的结果信息，包括状态码、是否成功和提示信息
     */
    @RequestMapping("/moveDirectory")
    public @ResponseBody Result<String> moveDirectory(String currentPath, String[] directoryName, String targetdirectorypath) {
        try {
            fileService.moveDirectory(request, currentPath, directoryName, targetdirectorypath);
            return new Result<>(366, true, "移动成功");
        } catch (Exception e) {
            return new Result<>(361, true, "移动失败");
        }
    }

    /**
     * 处理回收站文件展示请求
     * <p>
     * 该方法负责从文件服务中获取已删除（回收站内）的文件信息，并将其设置到请求属性中，
     * 以便在页面上展示这些文件信息
     *
     * @return 返回"recycle"字符串，用于展示回收站文件的页面
     */
    @RequestMapping("/recycleFile")
    public String recycleFile() {
        try {
            // 调用文件服务，获取已删除的文件列表
            List<RecycleFile> findDelFile = fileService.recycleFiles(request);
            // 如果获取到的文件列表不为空，则将其设置到请求属性中
            if (null != findDelFile && findDelFile.size() != 0) {
                request.setAttribute("findDelFile", findDelFile);
            }
        } catch (Exception e) {
            // 异常处理：打印异常信息，通常在生产环境中应避免直接打印堆栈跟踪
            e.printStackTrace();
        }
        // 返回逻辑视图名称，用于展示回收站文件的页面
        return "recycle";
    }

    /**
     * 还原目录
     * <p>
     * 该方法通过文件ID数组还原（恢复）指定的目录此方法使用@RequestMapping注解来处理HTTP请求，
     * 并返回一个Result对象，其中包含处理结果和消息
     *
     * @param fileId 文件ID数组，标识需要还原的目录
     * @return 返回一个Result对象，包含处理状态码、是否成功以及操作消息
     */
    @RequestMapping("/revertDirectory")
    public @ResponseBody Result<String> revertDirectory(int[] fileId) {
        try {
            // 调用FileService中的方法来实际执行目录的还原操作
            fileService.revertDirectory(request, fileId);
            // 如果还原成功，返回成功状态码和成功消息
            return new Result<>(327, true, "还原成功");
        } catch (Exception e) {
            // 如果还原过程中发生异常，返回失败状态码和失败消息
            return new Result<>(322, false, "还原失败");
        }
    }

    /**
     * 处理回收站中文件的删除请求
     * 该方法接收一个文件ID数组，尝试从回收站中彻底删除这些文件
     *
     * @param fileId 包含待删除文件ID的数组
     * @return 返回一个Result对象，包含删除操作的结果信息
     */
    @RequestMapping("/delRecycle")
    public @ResponseBody Result<String> delRecycleDirectory(int fileId[]) {
        try {
            // 调用FileService中的delRecycle方法执行删除操作
            fileService.delRecycle(request, fileId);
            // 如果删除成功，返回一个包含成功信息的Result对象
            return new Result<>(327, true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            // 如果删除失败，打印异常信息并返回一个包含失败信息的Result对象
            return new Result<>(322, false, "删除失败");
        }
    }

    /**
     * 清空回收站
     * <p>
     * 该方法用于清空回收站，删除所有在回收站中的目录该方法使用@RequestMapping注解来处理HTTP请求，
     * 并返回一个Result对象，其中包含处理结果和消息
     *
     * @return 返回一个Result对象，包含处理状态码、是否成功以及操作消息
     */
    @RequestMapping("/delAllRecycle")
    public @ResponseBody Result<String> delAllRecycleDirectory() {
        try {
            // 调用FileService中的方法来实际执行清空回收站的操作
            fileService.delAllRecycle(request);
            // 返回状态码
            return new Result<>(327, true, "删除成功");
        } catch (Exception e) {
            // 如果清空回收站过程中发生异常，返回失败状态码和失败消息
            return new Result<>(322, false, "删除失败");
        }
    }

    /**
     * 开放文件访问接口
     * 该方法旨在通过HTTP请求响应，提供文件下载或访问功能
     *
     * @param response    用于向客户端返回文件的HTTP响应对象
     * @param currentPath 文件所在的当前路径，用于定位文件位置
     * @param fileName    文件名，用于指定需要开放的文件
     * @param fileType    文件类型，可能用于决定文件的处理方式或MIME类型
     */
    @RequestMapping("/openFile")
    public void openFile(HttpServletResponse response, String currentPath,
                         String fileName, String fileType) {
        try {
            // 调用服务层方法，处理文件响应逻辑
            fileService.respFile(response, request, currentPath, fileName,
                    fileType);
        } catch (IOException e) {
            // 捕获IO异常，通常表示文件读取或网络传输问题
            e.printStackTrace();
        }
    }

    /**
     * 处理打开音频页面的请求
     *
     * @param model       用于传递数据到视图的Model对象
     * @param currentPath 当前文件路径，用于定位文件位置
     * @param fileName    文件名，用于标识具体音频文件
     * @return 返回音频页面的视图名称
     */
    @RequestMapping("/openAudioPage")
    public String openAudioPage(Model model, String currentPath, String fileName) {
        // 将当前路径添加到模型中，以便在页面上显示或进一步操作
        model.addAttribute("currentPath", currentPath);
        // 将文件名添加到模型中，用于在页面上标识或操作特定的音频文件
        model.addAttribute("fileName", fileName);
        // 返回音频页面的视图名称，这里的"audio"应该对应一个实际的页面模板文件
        return "audio";
    }

    /**
     * 使用@RequestMapping注解来映射HTTP请求到处理方法
     * 该方法的目的是处理对"/openOffice"路径的请求，尝试打开一个Office文档并返回结果
     *
     * @param currentPath 当前文件路径，用于定位文件位置
     * @param fileName    文件名，用于识别和处理特定的文件
     * @param fileType    文件类型，虽然在方法体内未直接使用，但可能用于前期验证或处理逻辑
     * @return 返回一个Result对象，包含处理结果和相关数据
     */
    @RequestMapping("/openOffice")
    public @ResponseBody Result<String> openOffice(String currentPath,
                                                   String fileName,
                                                   String fileType) {
        try {
            // 调用fileService的openOffice方法来处理文件
            String openOffice = fileService.openOffice(request, currentPath, fileName);
            if (openOffice != null) {
                // 如果文件成功打开，获取PDF文件路径并保存到session
                String pdfPath = (String) request.getSession().getAttribute(fileName);
                request.getSession().setAttribute("PDFID", openOffice);
                request.getSession().setAttribute(openOffice, pdfPath);
                // 创建一个成功的Result对象，并返回打开的Office文档标识
                Result<String> result = new Result<>(505, true, "打开成功");
                result.setData(openOffice);
                return result;
            }
            // 如果文件打开失败，返回一个表示失败的Result对象
            return new Result<>(501, false, "打开失败");
        } catch (Exception e) {
            // 捕获并打印异常，然后返回一个表示失败的Result对象
            e.printStackTrace();
            return new Result<>(501, false, "打开失败");
        }
    }
}