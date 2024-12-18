package cn.ccs.dao;

import cn.ccs.pojo.RecycleFile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FileDao {
    // 插入文件
    void insertFiles(@Param("filePath") String filePath, @Param("userName") String userName) throws Exception ;

    // 查询回收站中数据
    List<RecycleFile> selectFiles(@Param("userName") String userName) throws Exception;

    // 查询文件
    RecycleFile selectFile(@Param("fileId") int fileId) throws Exception;

    // 删除文件
    void deleteFile(@Param("fileId") int fileId, @Param("userName") String userName) throws Exception;

    // 根据当前用户名删除所有的删除记录
    void deleteFiles(@Param("userName") String userName) throws Exception;
}
