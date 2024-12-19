package cn.ccs.utils;

import com.baidubce.BceClientConfiguration;
import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.doc.DocClient;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 文件工具类
 */

public class FileUtils {
    /**
     * 获取文件大小
     *
     * @param size 文件大小（以字节为单位）
     * @return 文件大小的字符串表示，例如 "1024B" 或 "1.5MB"
     */
    public static String getDataSize(long size) {
        DecimalFormat formater = new DecimalFormat("####.0");
        if (size < 1024) {
            return size + "B";
        } else if (size < 1024 * 1024) {
            float kbsize = size / 1024f;
            return formater.format(kbsize) + "KB";
        } else if (size < 1024 * 1024 * 1024) {
            float mbsize = size / 1024f / 1024f;
            return formater.format(mbsize) + "MB";
        } else if (size < 1024 * 1024 * 1024 * 1024) {
            float gbsize = size / 1024f / 1024f / 1024f;
            return formater.format(gbsize) + "GB";
        } else {
            return "-";
        }
    }

    /**
     * 格式化时间戳
     *
     * @param time 时间戳（毫秒）
     * @return 格式化后的时间字符串，例如 "yyyy-MM-dd HH:mm:ss"
     */
    public static String formatTime(long time) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(time));
    }

    /**
     * 生成随机的8位字符串
     *
     * @return 随机生成的8位字符串
     */
    public static String getUrl8() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    // 文件类型映射表
    public final static Map<String, String> FILE_TYPE_MAP = new HashMap<String, String>();

    static {
        // 图片类型
        FILE_TYPE_MAP.put("jpg", "image");
        FILE_TYPE_MAP.put("png", "image");
        FILE_TYPE_MAP.put("gif", "image");
        FILE_TYPE_MAP.put("tif", "image");
        FILE_TYPE_MAP.put("bmp", "image");
        FILE_TYPE_MAP.put("bmp", "image");
        FILE_TYPE_MAP.put("bmp", "image");

        // 文档类型
        FILE_TYPE_MAP.put("html", "docum");
        FILE_TYPE_MAP.put("htm", "docum"); //HTM (htm)
        FILE_TYPE_MAP.put("css", "docum"); //css
        FILE_TYPE_MAP.put("js", "docum"); //js
        FILE_TYPE_MAP.put("ini", "docum");
        FILE_TYPE_MAP.put("txt", "docum");
        FILE_TYPE_MAP.put("jsp", "docum");
        FILE_TYPE_MAP.put("sql", "docum");
        FILE_TYPE_MAP.put("xml", "docum");
        FILE_TYPE_MAP.put("java", "docum");
        FILE_TYPE_MAP.put("bat", "docum");
        FILE_TYPE_MAP.put("mxp", "docum");
        FILE_TYPE_MAP.put("properties", "docum");

        // 办公软件类型
        FILE_TYPE_MAP.put("doc", "office");
        FILE_TYPE_MAP.put("docx", "office");
        FILE_TYPE_MAP.put("vsd", "office");
        FILE_TYPE_MAP.put("mdb", "office");
        FILE_TYPE_MAP.put("pdf", "office");
        FILE_TYPE_MAP.put("xlsx", "office");
        FILE_TYPE_MAP.put("xls", "office");
        FILE_TYPE_MAP.put("pptx", "office");
        FILE_TYPE_MAP.put("ppt", "office");
        FILE_TYPE_MAP.put("wps", "office");

        // 视频类型
        FILE_TYPE_MAP.put("mov", "vido");
        FILE_TYPE_MAP.put("rmvb", "vido");
        FILE_TYPE_MAP.put("flv", "vido");
        FILE_TYPE_MAP.put("mp4", "vido");
        FILE_TYPE_MAP.put("avi", "vido");
        FILE_TYPE_MAP.put("wav", "vido");
        FILE_TYPE_MAP.put("wmv", "vido");
        FILE_TYPE_MAP.put("mpg", "vido");

        // 音频类型
        FILE_TYPE_MAP.put("mp3", "audio");
        FILE_TYPE_MAP.put("mid", "audio");

        // 压缩文件类型
        FILE_TYPE_MAP.put("zip", "comp");
        FILE_TYPE_MAP.put("rar", "comp");
        FILE_TYPE_MAP.put("gz", "comp");

    }

    /**
     * 获取文件类型
     *
     * @param file 文件对象
     * @return 文件类型的字符串表示，如果类型未知则返回 "file"
     */
    public static String getFileType(File file) {
        if (file.isDirectory()) {
            return "folder-open";
        }
        String fileName = file.getPath();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        String fileType = FILE_TYPE_MAP.get(suffix);
        return fileType == null ? "file" : fileType;
    }

    // 百度文档客户端
    private static DocClient docClient = null;

    /**
     * 获取DocClient实例
     *
     * @return DocClient实例
     */
    public static synchronized DocClient getDocClient() {
        if (docClient == null) {
            try (InputStream input = FileUtils.class.getClassLoader().getResourceAsStream("baidu.properties")) {
                Properties props = new Properties();
                if (input != null) {
                    props.load(input);
                }

                // 从配置文件中读取密钥和端点
                String ACCESS_KEY_ID = props.getProperty("baidu.access_key_id");
                String SECRET_ACCESS_KEY = props.getProperty("baidu.secret_access_key");
                String ENDPOINT = props.getProperty("baidu.endpoint");

                // 初始化 BceClientConfiguration
                BceClientConfiguration config = new BceClientConfiguration();
                config.setCredentials(new DefaultBceCredentials(ACCESS_KEY_ID, SECRET_ACCESS_KEY));
                config.setEndpoint(ENDPOINT);

                // 创建 DocClient
                docClient = new DocClient(config);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("加载配置文件失败或初始化 DocClient 失败", e);
            }
        }
        return docClient;
    }

    /**
     * 计算文件的MD5值
     *
     * @param file 文件对象
     * @return 文件的MD5值（大写），如果计算失败则返回null
     */
    public static String MD5(File file) {
        byte[] bys = null;
        try {
            // 读取文件内容到字节数组
            bys = org.apache.commons.io.FileUtils.readFileToByteArray(file);
            // 计算MD5值并返回
            return DigestUtils.md5DigestAsHex(bys).toUpperCase();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}

