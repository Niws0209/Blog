package com.example.retedemo.controller;


import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.retedemo.common.Constants;
import com.example.retedemo.common.Result;
import com.example.retedemo.controller.dto.UserDto;
import com.example.retedemo.entity.User;
import com.example.retedemo.service.IUserService;
import com.example.retedemo.utils.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lyk
 * @since 2022-11-17
 */
@RestController
@Slf4j
public class UserController {
        @Resource
        private IUserService userService;

        @PostMapping("/api/addedit")
        public Result save(@RequestBody User user){
            return  Result.success(userService.saveOrUpdate(user));
        }

        @PostMapping("/api/login")
        public Result login(@RequestBody UserDto userDto) {
            String username = userDto.getUsername();
            String password = userDto.getPassword();
            String avatar = userDto.getAvatar();
            String nickname = userDto.getNickname();
            if (StrUtil.isBlank(username) || StrUtil.isBlank(password)){
                return Result.error(Constants.CODE_400,"参数不足");
            }
            UserDto dto = userService.login(userDto);
            return Result.success(dto);
        }

        @PostMapping("/api/register")
        public Result register(@RequestBody UserDto userDto) {
            String username = userDto.getUsername();
            String password = userDto.getPassword();
            if (StrUtil.isBlank(username) || StrUtil.isBlank(password)){
                return Result.error(Constants.CODE_400,"参数不足");
            }
            return Result.success(userService.register(userDto));
        }

        @GetMapping("/api/delete/{id}")
        public Result delete(@PathVariable Integer id){
            return Result.success(userService.removeById(id));
        }

        @PostMapping("/api/delete/batch")
        public Result deleteBatch(@RequestBody List<Integer> ids){
            return Result.success(userService.removeByIds(ids));
        }

        @GetMapping
        public Result findAll(){
            return Result.success(userService.list());
        }

        @GetMapping("/api/username/{username}")
        public Result findOne(@PathVariable String username){
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username", username);
            return Result.success(userService.getOne(queryWrapper));
    }

        @GetMapping("/api/{id}")
        public Result findOne(@PathVariable Integer id){
            return Result.success(userService.list());
        }

        @GetMapping("/api/page")
        public Result findPage(@RequestParam Integer pageNum,
                                   @RequestParam Integer pageSize,
                                   @RequestParam(defaultValue = "") String  username,
                                   @RequestParam(defaultValue = "") String  email,
                                   @RequestParam(defaultValue = "") String  address) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//            queryWrapper.like(!Strings.isEmpty(username),"username",username);
//            queryWrapper.like(!Strings.isEmpty(email),"email",email);
//            queryWrapper.like(!Strings.isEmpty(address),"address",address);
            queryWrapper.orderByDesc("id");
            if(!"".equals(username)){
                queryWrapper.like("username",username);
            }
            if(!"".equals(email)){
                queryWrapper.like("email",email);
            }
            if(!"".equals(address)){
                queryWrapper.like("address",address);
            }

            User currentUser = TokenUtils.getCurrentUser();
            if (currentUser != null) {
                log.info(currentUser.getNickname());
            }
            return Result.success(userService.page(new Page<>(pageNum,pageSize),queryWrapper));
        }


        @GetMapping("/api/export")
        public void export(HttpServletResponse response) throws Exception{
            //查出数据存在list中
            List<User> list = userService.list();

            //在内存操作，写出到浏览器
            ExcelWriter writer = ExcelUtil.getWriter(true);

            //自定义标题别名
//            writer.addHeaderAlias("username" , "用户名");
//            writer.addHeaderAlias("password" , "密码");
//            writer.addHeaderAlias("nickname" , "昵称");
//            writer.addHeaderAlias("email" , "邮箱");
//            writer.addHeaderAlias("phone" , "电话");
//            writer.addHeaderAlias("address" , "地址");
//            writer.addHeaderAlias("createTime" , "创建时间");

            //一次性写出list中的对象到excel，使用默认样式，强制输出标题
            writer.write(list , true);

            //设置浏览器响应的格式
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            String fileName = URLEncoder.encode("用户信息", "UTF-8");
            response.setHeader("Content-Disposition","attachment;filename=" + fileName + ".xlsx");

            ServletOutputStream out = response.getOutputStream();
            writer.flush(out, true);
            out.close();
            writer.close();

        }

        @PostMapping("/api/import")
        public Result imp(MultipartFile file) throws Exception {
            InputStream inputStream = file.getInputStream();
            ExcelReader reader = ExcelUtil.getReader(inputStream);
            List<User> list = reader.readAll(User.class);
            userService.saveBatch(list);
            return Result.success(true);
        }

}

