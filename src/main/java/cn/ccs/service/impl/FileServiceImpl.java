package cn.ccs.service.impl;

import cn.ccs.dao.UserDao;
import cn.ccs.dao.FileDao;
import cn.ccs.dao.OfficeDao;
import cn.ccs.pojo.FileCustom;
import cn.ccs.pojo.User;
import cn.ccs.service.FileService;
import cn.ccs.utils.FileUtils;
import cn.ccs.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
            File src = new File(getFileName(request, srcPath)); // 即将删除的文件地址
            File destDir = new File(getRecyclePath(request));  // 回收站目录地址

            // 确保目标目录存在
            if (!destDir.exists()) {
                destDir.mkdirs();
            }

            // 构建目标文件路径
            File dest = new File(destDir, src.getName());

            // 如果目标文件存在，则重命名目标文件
            if (dest.exists()) {
                String baseName = src.getName();
                String extension = "";
                int dotIndex = baseName.lastIndexOf('.');
                if (dotIndex != -1) {
                    extension = baseName.substring(dotIndex); // 提取扩展名（包括点）
                    baseName = baseName.substring(0, dotIndex); // 提取文件名部分
                }
                int duplicateCounter = 1;
                String newFileName = baseName + "(" + duplicateCounter + ")" + extension;

                // 循环生成唯一的文件名
                dest = new File(destDir, newFileName);
                while (dest.exists()) {
                    duplicateCounter++;
                    newFileName = baseName + "(" + duplicateCounter + ")" + extension;
                    dest = new File(destDir, newFileName);
                }
            }

            // 移动文件到目标文件路径
            org.apache.commons.io.FileUtils.moveFile(src, dest);

            // 保存本条删除信息
            FileDao.insertFiles(srcPath, UserUtils.getUsername(request));
        }
        // 重新计算文件大小
        reSize(request);
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
    public File downPackage(HttpServletRequest request, String currentPath, String[] fileNames, String username) {
        // 获取文件名
        File downloadFile = null;
        if (currentPath == null) {
            currentPath = "";
        }
        // 判断是否为单个文件
        if (fileNames.length == 1) {
            downloadFile = new File(getFileName(request, currentPath, username), fileNames[0]);// 返回绝对路径名
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
}