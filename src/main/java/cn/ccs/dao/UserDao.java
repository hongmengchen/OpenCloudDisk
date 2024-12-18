package cn.ccs.dao;

import cn.ccs.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
public interface UserDao {
    // 通过传入的用户对象查找对应用户信息
    User findUser(User user);

    // 依据用户名查找对应的用户信息，可能抛出异常
    User findUserByUserName(String username) throws Exception;

    // 根据用户名查询用户空间大小
    String getCountSize(String username);

    // 添加用户
    void addUser(User user) throws Exception;

    // 通过传入用户对象检查用户名是否已存在，可能抛出异常
    User checkUser(User user) throws Exception;

    // 更新用户空间大小
    void reSize(@Param("username") String username, @Param("countSize") String countSize) throws Exception;
}