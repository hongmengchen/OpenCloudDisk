package cn.ccs.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 文件分享实体类
 */

@Data
@TableName("share")
public class Share {
    // 定义访问权限常量，用于表示内容的公开状态
    public static final int PUBLIC = 1;

    // 定义访问权限常量，用于表示内容的私有状态
    public static final int PRIVATE = 2;

    // 定义状态常量，用于表示内容已取消的状态
    public static final int CANCEL = 0;

    // 定义状态常量，用于表示内容已删除的状态
    public static final int DELETE = -1;

    // 分享链接
    private String shareUrl;

    // 分享文件id
    private String shareId;

    // 分享人
    private String shareUser;

    // 分享文件路径
    private String path;

    // 评论
    private String command;

    // 状态
    private int status;
}
