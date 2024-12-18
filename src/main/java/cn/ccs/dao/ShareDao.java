package cn.ccs.dao;

import cn.ccs.pojo.Share;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShareDao {
    void shareFile(Share share) throws Exception;
    List<Share> findShareByName(@Param("username") String username, @Param("status")  int status) throws Exception;
    void cancelShare(@Param("url") String url, @Param("filePath")  String filePath, @Param("status") int status) throws Exception;
    List<Share> findShare(Share share) throws Exception;
}
