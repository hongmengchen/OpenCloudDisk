package cn.ccs.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 回收站文件
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class RecycleFile extends FileCustom{
    // 文件id
    private Integer fileId;
}
