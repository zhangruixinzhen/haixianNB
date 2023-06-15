package com.hzlx.dao.impl;

import com.hzlx.dao.UserInfoDao;
import com.hzlx.entity.UserInfo;
import com.hzlx.utils.BaseDao;

public class UserInfoDaoImpl extends BaseDao<UserInfo> implements UserInfoDao {


    @Override
    public UserInfo getUserInfoByUserNameAndPassword(String userName, String passord) {
        return selectOne("select * from t_user_info where user_name=? and `password`=?",UserInfo.class,userName,passord);
    }
}
