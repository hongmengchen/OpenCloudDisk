package cn.ccs.service.impl;

import cn.ccs.dao.UserDao;
import cn.ccs.dao.FileDao;
import cn.ccs.dao.OfficeDao;
import cn.ccs.pojo.FileCustom;
import cn.ccs.pojo.SummaryFile;
import cn.ccs.pojo.User;
import cn.ccs.service.FileService;
import cn.ccs.utils.FileUtils;
import cn.ccs.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static jdk.nashorn.internal.objects.NativeError.getFileName;

@Service("FileService")
public class FileServiceImpl implements FileService {
    // 文件相对前缀，用于构建文件在项目中的相对存储路径，指向项目内特定的文件存储目录
    public static final String PREFIX = "WEB-INF" + File.separator + "file" + File.separator;
    // 新用户注册默认文件夹数组，定义了新用户创建时默认会生成的文件夹名称列表，包含如视频、音乐等不同类型的文件夹以及回收站文件夹
    public static final String[] DEFAULT_DIRECTORY = {"vido", "music", "source", "image", User.RECYCLE};

    // 通过构造函数注入UserDao，用于后续与用户相关的数据操作（如查询用户信息、更新用户空间大小等）
    @Autowired
    public FileServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Autowired
    private UserDao userDao;

    /**
     * 为用户添加新的命名空间（文件夹）的方法
     * 先获取根路径，然后基于此创建指定的命名空间文件夹，并在该文件夹下创建一系列默认的子文件夹（如视频、音乐等）
     *
     * @param request   HttpServletRequest对象，用于获取项目相关的上下文信息，比如获取根路径时需要用到会话信息等
     * @param namespace 要创建的命名空间（文件夹）名称
     */
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

    /**
     * 根据请求对象和文件名获取文件的真实路径
     * 如果传入的文件名是空（null或"\\"），则将文件名设置为空字符串，然后结合用户名（从请求中获取）构建出完整的真实文件路径并返回
     *
     * @param request  HttpServletRequest对象，用于获取当前用户相关信息（如用户名）
     * @param fileName 原始文件名，可能是相对路径形式或者完整文件名
     * @return 返回构建好的文件真实路径字符串
     */
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

    /**
     * 根据请求对象、文件名以及用户名获取文件的真实路径
     * 如果用户名是空，则调用另一个重载的getFileName方法，仅根据请求和文件名获取路径；如果文件名是空，则将其设置为空字符串，然后构建并返回文件真实路径
     *
     * @param request  HttpServletRequest对象，用于获取相关上下文信息
     * @param fileName 原始文件名，可能是相对路径形式或者完整文件名
     * @param username 用户名，明确指定所属用户
     * @return 返回构建好的文件真实路径字符串
     */
    public String getFileName(HttpServletRequest request, String fileName, String username) {
        if (username == null) {
            return getFileName(request, fileName);
        }
        if (fileName == null) {
            fileName = "";
        }
        return getRootPath(request) + username + File.separator + fileName;
    }

    /**
     * 获取文件存储的根路径方法
     * 通过请求对象获取会话的Servlet上下文，进而获取项目的根路径，并在此基础上拼接上预先定义的文件相对前缀，最终返回完整的根路径字符串
     *
     * @param request HttpServletRequest对象，用于获取项目相关的上下文信息
     * @return 返回文件存储的根路径字符串，格式类似http://locolhost:8080/cn/WEB-INF/file
     */
    @Override
    public String getRootPath(HttpServletRequest request) {
        String rootPath = request.getSession().getServletContext().getRealPath("/") + PREFIX;
        return rootPath;
    }

