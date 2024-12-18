package cn.ccs.controller;

import cn.ccs.pojo.Result;
import cn.ccs.pojo.ShareFile;
import cn.ccs.service.ShareService;
import cn.ccs.service.impl.ShareServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 控制器类，用于处理文件分享相关的HTTP请求
 */

@Controller
public class ShareController {
    // 注入ShareService实例
    private final ShareService shareService;

    /**
     * 构造函数，自动注入ShareService实例
     * @param shareService
     */
    @Autowired
    public ShareController(ShareServiceImpl shareService) {
        this.shareService = shareService;
    }

    /**
     * 处理文件分享请求的方法
     *
     * @param request HTTP请求对象，用于获取请求信息
     * @param currentPath 当前路径，用于确定待分享文件的位置
     * @param shareFile 待分享文件的路径数组
     * @return 返回一个包含分享结果的Result对象
     */
    @RequestMapping("/shareFile")
    public @ResponseBody Result<String> shareFile(HttpServletRequest request, String currentPath, String[] shareFile){
        try {
            String shareUrl = shareService.shareFile(request, currentPath, shareFile);
            Result<String> result = new Result<>(405, true, "分享成功");
            result.setData(shareUrl);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new Result<>(401, false, "分享失败");
        }
    }

    /**
     * 处理分享页面的请求，根据分享链接获取分享文件列表
     *
     * @param request HTTP请求对象，用于获取请求信息
     * @param shareUrl 分享链接
     */
    @RequestMapping("/share")
    public String share(HttpServletRequest request, String shareUrl){
        try {
            List<ShareFile> files = shareService.findShare(request, shareUrl);
            request.setAttribute("files", files);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "share";
    }

    /**
     * 处理分享文件列表的请求，根据分享链接获取分享文件列表
     *
     * @param request HTTP请求对象，用于获取请求信息
     * @param status 文件状态，用于筛选分享文件列表
     */
    @RequestMapping("/searchShare")
    public @ResponseBody Result<List<ShareFile>> searchShare(HttpServletRequest request, int status){
        try {
            List<ShareFile> files = shareService.findShareByName(request, status);
            Result<List<ShareFile>> result = new Result<>(415, true, "获取成功");
            result.setData(files);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new Result<>(411, false, "获取失败");
        }
    }

    /**
     * 处理取消分享文件的请求，根据分享链接和文件路径取消分享文件
     *
     * @param url 分享链接
     * @param filePath 文件路径
     * @param status 文件状态，用于确定取消分享文件的操作类型
     */
    @RequestMapping("/cancelShare")
    public @ResponseBody Result<String> cancelShare(String url, String filePath, int status){
        try {
            String msg = shareService.cancelShare(url, filePath, status);
            Result<String> result = new Result<String>(425, true, msg);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new Result<String>(421, false, "删除失败");
        }
    }
}
