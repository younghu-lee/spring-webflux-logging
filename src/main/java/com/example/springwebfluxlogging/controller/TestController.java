package com.example.springwebfluxlogging.controller;

import com.example.springwebfluxlogging.model.CommonModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
public class TestController {

    @PostMapping("/post")
    public Mono<Void> postTest(@RequestBody CommonModel.CommonRequestParam param) {
        return Mono.empty();
    }

    @PutMapping("/put")
    public Mono<Void> putTest(@RequestBody CommonModel.CommonRequestParam param) {
        return Mono.empty();
    }

    @GetMapping("/get")
    public Mono<Void> getTest(@RequestParam(name = "arg1") String arg1, @RequestParam(name = "arg2") String arg2) {
        return Mono.empty();
    }

    @PostMapping(value = "/multipart", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Mono<Void> multipartTest(@ModelAttribute CommonModel.MultipartParam param) {
        return Mono.empty();
    }
}
