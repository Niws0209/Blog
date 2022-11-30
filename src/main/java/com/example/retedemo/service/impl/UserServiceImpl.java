package com.example.retedemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.retedemo.controller.dto.UserDto;
import com.example.retedemo.entity.User;
import com.example.retedemo.mapper.UserMapper;
import com.example.retedemo.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lyk
 * @since 2022-11-17
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public boolean login(UserDto userDto) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",userDto.getUsername());
        queryWrapper.eq("password",userDto.getPassword());
        try {
            User one = getOne(queryWrapper);
            return one != null;
        } catch (Exception e) {
            return false;
        }
    }
}
