package cn.ccs.service.impl;

import cn.ccs.dao.UserDao;
import cn.ccs.pojo.User;
import cn.ccs.service.UserService;
import cn.ccs.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserServiceImpl implements UserService {
    // 注入Dao
    private final UserDao userDao;

    @Autowired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User findUser(User user){
        try {
            user.setPassword(UserUtils.MD5(user.getPassword()));
            User existUser = userDao.findUser(user);
            return  existUser;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getCountSize(String username) {
        String countSize = null;
		try {
			countSize = userDao.getCountSize(username);
		} catch (Exception e) {
			e.printStackTrace();
			return countSize;
		}
		return countSize;
    }
}
