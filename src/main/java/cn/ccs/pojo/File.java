package cn.ccs.pojo;

import lombok.Data;

/**
 * 文件实体类
 */

@Data
public class File {
    // 文件id，自增
    private Integer fileId;

    // 操作人
    private String userName;

    // 文件路径
    private String filePath;
}
