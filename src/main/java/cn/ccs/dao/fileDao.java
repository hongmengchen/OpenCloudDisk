package cn.ccs.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface fileDao {
    static void insertFiles(@Param("filePath") String filePath, @Param("userName") String userName) throws Exception {

    }

}
