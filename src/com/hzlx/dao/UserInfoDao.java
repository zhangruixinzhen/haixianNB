package com.hzlx.dao;

import com.hzlx.entity.UserInfo;

public interface UserInfoDao {
    UserInfo getUserInfoByUserNameAndPassword(String userName,String pawwword);
}
