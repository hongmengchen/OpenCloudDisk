package cn.ccs.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 文件实体类
 */

@Data
@TableName("file")
public class File {
    // 文件id，自增
    private Integer fileId;

    // 操作人
    private String userName;

    // 文件路径
    private String filePath;
}