    /**
     * 获取指定路径下所有文件信息的方法
     * 先获取指定路径下的所有文件（和文件夹）列表，然后遍历该列表，对每个文件（非回收站文件夹对应的文件）构建一个FileCustom对象，
     * 将文件的名称、最后修改时间、所在路径、文件大小（根据是文件还是文件夹设置不同的值）以及文件类型等信息封装到FileCustom对象中，
     * 最后将这些FileCustom对象组成的列表返回，代表该路径下的文件信息集合
     *
     * @param realPath 要获取文件信息的真实路径字符串
     * @return 返回包含指定路径下所有文件详细信息的FileCustom对象列表
     */
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

    /**
     * 添加文件夹的方法
     * 根据请求对象、当前路径以及要添加的文件夹名称，构建出文件夹对应的File对象，然后调用mkdir方法创建该文件夹，返回创建结果（是否成功创建）
     *
     * @param request       HttpServletRequest对象，用于获取相关上下文信息（比如获取完整路径时可能用到用户名等）
     * @param currentPath   当前所在的文件路径，作为新文件夹的父级路径
     * @param directoryName 要添加的文件夹名称
     * @return 返回布尔值，表示文件夹是否创建成功
     */
    @Override
    public boolean addDirectory(HttpServletRequest request, String currentPath, String directoryName) {
        File file = new File(getFileName(request, currentPath), directoryName);
        return file.mkdir();
    }

