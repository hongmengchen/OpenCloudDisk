package cn.ccs.pojo;

import lombok.Data;

@Data
public class ShareFile extends FileCustom {
    private String shareUser;
    private String url;
}
