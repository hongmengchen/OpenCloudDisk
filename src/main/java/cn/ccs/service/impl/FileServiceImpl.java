package cn.ccs.service.impl;

import cn.ccs.dao.UserDao;
import cn.ccs.pojo.User;
import cn.ccs.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.io.File;

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
}
