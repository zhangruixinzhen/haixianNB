package com.hzlx.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseDao<T> {
    private static Connection connection = null;
    private static PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    public int executeUpdate(String sql, Object... objs) {
        int rows = 0;
        try {
            //从连接池获取链接
            connection = ConnectionPool.getConnection();
            //获取sql预执行器
            preparedStatement = connection.prepareStatement(sql);
            //替换? 占位符
            for (int i = 0; i < objs.length; i++) {
                //循环替换
                preparedStatement.setObject(i + 1, objs[i]);
            }
            rows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("数据库连接异常");
            e.printStackTrace();
        } finally {
            ConnectionPool.closeAll(connection, preparedStatement, null);
        }
        return rows;
    }

    public T selectOne(String sql, Class aclass, Object... objs) {
        Object obj = null;
        try {
            //获取数据库将连接
            connection = ConnectionPool.getConnection();
            //获取SQL预执行器
            preparedStatement = connection.prepareStatement(sql);
            //替换占位符?
            for (int i = 0; i < objs.length; i++) {
                //循环替换
                preparedStatement.setObject(i + 1, objs[i]);
            }
            //执行sql获取结果集
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                //从结果集中解析数据,并封装成一个对象
                obj = aclass.newInstance();
                //通过反射获取对象所有属性
                Field[] fields = aclass.getDeclaredFields();
                //通过属性的名字找对应的set方法 执行set方法去完成赋值操作
                for (int i = 0; i < fields.length; i++) {
                    //获取字段名
                    String fieldName = fields[i].getName();
                    //截取字段名的首字母 用于转大写
                    String oldChar = fieldName.substring(0, 1);
                    //通过字段名找到对应的set方法名
                    String methodName = "set" + oldChar.toUpperCase() + fieldName.substring(1);
                    Method[] methods = aclass.getMethods();
                    //通过反射找到对应的方法,并执行它,完成赋值操作
                    Method method = aclass.getMethod(methodName, fields[i].getType());
                    //执行对应的set方法,给对象赋值
                    method.invoke(obj, resultSet.getObject(i + 1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            System.out.println("对象实例化失败");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            System.out.println("没有找到对应的set方法");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } finally {
            ConnectionPool.closeAll(connection, preparedStatement, null);
        }
        return (T) obj;
    }


    //查看多个结果-->对象
    //sql 执行的sql
    //clazz 对象字节码
    //objects 参数列表
    //集合
    public List<T> selectListForObject(String sql, Class clazz, Object... objects) {
        List<T> list = null;
        try {
            connection = ConnectionPool.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            //替换占位符
            for (int i = 0; i < objects.length; i++) {
                preparedStatement.setObject(i + 1, objects[i]);
            }
            //执行sql获取结果集
            resultSet = preparedStatement.executeQuery();
            list = new ArrayList<>();
            //遍历结果集,封装对象并且放到list中
            while (resultSet.next()) {
                //通过反射创建对象
                Object obj = clazz.newInstance();
                //获取所有的字段
                Field[] fields = clazz.getDeclaredFields();
                //根据字段名获取对应的set方法名
                for (int i = 0; i < fields.length; i++) {
                    //获取字段名
                    String name = fields[i].getName();
                    //根据字段名-->字段名首字母转大写,在前边拼接上set
                    String firstChar = name.substring(0, 1).toUpperCase();
                    //方法名
                    String methodName = "set" + firstChar + name.substring(1);
                    //根据拼接好的方法名获取方法对象
                    Method method = clazz.getMethod(methodName, fields[i].getType());
                    //指定对应的set方法完成赋值
                    method.invoke(obj, resultSet.getObject(i + 1));
                }
                list.add((T) obj);
            }

        } catch (SQLException e) {
            System.out.println("数据库连接异常");
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            System.out.println("没有对应的set方法");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            System.out.println("赋值失败");
            e.printStackTrace();
        } finally {
            ConnectionPool.closeAll(connection, preparedStatement, null);
        }

        return list;
    }


    //查询单条数据,把结果封装到map集合中
    //sql 执行的sql
    //objects 参数列表
    //结果集
    public Map<String, Object> selectOneForMap(String sql, Object... objects) {
        Map<String, Object> map = null;
        try {
            connection = ConnectionPool.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            //替换占位符
            for (int i = 0; i < objects.length; i++) {
                preparedStatement.setObject(i + 1, objects[i]);
            }
            //执行sql 获取结果集
            resultSet = preparedStatement.executeQuery();
            map = new HashMap<>();
            //字段名都在metaData对象身上
            ResultSetMetaData metaData = resultSet.getMetaData();
            //获取结果的总列数
            int columnCount = metaData.getColumnCount();
            if (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    //根据列名从结果集中获取数据
                    Object value = resultSet.getObject(columnName);
                    map.put(columnName, value);
                }
            }
        } catch (SQLException e) {
            System.out.println("数据连接异常");
            e.printStackTrace();
        } finally {
            ConnectionPool.closeAll(connection, preparedStatement, null);
        }
        return map;
    }


    //查询多条数据,把结果封装到list集合中
    //sql 执行的sql
    //objects 参数列表
    //结果集
    public List<Map<String, Object>> selectListForMap(String sql, Object... objects) {
        //装结果集的容器
        List<Map<String, Object>> list = null;
        try {
            connection = ConnectionPool.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            //替换占位符
            for (int i = 0; i < objects.length; i++) {
                preparedStatement.setObject(i + 1, objects[i]);
            }
            //执行sql获取结果集
            resultSet = preparedStatement.executeQuery();
            list = new ArrayList<>();
            //获取metaData对象,方便获取列名
            ResultSetMetaData metaData = resultSet.getMetaData();
            //获取结果集一共有多少列
            int columnCount = metaData.getColumnCount();
            //遍历结果集,封装成map并放入list集合中
            while (resultSet.next()) {
                Map<String, Object> map = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    //获取列名
                    String columnName = metaData.getColumnLabel(i);
                    //根据列名获取结果
                    Object value = resultSet.getObject(columnName);
                    //把columnName作为key 放入map中
                    map.put(columnName, value);
                }
                list.add(map);
            }
        } catch (SQLException e) {
            System.out.println("数据库连接异常");
            e.printStackTrace();
        } finally {
            ConnectionPool.closeAll(connection, preparedStatement, null);
        }
        return list;
    }

}
