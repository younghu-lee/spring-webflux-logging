package com.example.springwebfluxlogging.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;

@Service
public class CommonService {

    public String getRemoteAddr(ServerWebExchange exchange) {
        String ip = "";
        ip = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = exchange.getRequest().getHeaders().getFirst("Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = exchange.getRequest().getHeaders().getFirst("WL-Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = exchange.getRequest().getHeaders().getFirst("HTTP_CLIENT_IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = exchange.getRequest().getHeaders().getFirst("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = exchange.getRequest().getHeaders().getFirst("X-Real-IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = exchange.getRequest().getHeaders().getFirst("X-RealIP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = exchange.getRequest().getHeaders().getFirst("REMOTE_ADDR");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        }
        return ip;
    }

}
