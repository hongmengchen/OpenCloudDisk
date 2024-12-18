package cn.ccs.service.impl;

import cn.ccs.dao.UserDao;
import cn.ccs.dao.fileDao;
import cn.ccs.dao.officeDao;
import cn.ccs.pojo.FileCustom;
import cn.ccs.pojo.User;
import cn.ccs.service.FileService;
import cn.ccs.utils.FileUtils;
import cn.ccs.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipOutputStream;

@Service("FileService")
public class FileServiceImpl implements FileService {
    // 文件相对前缀
    public static final String PREFIX = "WEB-INF" + File.separator + "file" + File.separator;
    // 新用户注册默认文件夹
    public static final String[] DEFAULT_DIRECTORY = {"vido", "music", "source", "image", User.RECYCLE};

    private final UserDao userDao;

    @Autowired
    public FileServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

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

    public String getFileName(HttpServletRequest request, String fileName) {
        // fileName= fileName.replace("\\", "//");
        if (fileName == null || fileName.equals("\\")) {
            System.out.println(1);
            fileName = "";
        }
        String username = UserUtils.getUsername(request);
        String realpath = getRootPath(request) + username + File.separator + fileName;
        return realpath;

    }

    public String getFileName(HttpServletRequest request, String fileName, String username) {
        if (username == null) {
            return getFileName(request, fileName);
        }
        if (fileName == null) {
            fileName = "";
        }
        return getRootPath(request) + username + File.separator + fileName;
    }

    @Override
    //getRoonPath方法最终返回的是文件：http://locolhost:8080/cn/WEB-INF/file
    public String getRootPath(HttpServletRequest request) {
        String rootPath = request.getSession().getServletContext().getRealPath("/") + PREFIX;
        return rootPath;
    }

    //获取路径下所有的文件信息
    public List<FileCustom> listFile(String realPath) {
        File[] files = new File(realPath).listFiles();
        List<FileCustom> lists = new ArrayList<FileCustom>();
        if (files != null) {
            for (File file : files) {
                if (!file.getName().equals(User.RECYCLE)) {
                    FileCustom custom = new FileCustom();
                    custom.setFileName(file.getName());
                    custom.setLastTime(FileUtils.formatTime(file.lastModified()));
                    custom.setCurrentPath(realPath);
                    if (file.isDirectory()) {
                        custom.setFileSize("-");
                    } else {
                        custom.setFileSize(FileUtils.getDataSize(file.length()));
                    }
                    custom.setFileType(FileUtils.getFileType(file));
                    lists.add(custom);
                }
            }
        }
        return lists;

    }

    public void uploadFilePath(HttpServletRequest request, MultipartFile[] files, String currentPath) throws Exception {
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            String filePath = getFileName(request, currentPath);
            File distFile = new File(filePath, fileName);
            if (!distFile.exists()) {
                file.transferTo(distFile);
                if ("office".equals(FileUtils.getFileType(distFile))) {
                    try {
                        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
                        String documentId = FileUtils.getDocClient().createDocument(distFile, fileName, suffix).getDocumentId();
                        officeDao.addOffice(documentId, FileUtils.MD5(distFile));
                    } catch (Exception e) {
                    }
                }
            }
        }
        reSize(request);
    }
    public void reSize(HttpServletRequest request) {
        String rootPath = getRootPath(request);
        File rootDir = new File(rootPath);
        if (rootDir.exists() && rootDir.isDirectory()) {
            List<File> imageFiles = new ArrayList<>();
            File[] files = rootDir.listFiles();
            if (files!= null) {
                for (File file : files) {
                    if (file.isFile() && isImageFile(file)) {
                        imageFiles.add(file);
                    }
                }
            }
            for (File imageFile : imageFiles) {
                try {
                    BufferedImage originalImage = ImageIO.read(imageFile);
                    int newWidth = (int) (originalImage.getWidth() * 0.5);
                    int newHeight = (int) (originalImage.getHeight() * 0.5);
                    BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
                    resizedImage.createGraphics().drawImage(originalImage, 0, 0, newWidth, newHeight, null);
                    ImageIO.write(resizedImage, getFileExtension(imageFile.getName()), imageFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean isImageFile(File file) {
        String fileName = file.getName();
        String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        return fileExtension.equals("jpg") || fileExtension.equals("jpeg") || fileExtension.equals("png") || fileExtension.equals("gif");
    }

    private String getFileExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index > 0) {
            return fileName.substring(index + 1);
        }
        return "";
    }

    public void delDirectory(HttpServletRequest request, String currentPath, String[] directoryName) throws Exception {
        for (String fileName : directoryName) {
            //拼接源文件的地址
            String srcPath = currentPath + File.separator + fileName;
            //根据源文件相对地址拼接 绝对路径
            File src = new File(getFileName(request, srcPath));//即将删除的文件地址
            File dest = new File(getRecyclePath(request));//回收站目录地址
            //调用commons.jar包中的moveToDirectory移动文件,移至回收站目录
            org.apache.commons.io.FileUtils.moveToDirectory(src, dest, true);
            //保存本条删除信息
            fileDao.insertFiles(srcPath, UserUtils.getUsername(request));
        }
        //重新计算文件大小
        reSize(request);
    }
    public String getRecyclePath(HttpServletRequest request) {
        return getFileName(request, User.RECYCLE);
    }

    // 下载文件打包
    @Override
    public File downPackage(HttpServletRequest request, String currentPath, String[] fileNames, String username) {
        // 获取文件名
        File downloadFile = null;
		if (currentPath == null) {
			currentPath = "";
		}
        // 判断是否为单个文件
		if (fileNames.length == 1) {
			downloadFile = new File(getFileName(request, currentPath, username), fileNames[0]);//返回绝对路径名
			if (downloadFile.isFile()) {
				return downloadFile;
			}
		}
        return null;
		/*String[] sourcePath = new String[fileNames.length];
		for (int i = 0; i < fileNames.length; i++) {
			sourcePath[i] = getFileName(request, currentPath, username) + File.separator + fileNames[i];
		}
		String packageZipName = packageZip(sourcePath);
		downloadFile = new File(packageZipName);
		return downloadFile;*/
    }

    // 删除压缩文件包
	public void deleteDownPackage(File downloadFile) {
		if (downloadFile.getName().endsWith(".zip")) {
			downloadFile.delete();
		}
	}
}





