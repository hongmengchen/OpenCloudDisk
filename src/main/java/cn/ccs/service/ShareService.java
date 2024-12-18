package cn.ccs.service;

import cn.ccs.pojo.ShareFile;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 分享服务接口
 */

@Service
public interface ShareService {
    // 根据分享链接查找分享文件
    List<ShareFile> findShare(HttpServletRequest request, String shareUrl) throws Exception;

    // 分享文件
    String shareFile(HttpServletRequest request, String currentPath, String[] shareFile) throws Exception;

    // 查找分享文件
    List<ShareFile> findShareByName(HttpServletRequest request, int status) throws Exception;

    // 取消分享
    String cancelShare(String url, String filePath, int status) throws Exception;
}
