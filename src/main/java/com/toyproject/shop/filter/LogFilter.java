package com.toyproject.shop.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.UUID;

@Slf4j
public class LogFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
      log.info("filter init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String reqUri = req.getRequestURI();

        String uuid = UUID.randomUUID().toString();

        try{
            log.info("REQUEST [{}] [{}]",uuid, reqUri);
            chain.doFilter(request, response);
        }catch (Exception e){
            throw e;
        }finally {
            log.info("RESPONSE [{}] [{}]",uuid, reqUri);
        }
    }

    @Override
    public void destroy() {
        log.info("filter destroy");
    }
}
