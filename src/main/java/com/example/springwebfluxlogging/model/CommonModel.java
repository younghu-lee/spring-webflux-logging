package com.example.springwebfluxlogging.model;

import lombok.Data;
import org.springframework.http.codec.multipart.FilePart;
public class CommonModel {

    @Data
    public static class CommonRequestParam {
        private String arg1;

        private String arg2;

        private int arg3;
    }

    @Data
    public static class MultipartParam extends CommonRequestParam {
        private FilePart file;
    }
}
