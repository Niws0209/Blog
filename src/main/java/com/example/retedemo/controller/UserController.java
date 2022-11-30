package com.example.retedemo.controller;


import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.retedemo.controller.dto.UserDto;
import com.example.retedemo.entity.User;
import com.example.retedemo.service.IUserService;
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
public class UserController {
        @Resource
        private IUserService userService;

        @PostMapping("/api/addedit")
        public boolean save(@RequestBody User user){
            return userService.saveOrUpdate(user);
        }

        @PostMapping("/api/login")
        public boolean login(@RequestBody UserDto userDto) {
            String username = userDto.getUsername();
            String password = userDto.getPassword();
            if (StrUtil.isBlank(username) || StrUtil.isBlank(password)){
                return false;
            }
            return userService.login(userDto);
        }

        @GetMapping("/api/delete/{id}")
        public boolean delete(@PathVariable Integer id){
            return userService.removeById(id);
        }

        @PostMapping("/api/delete/batch")
        public boolean deleteBatch(@RequestBody List<Integer> ids){
                return userService.removeByIds(ids);
        }

        @GetMapping
        public List<User> findAll(){
            return userService.list();
        }

        @GetMapping("/api/{id}")
        public List<User> findOne(@PathVariable Integer id){
            return userService.list();
        }

        @GetMapping("/api/page")
        public Page<User> findPage(@RequestParam Integer pageNum,
                                   @RequestParam Integer pageSize,
                                   @RequestParam(defaultValue = "") String  username,
                                   @RequestParam(defaultValue = "") String  email,
                                   @RequestParam(defaultValue = "") String  address) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.like(!Strings.isEmpty(username),"username",username);
            queryWrapper.like(!Strings.isEmpty(email),"email",email);
            queryWrapper.like(!Strings.isEmpty(address),"address",address);
            queryWrapper.orderByDesc("id");
            return userService.page(new Page<>(pageNum,pageSize),queryWrapper);
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
        public boolean imp(MultipartFile file) throws Exception {
            InputStream inputStream = file.getInputStream();
            ExcelReader reader = ExcelUtil.getReader(inputStream);
            List<User> list = reader.readAll(User.class);
            userService.saveBatch(list);
            return true;
        }

}

