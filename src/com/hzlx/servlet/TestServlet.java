package com.hzlx.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

public class TestServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        resp.getWriter().write("hello Servlet..");
        req.setCharacterEncoding("UTF-8");
        String uName = req.getParameter("username");
        String pwd = req.getParameter("pwd");

        resp.setContentType("text/html;charset=UTF-8");
        //获取URL端口的资源路径
        System.out.println(req.getRequestURI());
        //获取请求的URL
        System.out.println(req.getRequestURL());
        //获取项目名
        System.out.println(req.getContextPath());
        //获取请求方式
        System.out.println(req.getMethod());

        Enumeration<String> enumeration = req.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            System.out.println(enumeration.nextElement());
        }

        if ("张三".equals(uName)&&"6".equals(pwd)){
            resp.getWriter().write("六");

        }else{
            resp.getWriter().write("666");
        }
    }
}
