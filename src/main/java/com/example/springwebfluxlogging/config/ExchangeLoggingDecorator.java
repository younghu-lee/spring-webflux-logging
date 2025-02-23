package com.example.springwebfluxlogging.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExchangeLoggingDecorator extends ServerWebExchangeDecorator {

    private RequestLoggingDecorator requestLoggingDecorator;

    private final StringBuilder body = new StringBuilder();

    public ExchangeLoggingDecorator(ServerWebExchange exchange) {
        super(exchange);
        this.requestLoggingDecorator = new RequestLoggingDecorator(exchange.getRequest());
    }

    public RequestLoggingDecorator getRequest() {
        return requestLoggingDecorator;
    }

    @Override
    public Mono<MultiValueMap<String, Part>> getMultipartData() {
        return super.getDelegate().getMultipartData().doOnNext(stringPartMultiValueMap -> {
            HashMap<String, Object> map = new HashMap<>();
            for (String key : stringPartMultiValueMap.keySet()) {
                List<Part> part = stringPartMultiValueMap.get(key);
                List<Object> data = new ArrayList<>();
                part.forEach(p -> {
                   if (!p.getClass().getName().contains("DefaultParts$DefaultFilePart")) {
                       p.content().next().subscribe(bytes -> {
                           if (part.size() > 1) {
                               data.add(bytes.toString(StandardCharsets.UTF_8));
                           } else {
                               map.put(key, bytes.toString(StandardCharsets.UTF_8));
                           }
                       });
                   }
                });
                if (!data.isEmpty()) {
                    map.put(key, data);
                } else if (!map.containsKey(key)) {
                    map.put(key, "");
                }
            }
            try {
                this.body.append(new ObjectMapper().writeValueAsString(map));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public String getFullBody() {
        return this.body.toString();
    }
}
