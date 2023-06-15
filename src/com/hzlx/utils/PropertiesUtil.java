package com.hzlx.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
    private static Properties properties;

    private PropertiesUtil(){}

    //使用静态代码块创建对象
    static {
        properties = new Properties();
    }

    //把配置文件读到程序中
    // fileName 配置文件的名字(只需要写名字即可)
    public static void load(String fileName){

        //把配置文件转成一个流
        InputStream resourceAsStream = PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName+".properties");
        try {
            properties.load(resourceAsStream);
        } catch (IOException e) {
            System.out.println("加载配置文件异常");
            e.printStackTrace();
        }
    }
    //根据配置文件的key获取指定的值
    public static String getValue(String key){
        return properties.get(key).toString();
    }
}
