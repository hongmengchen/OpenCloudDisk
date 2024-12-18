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

/**
 *  分享服务实现类
 */

@Service("shareService")
public class ShareServiceImpl implements ShareService {
    //依赖注入
    private final ShareDao shareDao;

    /**
     * 注入shareDao
     */
    @Autowired
    public ShareServiceImpl(ShareDao shareDao) {
        this.shareDao = shareDao;
    }

    /**
     * 根据分享链接查找分享的文件
     *<p>
     * 此方法用于处理根据给定的分享链接获取公开分享的文件列表的请求它首先创建一个Share对象，
     * 设置其分享链接和状态为PUBLIC，然后调用shareDao.findShare方法查询数据库中匹配的分享记录，
     * 最后调用getShareFile方法处理并返回分享的文件列表
     *
     * @param request HTTP请求对象，用于获取请求相关信息
     * @param shareUrl 分享链接，用于查询特定的分享记录
     * @return 返回一个ShareFile对象列表，代表分享的文件列表
     * @throws Exception 如果在处理过程中遇到任何错误，抛出此异常
     */
    @Override
    public List<ShareFile> findShare(HttpServletRequest request, String shareUrl) throws Exception{
        Share share = new Share();
        share.setShareUrl(shareUrl);
        share.setStatus(Share.PUBLIC);
        List<Share> shares = shareDao.findShare(share);
        return getShareFile(request, shares);
    }

    /**
     * 处理分享文件列表
     *<p>
     * 此方法用于处理根据给定的分享记录列表获取分享文件的请求，它首先判断分享记录列表是否为空，
     * 如果为空，则返回null，否则创建一个ShareFile对象列表，用于存储分享文件信息。
     * 然后遍历分享记录列表，获取每个分享记录的分享用户、分享链接、文件路径等属性，
     * 并根据这些属性创建一个File对象，获取文件类型、文件名、文件大小、最后修改时间等属性，
     * 并创建一个ShareFile对象，将这些属性设置进去，最后将ShareFile对象
     * 添加到ShareFile对象列表中，最后返回ShareFile对象列表。
     */
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

    /**
     * 分享文件
     *<p>
     * 此方法用于处理分享文件的请求，它首先获取当前登录的用户名，然后创建一个Share对象，
     * 设置其分享路径、分享用户、分享链接和状态，然后调用shareDao.shareFile方法将分享记录添加到数据库中，
     * 最后返回分享链接。
     *
     * @param request HTTP请求对象，用于获取请求相关信息
     * @param currentPath 当前路径，用于确定分享文件的位置
     * @param shareFile 要分享的文件列表
     *@return 返回一个字符串，代表分享链接
     */
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

    /**
     * 查询分享文件
     *<p>
     * 此方法用于处理查询分享文件的请求，它首先获取当前登录的用户名，然后创建一个Share对象，
     * 设置其分享用户和状态，然后调用shareDao.findShareByName方法查询数据库中匹配的分享记录，
     * 最后调用getShareFile方法处理并返回分享的文件列表。
     *
     * @param request HTTP请求对象，用于获取请求相关信息
     * @param status 分享状态，用于筛选分享记录
     * @return 返回一个ShareFile对象列表，代表分享的文件列表
     */
    public List<ShareFile> findShareByName(HttpServletRequest request, int status) throws Exception{
        List<Share> shares = shareDao.findShareByName(UserUtils.getUsername(request), status);
        return getShareFile(request, shares);
    }

    /**
     * 取消分享
     *<p>
     * 此方法用于处理取消分享文件的请求，它首先判断取消分享的状态，如果是取消，则调用shareDao.cancelShare方法将分享状态设置为DELETE，
     * 并返回一个字符串"删除成功"，否则调用shareDao.cancelShare方法将分享状态设置为CANCEL，并返回一个字符串"链接已失效"。
     *
     * @param url 分享链接，用于匹配特定的分享记录
     * @param filePath 文件路径，用于匹配特定的分享记录
     *@param status 分享状态，用于判断是否需要取消分享
     * @return 返回一个字符串，代表取消分享的结果
     */
    public String cancelShare(String url, String filePath, int status) throws Exception {
        if(Share.CANCEL == status){
            shareDao.cancelShare(url, filePath, Share.DELETE);
            return "删除成功";
        }else{
            shareDao.cancelShare(url, filePath, Share.CANCEL);
            return "链接已失效";
        }
    }
}
