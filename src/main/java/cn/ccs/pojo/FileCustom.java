package cn.ccs.pojo;

import lombok.Data;

/**
 * 文件自定义类
 */

@Data
public class FileCustom {
    // 文件名
    private String fileName;

    // 文件类型
    private String fileType;

    // 文件大小
    private String fileSize;

    // 最后修改时间
    private String lastTime;

    // 文件路径
    private String filePath;

    // 当前路径
    private String currentPath;
}
