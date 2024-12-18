package cn.ccs.dao;

import cn.ccs.pojo.Share;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShareDao {
    // 添加分享文件
    void shareFile(Share share) throws Exception;

    // 根据用户名查找分享文件
    List<Share> findShareByName(@Param("username") String username, @Param("status")  int status) throws Exception;

    // 取消分享
    void cancelShare(@Param("url") String url, @Param("filePath")  String filePath, @Param("status") int status) throws Exception;

    // 查找分享文件
    List<Share> findShare(Share share) throws Exception;
}
