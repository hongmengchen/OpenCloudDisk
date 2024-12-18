package cn.ccs.service.impl;

import cn.ccs.dao.UserDao;
import cn.ccs.pojo.FileCustom;
import cn.ccs.pojo.User;
import cn.ccs.service.FileService;
import cn.ccs.utils.FileUtils;
import cn.ccs.utils.UserUtils;
import org.apache.hadoop.mapred.IFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service("FileService")
public class FileServiceImpl implements FileService {
    // 文件相对前缀
    public static final String PREFIX = "WEB-INF" + File.separator + "file" + File.separator;
    // 新用户注册默认文件夹
    public static final String[] DEFAULT_DIRECTORY = { "vido", "music", "source", "image", User.RECYCLE };

    @Autowired
    private UserDao userDao;

    @Override
    public void addNewNameSpace(HttpServletRequest request, String namespace) {
        String fileName = getRootPath(request);
        File file = new File(fileName, namespace);
        file.mkdir();
        for (String newFileName : DEFAULT_DIRECTORY) {
            File newFile = new File(file, newFileName);
            newFile.mkdir();
        }
    }

    @Override
    public String getRootPath(HttpServletRequest request) {
        String rootPath = request.getSession().getServletContext().getRealPath("/") + PREFIX;
        return rootPath;
    }

    @Override
    public String getFileName(HttpServletRequest request, String fileName) {
        fileName= fileName.replace("\\", "//");
        if (fileName == null||fileName.equals("\\")) {
            System.out.println(1);
            fileName = "";
        }
        String username = UserUtils.getUsername(request);
        String realpath=getRootPath(request) + username + File.separator + fileName;
        return realpath;
    }

    @Override
    public String getFileName(HttpServletRequest request,String username, String fileName) {
        if (username == null) {
            return getFileName(request, fileName);
        }
        if (fileName == null) {
            fileName = "";
        }
        return getRootPath(request) + username + File.separator + fileName;
    }

    @Override
    public List<FileCustom> listFile(String path) {
        File[] file = new File(path).listFiles();
        List<FileCustom> lists = new ArrayList<FileCustom>();
        if (file != null) {
            for (File f : file) {
                if (!f.getName().equals(User.RECYCLE)) {
                    FileCustom fileCustom = new FileCustom();
                    fileCustom.setFileName(f.getName());
                    fileCustom.setLastTime(FileUtils.formatTime(f.lastModified()));
                    fileCustom.setCurrentPath(path);
                    if (f.isDirectory()) {
                        fileCustom.setFileSize("~");
                    } else {
                        fileCustom.setFileSize(FileUtils.getDataSize(f.length()));
                    }
                    fileCustom.setFileType(FileUtils.getFileType(f));
                    lists.add(fileCustom);
                }
            }
        }
        return lists;
    }
}
