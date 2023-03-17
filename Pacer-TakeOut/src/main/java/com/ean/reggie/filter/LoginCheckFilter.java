package com.ean.reggie.filter;


import com.alibaba.fastjson.JSON;
import com.ean.reggie.common.BaseContext;
import com.ean.reggie.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(filterName = "loginCheckFilter" , urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    // 用于判断字段匹配的类AntPathMatcher
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 父类向下转型
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // 从request请求中获取URI
        String requestURI = request.getRequestURI();
        // 不需要拦截的请求路径存到字符串数组中
        String[] uris = new String[]{
                "/backend/**",
                "/front/**",
                "/employee/login",
                "/employee/logout"
        };

        // 如果请求路径相互匹配，那么直接放行。
        if(isCheck(uris,requestURI)){
            filterChain.doFilter(request,response);
            return;
        }

        // 判断登录状态，如果登录则放行。
        if(request.getSession().getAttribute("employee") != null){
            log.info("用户已登录，用户id为{}",request.getSession().getAttribute("employee"));

            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setThread(empId);

            filterChain.doFilter(request,response);
            return;
        }

        // PrintWriter writer = response.getWriter();
        // Result<Object> notlogin = Result.error("NOTLOGIN");
        // String r = JSON.toJSONString(notlogin);
        // writer.write(r);

        // response.getWriter().write(JSON.toJSONString(Result.error("NOTLOGIN")));
        response.sendRedirect("/employee/login");
        return;
    }

    public boolean isCheck(String[] uris,String requestURI){
        for (String uri : uris) {
            boolean match = PATH_MATCHER.match(uri, requestURI);
            if(match)
                return true;
        }
        return false;
    }
}
