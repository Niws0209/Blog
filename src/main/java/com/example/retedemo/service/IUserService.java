package com.example.retedemo.service;

import com.example.retedemo.controller.dto.UserDto;
import com.example.retedemo.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lyk
 * @since 2022-11-17
 */
public interface IUserService extends IService<User> {

    UserDto login(UserDto userDto);

    User register(UserDto userDto);
}
