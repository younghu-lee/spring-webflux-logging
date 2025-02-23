package com.example.springwebfluxlogging.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;

@Slf4j
public class RequestLoggingDecorator extends ServerHttpRequestDecorator {

    private final StringBuilder body = new StringBuilder();

    public RequestLoggingDecorator(ServerHttpRequest delegate) {
        super(delegate);
    }

    @Override
    public Flux<DataBuffer> getBody() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        return super.getBody().publishOn(Schedulers.boundedElastic()).doOnNext(dataBuffer -> {
            try {
                Channels.newChannel(baos).write(dataBuffer.readableByteBuffers().next());
                this.body.append(new String(baos.toByteArray(), StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public String getFullBody() {
        return this.body.toString();
    }
}
