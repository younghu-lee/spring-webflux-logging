package com.example.springwebfluxlogging.config;

import com.example.springwebfluxlogging.service.CommonService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalFilter implements WebFilter {

    private final CommonService commonService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ExchangeLoggingDecorator exchangeLoggingDecorator = new ExchangeLoggingDecorator(exchange);
        return chain.filter(exchangeLoggingDecorator)
                .publishOn(Schedulers.boundedElastic())
                .doFinally(signalType -> {
                    String requestBody = "";
                    ObjectMapper objectMapper = new ObjectMapper();
                    HashMap<String, Object> map = new HashMap<>();
                    MultiValueMap<String, String> multiValueMap = exchangeLoggingDecorator.getRequest().getQueryParams();
                    for (String key : multiValueMap.keySet()) {
                        List<String> data = multiValueMap.get(key);
                        if (data.size() > 1) {
                            map.put(key, data);
                        } else {
                            map.put(key, data.getFirst());
                        }
                    }
                    try {
                        requestBody = objectMapper.writeValueAsString(map);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }

                    if (exchange.getRequest().getHeaders().getContentType() != null
                        && exchange.getRequest().getHeaders().getContentType().includes(MediaType.MULTIPART_FORM_DATA)) {
                        requestBody = exchangeLoggingDecorator.getFullBody();
                    } else if (StringUtils.isEmpty(requestBody) || "{}".equals(requestBody) || "POST".equals(exchange.getRequest().getMethod().name())) {
                        requestBody = exchangeLoggingDecorator.getRequest().getFullBody();
                    }

                    log.debug("requestBody : {}", requestBody);
                    log.debug("ip : {}", commonService.getRemoteAddr(exchange));
                });
    }
}
