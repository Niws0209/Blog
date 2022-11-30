package com.example.retedemo.controller.dto;

import lombok.Data;

/**
 *接收前端传的数据
 * */

@Data
public class UserDto {
    private String username;
    private String password;
}
