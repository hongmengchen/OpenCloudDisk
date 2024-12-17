package cn.ccs.pojo;

import lombok.Data;

/**
 * 文件记录实体类
 */

@Data
public class Office {
    // 上传office文档的id
    private String officeId;

    // 与上传至百度云文件对应的id
    private String officeMd5;
}
