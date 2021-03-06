package com.hdjunction.tinyERMService.dto;

public enum StatusEnum {
    OK(200, "OK"),
    NO_CONTENT(204, "NO CONTENT"),
    BAD_REQUEST(400, "BAD_REQUEST"),
    NOT_FOUND(404, "NOT_FOUND"),

    INTERNAL_SERER_ERROR(500, "INTERNAL_SERVER_ERROR");

    int statusCode;
    String code;

    StatusEnum(int statusCode, String code) {
        this.statusCode = statusCode;
        this.code = code;
    }
}

