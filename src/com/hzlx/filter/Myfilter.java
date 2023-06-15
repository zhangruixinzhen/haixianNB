package com.hzlx.filter;


import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Myfilter implements Filter {


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("你充Q币嘛");
        filterChain.doFilter(servletRequest,servletResponse);
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        response.sendRedirect("");

    }
}
