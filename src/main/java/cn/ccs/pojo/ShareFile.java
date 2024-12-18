package cn.ccs.pojo;

import lombok.Data;

/**
 * 分享文件
 */

@Data
public class ShareFile extends FileCustom {
    // 分享人
    private String shareUser;

    // 分享链接
    private String url;
}
