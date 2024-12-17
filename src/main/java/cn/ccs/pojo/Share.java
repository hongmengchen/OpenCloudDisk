package cn.ccs.pojo;

import lombok.Data;

@Data
public class Share {
    private Integer shareId;
    private String shareUrl;
    private String path;
    private String shareUser;
    private Integer status;
    private String command;
}
