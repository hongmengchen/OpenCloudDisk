package cn.ccs.dao;

import cn.ccs.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
public interface UserDao {
    // 根据账号密码查询用户
    User findUser(User user); // 通过传入的用户对象查找对应用户信息

    // 根据用户名查询用户
    User findUserByUserName(String username) throws Exception; // 依据用户名查找对应的用户信息，可能抛出异常

    // 根据用户名查询用户空间大小
    String getCountSize(String username); // 根据用户名获取其空间大小信息

    // 注册用户
    void addUser(User user) throws Exception; // 将传入的用户对象信息插入数据库实现注册，可能抛出异常

    // 检查用户名是否重复
    User checkUser(User user) throws Exception; // 通过传入用户对象检查用户名是否已存在，可能抛出异常

    // 更新用户空间大小
    void reSize(@Param("username") String username, @Param("countSize") String countSize) throws Exception; // 以用户名和新空间大小数据更新用户空间大小记录，可能抛出异常
}