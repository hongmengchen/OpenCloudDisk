package cn.ccs.pojo;

import lombok.Data;

/**
 * 用户实体类
 */

@Data
public class User {
    // 命名空间
    public static final String NAMESPACE = "username";
    // 删除
    public static final String RECYCLE = "recycle";
    // 用户id，自增
    private Integer id;
    // 用户名
    private String username;
    // 密码
    private String password;
    // 已上传文件大小
    private String countSize;
    // 可用总空间大小
    private String totalSize;
}
