package cn.ccs.service.impl;

import cn.ccs.dao.UserDao;
import cn.ccs.pojo.User;
import cn.ccs.service.UserService;
import cn.ccs.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 */

@Service("userService")
public class UserServiceImpl implements UserService {
    // 用户数据访问对象，用于与数据库进行交互
    private final UserDao userDao;

    /**
     * 构造函数，用于注入UserDao实例
     *
     * @param userDao 用户数据访问对象，不能为空，用于执行数据库操作
     */
    @Autowired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * 根据用户信息查找用户
     *
     * @param user 用户对象，包含查找用户的必要信息
     * @return 如果找到用户则返回用户对象，否则返回null
     */
    @Override
    public User findUser(User user) {
        try {
            // 对用户密码进行MD5加密处理
            user.setPassword(UserUtils.MD5(user.getPassword()));
            // 调用Dao方法查找用户
            User existUser = userDao.findUser(user);
            return existUser;
        } catch (Exception e) {
            // 异常处理，打印堆栈跟踪信息
            e.printStackTrace();
            // 返回null表示查找失败
            return null;
        }
    }

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 如果找到用户则返回用户对象，否则返回null
     */
    @Override
    public User findUser(String username) {
        User user = null;
        try {
            user = userDao.findUserByUserName(username);
        } catch (Exception e) {
            e.printStackTrace();
            return user;
        }
        return user;
    }

    /**
     * 获取用户的当前所用空间大小
     *
     * @param username 用户名
     * @return 所用空间大小的字符串表示，如果发生异常则返回null
     */
    @Override
    public String getCountSize(String username) {
        String countSize = null;
        try {
            // 调用Dao方法获取所用空间大小
            countSize = userDao.getCountSize(username);
        } catch (Exception e) {
            // 调用Dao方法获取所用空间大小
            e.printStackTrace();
            // 返回当前countSize，可能为null，表示获取失败
            return countSize;
        }
        // 返回所用空间大小
        return countSize;
    }

    /**
     * 添加新用户
     *
     * @param user 待添加的用户对象
     * @return 添加成功返回true，用户已存在返回false
     */
    public boolean addUser(User user) throws Exception {
        // 检查用户是否已存在
        User users = userDao.checkUser(user);
        if (users == null) {
            // 用户不存在，对密码进行MD5加密处理后添加到数据库
            user.setPassword(UserUtils.MD5(user.getPassword()));
            userDao.addUser(user);
        } else {
            // 用户已存在，返回false
            return false;
        }
        // 添加成功，返回true
        return true;
    }
}
