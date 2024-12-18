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

@Controller
public class ShareController {
    @Autowired
    private ShareService shareService;

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
}
