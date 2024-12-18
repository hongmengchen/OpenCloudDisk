package cn.ccs.pojo;

import lombok.Data;

import java.util.List;
/**
 * SummaryFile类用于表示一个具有摘要信息的文件对象
 * 它不仅表示一个文件，还可以表示一个目录，包含文件或目录的路径、名称等信息
 */

@Data
public class SummaryFile {
    //标志：是不是一个文件
    private boolean isFile;

    //文件路径
    private String path;

    //文件名称
    private String fileName;

    //目录数量
    private int listdiretory;

    //文件列表
    private List<SummaryFile> listFile;
}