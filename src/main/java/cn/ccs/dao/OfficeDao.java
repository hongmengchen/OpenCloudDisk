package cn.ccs.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;


@Mapper
public interface OfficeDao {
    // 添加文件
    void addOffice(@Param("officeId") String officeId, @Param("officeMd5") String officeMd5) throws Exception;

    // 根据md5码获取officeId
    String getOfficeId(String officeMd5) throws Exception;

    // 添加office文件
    void addOfficefile(@Param("officeMd5") String officeMd5) throws Exception;
}