package cn.ccs.pojo;

import lombok.Data;

@Data
public class Share {
    // 分享id
    private Integer shareId;
    // 分享链接
    private String shareUrl;
    // 文件路径
    private String path;
    // 分享者
    private String shareUser;
    //状态
    private Integer status;
    // 命令
    private String command;
}
