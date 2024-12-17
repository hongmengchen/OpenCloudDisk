package cn.ccs.pojo;

import lombok.Data;

/**
 * 文件分享实体类
 */

@Data
public class Share {
    // 分享id，自增
    private Integer shareId;

    // 分享文件的对外地址
    private String shareUrl;

    // 被分享文件的路径
    private String path;

    // 分享人
    private Integer shareUser;

    // 状态（1-公开 2-加密）
    private Integer status;

    // 提取码
    private String command;
}
