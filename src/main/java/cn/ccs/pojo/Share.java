package cn.ccs.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 文件分享实体类
 */

@Data
@TableName("share")
public class Share {
    public static final int PUBLIC = 1;
    public static final int PRIVATE = 2;
    public static final int CANCEL = 0;
    public static final int DELETE = -1;
    private String shareUrl;
    private String shareId;
    private String shareUser;
    private String path;
    private String command;
    private int status;
}
