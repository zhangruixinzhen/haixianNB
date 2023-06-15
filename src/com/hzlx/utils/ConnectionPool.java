package com.hzlx.utils;

import java.sql.*;
import java.util.LinkedList;

public class ConnectionPool {

    //最小连接数
    private static int MIN_POOL_NUM;
    //最大连接数
    private static int MAX_POOL_NUM;

    private static String DRIVER;
    private static String URL;
    private static String USERNAME;
    private static String PASSWORD;

    private static LinkedList<Connection> connectionPool = new LinkedList<>();

    static {
        init();
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        initConnection();
    }

    //初始化方法 给属性赋值
    private static void init() {
        //先把jdbc配置文件加载到程序里
        PropertiesUtil.load("jdbc");
        DRIVER = PropertiesUtil.getValue("jdbc.driver");
        URL = PropertiesUtil.getValue("jdbc.url");
        USERNAME = PropertiesUtil.getValue("jdbc.userName");
        PASSWORD = PropertiesUtil.getValue("jdbc.password");
        MIN_POOL_NUM = Integer.parseInt(PropertiesUtil.getValue("jdbc.minPoolNum"));
        MAX_POOL_NUM = Integer.parseInt(PropertiesUtil.getValue("jdbc.maxPoolNum"));
    }

    private static void initConnection() {
        for (int rows = 0; rows < MIN_POOL_NUM; rows++) {
            try {
                //往集合的尾部追加
                connectionPool.addLast(DriverManager.getConnection(URL, USERNAME, PASSWORD));
            } catch (SQLException e) {
                System.out.println("初始化获取链接异常");
                e.printStackTrace();
            }
        }
    }


    //获取连接对象

    public static Connection getConnection() {
        //
        if (connectionPool.size() > 0) {
            return connectionPool.removeFirst();
        } else {
            try {
                return DriverManager.getConnection(URL, USERNAME, PASSWORD);
            } catch (SQLException e) {
                System.out.println("获取连接异常");
                e.printStackTrace();
            }
        }
        return null;

    }

    //归还连接的方法
    //connection 还回来的连接对象
    //true 归还成功 false归还失败
    private static boolean returnConnection(Connection connection) {
        //判断当前连接池集合的size大于等于配置连接数的话,说明连接池满了,这个链接需要丢弃
        if (connectionPool.size() >= MAX_POOL_NUM) {
            try {
                connection.close();
                return true;
            } catch (SQLException e) {
                System.out.println("关闭连接异常");
                e.printStackTrace();
            }
        } else {
            connectionPool.addLast(connection);
            return true;
        }
        return false;
    }


    public static void closeAll(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                System.out.println("关闭结果集失败");
                e.printStackTrace();
            }
        }

        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                System.out.println("关闭preparedStatement失败");
                e.printStackTrace();
            }
        }

        if (connection != null) {
            returnConnection(connection);
        }
    }

}
