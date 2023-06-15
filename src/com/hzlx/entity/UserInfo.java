package com.hzlx.entity;

import lombok.Data;

import java.util.Date;
@Data
public class UserInfo {
    private Integer id;
    private String name;
    private String userName;
    private String password;
    private String tel;
    private String address;
    private String idCard;
    private Date createTime;
    private Integer status;


}
