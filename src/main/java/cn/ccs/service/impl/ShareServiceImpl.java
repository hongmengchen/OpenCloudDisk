package cn.ccs.service.impl;

import cn.ccs.dao.ShareDao;
import cn.ccs.pojo.Share;
import cn.ccs.pojo.ShareFile;
import cn.ccs.pojo.User;
import cn.ccs.service.ShareService;
import cn.ccs.utils.FileUtils;
import cn.ccs.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service("shareService")
public class ShareServiceImpl implements ShareService {
    @Autowired
    private ShareDao shareDao;
    @Override
    public List<ShareFile> findShare(HttpServletRequest request, String shareUrl) throws Exception{
        Share share = new Share();
        share.setShareUrl(shareUrl);
        share.setStatus(Share.PUBLIC);
        List<Share> shares = shareDao.findShare(share);
        return getShareFile(request, shares);
    }

    private List<ShareFile> getShareFile(HttpServletRequest request, List<Share> shares){
        List<ShareFile> files = null;
        if(shares != null){
            files = new ArrayList<ShareFile>();
            String rootPath = request.getSession().getServletContext().getRealPath("/") + FileServiceImpl.PREFIX;
            for (Share share : shares) {
                File file = new File(rootPath + share.getShareUser(), share.getPath());
                ShareFile shareFile = new ShareFile();
                shareFile.setFileType(FileUtils.getFileType(file));
                shareFile.setFileName(file.getName());
                shareFile.setFileSize(file.isFile() ? FileUtils.getDataSize(file.length()) : "-");
                shareFile.setLastTime(FileUtils.formatTime(file.lastModified()));
                shareFile.setShareUser(share.getShareUser());
                shareFile.setUrl(share.getShareUrl());
                shareFile.setFilePath(share.getPath());
                files.add(shareFile);
            }
        }
        return files;
    }

    public String shareFile(HttpServletRequest request, String currentPath, String[] shareFile) throws Exception {
        String username = (String) request.getSession().getAttribute(User.NAMESPACE);
        String shareUrl = FileUtils.getUrl8();
        for (String file : shareFile) {
            Share share = new Share();
            share.setPath(currentPath + File.separator + file);
            share.setShareUser(username);
            share.setShareUrl(shareUrl);
            shareDao.shareFile(share);
        }
        return shareUrl;
    }
}