    /**
     * 文件上传的方法
     * 遍历传入的文件数组，对每个文件进行处理：获取原始文件名，构建目标文件的完整路径，如果目标文件不存在，则将上传的文件转移到目标路径下；
     * 对于办公类型（通过文件工具类判断）的文件，还会尝试提取后缀名，创建文档并将相关信息保存到officeDao中（具体操作可能依赖相关业务逻辑）；
     * 最后调用reSize方法更新用户空间大小（以反映文件上传后的空间变化情况）
     *
     * @param request     HttpServletRequest对象，用于获取相关上下文信息（比如获取完整路径等）
     * @param files       要上传的文件数组，包含多个MultipartFile类型的文件对象
     * @param currentPath 当前所在的文件路径，作为文件上传的目标路径基础
     * @throws Exception 如果文件上传过程中出现任何问题（如文件转移失败、文档创建失败等），则抛出异常
     */
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
                        OfficeDao.addOffice(documentId, FileUtils.MD5(distFile));
                    } catch (Exception e) {
                    }
                }
            }
        }
        reSize(request);
    }

    /**
     * 重新计算并更新用户空间大小的方法
     * 先获取当前用户的用户名，然后调用countFileSize方法获取用户当前的文件总大小，再通过userDao将新的空间大小数据更新到数据库中；
     * 如果在更新过程中出现异常，则打印异常栈信息（此处可进一步完善异常处理逻辑）
     *
     * @param request HttpServletRequest对象，用于获取当前用户的用户名等相关信息
     */
    @Override
    public void reSize(HttpServletRequest request) {
        String userName = UserUtils.getUsername(request);
        try {
            userDao.reSize(userName, countFileSize(request));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断给定文件是否为图片文件的方法
     * 通过获取文件的扩展名（文件名中最后一个点之后的部分），并与常见的图片文件扩展名（如jpg、jpeg、png、gif）进行比较，来判断该文件是否为图片文件
     *
     * @param file 要判断的文件对象
     * @return 返回布尔值，如果文件扩展名匹配常见图片文件扩展名之一，则返回true，表示是图片文件；否则返回false
     */
    private boolean isImageFile(File file) {
        String fileName = file.getName();
        String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        return fileExtension.equals("jpg") || fileExtension.equals("jpeg") || fileExtension.equals("png") || fileExtension.equals("gif");
    }

    /**
     * 获取文件扩展名的方法
     * 查找文件名中最后一个点的位置，如果存在点（即有扩展名），则返回点之后的部分作为文件扩展名；如果不存在点（即没有扩展名），则返回空字符串
     *
     * @param fileName 要获取扩展名的文件名
     * @return 返回文件扩展名的字符串，如果没有扩展名则返回空字符串
     */
    private String getFileExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index > 0) {
            return fileName.substring(index + 1);
        }
        return "";
    }

    /**
     * 删除文件夹的方法
     * 遍历要删除的文件夹名称数组，对每个文件夹：先拼接出其源文件的相对地址，再获取对应的绝对路径，
     * 将源文件移动到回收站目录（通过 commons.jar 包中的方法实现）；
     * 同时将删除信息保存到 fileDao 中（具体保存内容和逻辑依赖业务需求）；
     * 最后调用 reSize 方法重新计算并更新用户空间大小（以反映文件删除后的空间变化情况）。
     *
     * @param request       HttpServletRequest 对象，用于获取相关上下文信息（比如获取完整路径、用户名等）
     * @param currentPath   当前所在的文件路径，作为要删除文件夹的相对父级路径
     * @param directoryName 要删除的文件夹名称数组，包含多个要删除的文件夹名称
     * @throws Exception 如果在文件移动、删除信息保存等过程中出现任何问题，则抛出异常
     */
    public void delDirectory(HttpServletRequest request, String currentPath, String[] directoryName) throws Exception {
        for (String fileName : directoryName) {
            // 拼接源文件的地址
            String srcPath = currentPath + File.separator + fileName;
            // 根据源文件相对地址拼接绝对路径
            File src = new File(getFileName(request, srcPath)); // 即将删除的文件/文件夹
            File destDir = new File(getRecyclePath(request));  // 回收站目录

            // 确保目标目录存在
            if (!destDir.exists()) {
                destDir.mkdirs();
            }

            // 生成唯一的目标文件名（避免冲突）
            File dest = getUniqueFileName(destDir, src);

            // 如果是目录，递归移动目录；如果是文件，直接移动
            if (src.isDirectory()) {
                // 确保目标目录存在，并且使用唯一的目录名称
                if (dest.exists()) {
                    // 如果目标目录已经存在，就直接删除该目录，避免 FileExistsException
                    org.apache.commons.io.FileUtils.deleteDirectory(dest);
                }
                org.apache.commons.io.FileUtils.moveDirectory(src, dest);
            } else {
                org.apache.commons.io.FileUtils.moveFile(src, dest);
            }

            // 保存本条删除信息
            FileDao.insertFiles(srcPath, UserUtils.getUsername(request));
        }
        // 重新计算文件大小
        reSize(request);
    }

    /**
     * 生成唯一的目标文件名（避免与目标目录中的文件或文件夹同名）
     *
     * @param destDir 目标目录
     * @param src     源文件/文件夹
     * @return 唯一的目标文件对象
     */
    private File getUniqueFileName(File destDir, File src) {
        String baseName = src.getName();
        String extension = ""; // 默认扩展名为空
        int dotIndex = baseName.lastIndexOf('.');

        // 如果是文件，提取文件名和扩展名
        if (dotIndex != -1 && !src.isDirectory()) {
            extension = baseName.substring(dotIndex); // 获取扩展名（例如 ".png"）
            baseName = baseName.substring(0, dotIndex); // 获取文件名部分（例如 "file"）
        }

        // 初始目标文件或目录路径
        File dest = new File(destDir, baseName + extension);
        int duplicateCounter = 1;

        // 生成唯一文件名或目录名
        while (dest.exists()) {
            String newName;
            if (!src.isDirectory()) {
                // 如果是文件，扩展名加在文件名后
                newName = baseName + "(" + duplicateCounter + ")" + extension; // 文件名(1).png
            } else {
                // 如果是目录，后缀加在目录名后
                newName = baseName + "(" + duplicateCounter + ")"; // 目录名(1)
            }
            dest = new File(destDir, newName); // 创建新的文件/目录
            duplicateCounter++;
        }

        return dest; // 返回唯一的目标文件对象
    }

    /**
     * 获取回收站路径的方法
     * 通过请求对象和预定义的回收站名称（User.RECYCLE）调用getFileName方法来获取回收站对应的完整路径字符串并返回
     *
     * @param request HttpServletRequest对象，用于获取相关上下文信息（比如获取完整路径等）
     * @return 返回回收站的路径字符串
     */
    public String getRecyclePath(HttpServletRequest request) {
        return getFileName(request, User.RECYCLE);
    }

    /**
     * 下载文件打包的方法
     * 先对当前路径进行空值判断处理，如果传入的文件名数组长度为1（即只下载单个文件），则根据请求对象、当前路径以及用户名构建出该文件的绝对路径，
     * 若该路径对应的是一个文件，则返回该文件对象（可用于后续下载操作）；如果文件名数组长度不为1，则目前代码未完整实现打包下载逻辑（部分代码被注释掉了）
     *
     * @param request     HttpServletRequest对象，用于获取相关上下文信息（比如获取完整路径等）
     * @param currentPath 当前所在的文件路径，可能影响文件下载的具体路径构建
     * @param fileNames   要下载的文件名数组，包含多个文件名（可能用于批量下载或打包下载情况）
     * @param username    用户名，可能用于确定用户专属的文件路径等情况
     * @return 返回要下载的文件对象，如果不符合下载条件（如文件名数组长度不为1且未完整实现打包逻辑等）则返回null
     */
    @Override
    public File downPackage(HttpServletRequest request, String currentPath, String[] fileNames, String username) throws Exception {
        // 获取文件名
        File downloadFile = null;
        if (currentPath == null) {
            currentPath = "";
        }
        // 判断是否为单个文件，单文件length为1
        if (fileNames.length == 1) {
            downloadFile = new File(getFileName(request, currentPath, username), fileNames[0]);// 返回绝对路径名
            if (downloadFile.isFile()) {
                return downloadFile;
            }
        }
        // 批量打包下载
        String[] sourcePath = new String[fileNames.length];
        for (int i = 0; i < fileNames.length; i++) {
            sourcePath[i] = getFileName(request, currentPath, username) + File.separator + fileNames[i];
        }
        String packageZipName = packageZip(sourcePath);
        downloadFile = new File(packageZipName);
        return downloadFile;
    }

    /**
     * 压缩文件
     *
     * @param sourcePath 原始文件路径数组，用于确定需要压缩的文件
     * @return 返回压缩后的文件名
     * @throws Exception 可能抛出的异常，比如文件找不到等
     */
    private String packageZip(String[] sourcePath) throws Exception {
        String zipName = sourcePath[0] + (sourcePath.length == 1 ? "" : "等" + sourcePath.length + "个文件") + ".zip";
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new FileOutputStream(zipName));
            for (String string : sourcePath) {
                writeZos(new File(string), "", zos);
            }
        } finally {
            if (zos != null) {
                zos.close();
            }
        }
        return zipName;
    }

    /**
     * 递归压缩文件
     *
     * @param file     当前需要压缩的文件或目录
     * @param basePath 基础路径，用于构建压缩包内的相对路径
     * @param zos      ZipOutputStream对象，用于写入压缩包
     * @throws IOException 文件读写过程中可能抛出的异常
     */
    private void writeZos(File file, String basePath, ZipOutputStream zos) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        if (file.isDirectory()) {
            File[] listFiles = file.listFiles();
            if (listFiles.length != 0) {
                for (File childFile : listFiles) {
                    writeZos(childFile, basePath + file.getName() + File.separator, zos);
                }
            }
        } else {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            ZipEntry entry = new ZipEntry(basePath + file.getName());
            zos.putNextEntry(entry);
            int count = 0;
            byte data[] = new byte[1024];
            while ((count = bis.read(data)) != -1) {
                zos.write(data, 0, count);
            }
            bis.close();
        }
    }

    /**
     * 删除压缩文件包的方法
     * 判断给定的文件对象的文件名是否以".zip"结尾，如果是，则调用delete方法删除该文件（即删除压缩文件包）
     *
     * @param downloadFile 要删除的文件对象，通常是下载相关的压缩文件包
     */
    public void deleteDownPackage(File downloadFile) {
        if (downloadFile.getName().endsWith(".zip")) {
            downloadFile.delete();
        }
    }

    /**
     * 获取用户文件总大小并格式化为合适单位后返回的方法
     * 通过调用另一个重载的countFileSize方法（以文件对象为参数的那个），传入基于请求对象构建的文件路径对应的文件对象，获取文件总大小，
     * 再利用FileUtils工具类将大小数据格式化为合适的单位（如KB、MB等，具体格式由工具类决定）后返回字符串形式的大小数据
     *
     * @param request HttpServletRequest对象，用于获取相关上下文信息（比如获取完整路径等）
     * @return 返回格式化后的用户文件总大小的字符串，格式符合业务需求中的表示方式
     */
    public String countFileSize(HttpServletRequest request) {
        long countFileSize = countFileSize(new File(getFileName(request, null)));
        return FileUtils.getDataSize(countFileSize);
    }

    /**
     * 递归计算给定文件（如果是文件夹则递归计算其下所有文件）总大小的方法
     *
     * @param srcFile 要计算大小的文件对象，可以是文件或者文件夹，用于确定从哪个文件节点开始计算其包含的所有文件的总大小
     * @return 返回计算得出的文件总大小，单位为字节，若传入的文件对象不存在子文件（如为空文件夹或者本身就是个空文件）则返回0
     */
    public long countFileSize(File srcFile) {
        File[] listFiles = srcFile.listFiles();
        if (listFiles == null) {
            return 0;
        }
        long count = 0;
        for (File file : listFiles) {
            if (file.isDirectory()) {
                count += countFileSize(file);
            } else {
                count += file.length();
            }
        }
        return count;
    }

    /**
     * 根据条件搜索文件
     *
     * @param request     HTTP请求对象，用于获取用户信息和根路径
     * @param currentPath 当前路径，用于确定搜索的起始目录
     * @param reg         文件名的正则表达式，用于匹配文件名
     * @param regType     文件类型，用于过滤特定类型的文件
     * @return 返回一个包含匹配文件信息的列表
     */
    public List<FileCustom> searchFile(HttpServletRequest request, String currentPath, String reg, String regType) {
        List<FileCustom> list = new ArrayList<>();
        matchFile(request, list, new File(getSearchFileName(request, currentPath)), reg, regType == null ? "" : regType);
        return list;
    }

    /**
     * 构造文件的完整搜索路径
     *
     * @param request  HTTP请求对象，用于获取用户信息
     * @param fileName 传入的文件名或路径，用于拼接完整路径
     * @return 返回构造的文件完整路径
     */
    public String getSearchFileName(HttpServletRequest request, String fileName) {
        if (fileName == null || fileName.equals("\\")) {
            System.out.println(1);
            fileName = "";
        }
        String username = UserUtils.getUsername(request);
        String realpath = getRootPath(request) + username + File.separator + fileName;
        return realpath;
    }

    /**
     * 递归匹配指定目录下的文件
     *
     * @param request HTTP请求对象，用于获取用户信息
     * @param list    用于存储匹配文件的信息的列表
     * @param dirFile 当前遍历的目录文件对象
     * @param reg     文件名的正则表达式，用于匹配文件名
     * @param regType 文件类型，用于过滤特定类型的文件
     */
    public void matchFile(HttpServletRequest request, List<FileCustom> list, File dirFile, String reg,
                          String regType) {
        File[] listFiles = dirFile.listFiles();
        if (listFiles != null) {
            for (File file : listFiles) {
                if (file.isFile()) {
                    String suffixType = FileUtils.getFileType(file);
                    if (suffixType.equals(regType) || (reg != null && file.getName().contains(reg))) {
                        FileCustom custom = new FileCustom();
                        custom.setFileName(file.getName());
                        custom.setLastTime(FileUtils.formatTime(file.lastModified()));
                        String parentPath = file.getParent();
                        String prePath = parentPath.substring(
                                parentPath.indexOf(getSearchFileName(request, null)) + getSearchFileName(request, null).length());
                        custom.setCurrentPath(File.separator + prePath);
                        if (file.isDirectory()) {
                            custom.setFileSize("-");
                        } else {
                            custom.setFileSize(FileUtils.getDataSize(file.length()));
                        }
                        custom.setFileType(FileUtils.getFileType(file));
                        list.add(custom);
                    }
                } else {
                    matchFile(request, list, file, reg, regType);
                }
            }
        }
    }
    /**
     * 移动目录
     * 此方法负责将指定的目录从一个位置移动到另一个位置
     * 如果目标位置已经存在同名目录，将对新目录进行重命名以避免覆盖
     *
     * @param request 当前请求对象，用于获取基础路径信息
     * @param currentPath 当前目录的路径
     * @param directoryName 需要移动的目录名称数组
     * @param targetdirectorypath 目标目录的路径
     * @throws Exception 如果移动过程中发生错误，则抛出异常
     */
    public void moveDirectory(HttpServletRequest request, String currentPath, String[] directoryName,String targetdirectorypath) throws Exception {
        for (String srcName : directoryName) {
            File srcFile = new File(getFileName(request, currentPath), srcName);
            File targetFile = new File(getFileName(request, targetdirectorypath), srcName);
            /* 处理目标目录中存在同名文件或文件夹问题 */
            String srcname = srcName;
            String prefixname = "";
            String targetname = "";
            if (targetFile.exists()) {
                String[] srcnamesplit = srcname.split("\\)");
                if (srcnamesplit.length > 1) {
                    String intstring = srcnamesplit[0].substring(1);
                    Pattern pattern = Pattern.compile("[0-9]*");
                    Matcher isNum = pattern.matcher(intstring);
                    if (isNum.matches()) {
                        srcname = srcname.substring(srcnamesplit[0].length() + 1);
                    }
                }
                for (int i = 1; true; i++) {
                    prefixname = "(" + i + ")";
                    targetname = prefixname + srcname;
                    targetFile = new File(targetFile.getParent(), targetname);
                    if (!targetFile.exists()) {
                        break;
                    }
                }
                targetFile = new File(targetFile.getParent(), targetname);
            }
            /* 移动即先复制，再删除 */
            copyfile(srcFile, targetFile);
            delFile(srcFile);
        }
    }
    /**
     * 删除指定的文件或文件夹及其内容
     *
     * @param srcFile 要删除的文件或文件夹的路径
     * @throws Exception 如果删除操作失败，则抛出异常
     */
    public void delFile(File srcFile) throws Exception {
        /* 如果是文件，直接删除 */
        if (!srcFile.isDirectory()) {
            /* 使用map 存储删除的 文件路径，同时保存用户名 */
            srcFile.delete();
            return;
        }
        /* 如果是文件夹，再遍历 */
        File[] listFiles = srcFile.listFiles();
        for (File file : listFiles) {
            if (file.isDirectory()) {
                delFile(file);
            } else {
                if (file.exists()) {
                    file.delete();
                }
            }
        }
        if (srcFile.exists()) {
            srcFile.delete();
        }
    }
    /**
     * 根据给定路径生成摘要文件对象
     * 此方法用于递归遍历指定路径下的所有文件夹，并构建一个摘要文件（SummaryFile）对象，
     * 该对象包含文件夹的相关信息，如文件夹名称、路径以及该文件夹下包含的子文件夹列表等
     *
     * @param realPath 文件或文件夹的实际路径
     * @param number   用于决定文件路径显示级别的整数
     * @return 返回一个摘要文件（SummaryFile）对象，包含文件夹的摘要信息
     */
    public SummaryFile summarylistFile(String realPath, int number) {
        File file = new File(realPath);
        SummaryFile sF = new SummaryFile();
        List<SummaryFile> returnlist = new ArrayList<SummaryFile>();
        if (file.isDirectory()) {
            sF.setFile(false);
            if (realPath.length() <= number) {
                sF.setFileName("yun盘");
                sF.setPath("");
            } else {
                String path = file.getPath();
                sF.setFileName(file.getName());
                //截取固定长度 的字符串，从number开始到value.length-number结束.
                sF.setPath(path.substring(number));
            }
            /* 设置抽象文件夹的包含文件集合 */
            for (File filex : file.listFiles()) {
                //获取当前文件的路径，构造该文件
                SummaryFile innersF = summarylistFile(filex.getPath(), number);
                if (!innersF.isFile()) {
                    returnlist.add(innersF);
                }
            }
            sF.setListFile(returnlist);
            /* 设置抽象文件夹的包含文件夹个数 */
            sF.setListdiretory(returnlist.size());
        } else {
            sF.setFile(true);
        }
        return sF;
    }

    /**
     * 重写复制目录的方法
     *
     * @param request             HttpServletRequest对象，用于获取文件名
     * @param currentPath         当前目录路径
     * @param directoryName       要复制的目录名称数组
     * @param targetdirectorypath 目标目录路径
     * @throws Exception 如果复制过程中发生错误，则抛出异常
     */
    @Override
    public void copyDirectory(HttpServletRequest request, String currentPath, String[] directoryName, String targetdirectorypath) throws Exception {
        for (String srcName : directoryName) {
            File srcFile = new File(getFileName(request, currentPath), srcName);
            File targetFile = new File(getFileName(request, targetdirectorypath), srcName);

            String srcname = srcName;
            String prefixname = "";
            String targetname = "";
            if (targetFile.exists()) {
                String[] srcnamesplit = srcname.split("\\)");
                if (srcnamesplit.length > 1) {
                    String intstring = srcnamesplit[0].substring(1);
                    Pattern pattern = Pattern.compile("[0-9]*");
                    Matcher isNum = pattern.matcher(intstring);
                    if (isNum.matches()) {
                        srcname = srcname.substring(srcnamesplit[0].length() + 1);
                    }
                }
                for (int i = 1; true; i++) {
                    prefixname = "(" + i + ")";
                    targetname = prefixname + srcname;
                    targetFile = new File(targetFile.getParent(), targetname);
                    if (!targetFile.exists()) {
                        break;
                    }
                }
                targetFile = new File(targetFile.getParent(), targetname);
            }
            /* 复制 */
            copyfile(srcFile, targetFile);

        }
    }

    /**
     * 复制文件或文件夹
     * 此方法递归地复制给定的源文件或文件夹到目标位置如果源是一个文件，它将直接复制如果源是一个文件夹，它将递归复制文件夹及其内容
     *
     * @param srcFile    源文件或文件夹
     * @param targetFile 目标文件或文件夹
     * @throws IOException 如果在复制过程中发生I/O错误
     */
    public void copyfile(File srcFile, File targetFile) throws IOException {
        if (!srcFile.isDirectory()) {
            /* 如果是文件，直接复制 */
            targetFile.createNewFile();
            FileInputStream src = (new FileInputStream(srcFile));
            FileOutputStream target = new FileOutputStream(targetFile);
            FileChannel in = src.getChannel();
            FileChannel out = target.getChannel();
            in.transferTo(0, in.size(), out);
            src.close();
            target.close();
        } else {
            /* 如果是文件夹，再遍历 */
            File[] listFiles = srcFile.listFiles();
            targetFile.mkdir();
            for (File file : listFiles) {
                File realtargetFile = new File(targetFile, file.getName());
                copyfile(file, realtargetFile);
            }
        }
    }
// 重命名
    public boolean renameDirectory(HttpServletRequest request, String currentPath, String srcName, String destName) {
        //根据源文件名  获取  源地址
        File file = new File(getFileName(request, currentPath), srcName);
        //同上
        File descFile = new File(getFileName(request, currentPath), destName);
        return file.renameTo(descFile);//重命名
    }

}