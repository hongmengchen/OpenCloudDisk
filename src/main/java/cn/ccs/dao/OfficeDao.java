package cn.ccs.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;


@Mapper
public interface OfficeDao {
    void addOffice(@Param("officeId") String officeId, @Param("officeMd5") String officeMd5) throws Exception;
    String getOfficeId(String officeMd5) throws Exception;
}