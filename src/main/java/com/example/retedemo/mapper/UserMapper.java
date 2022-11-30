package com.example.retedemo.mapper;

import com.example.retedemo.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lyk
 * @since 2022-11-17
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
