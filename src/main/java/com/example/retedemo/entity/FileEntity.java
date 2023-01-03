package com.example.retedemo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.criteria.CriteriaBuilder;

@Data
@TableName("sys_file")
public class FileEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String type;
    private String md5;
    private Long size;
    private String url;
    private Boolean isDelete;
    private Boolean enable;

}
