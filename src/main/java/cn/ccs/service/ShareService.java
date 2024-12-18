package cn.ccs.service;

import cn.ccs.pojo.ShareFile;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
@Service
public interface ShareService {
    List<ShareFile> findShare(HttpServletRequest request, String shareUrl) throws Exception;
    String shareFile(HttpServletRequest request, String currentPath, String[] shareFile) throws Exception;
    List<ShareFile> findShareByName(HttpServletRequest request, int status) throws Exception;
    String cancelShare(String url, String filePath, int status) throws Exception;
}
