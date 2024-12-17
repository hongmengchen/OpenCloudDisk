package cn.ccs.dao;

import cn.ccs.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
public interface UserDao {
    // 根据账号密码查询用户
    User findUser(User user);
    // 根据用户名查询用户空间大小
    String getCountSize(String username);
}
