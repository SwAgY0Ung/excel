package com.yhy.excel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName data_area
 */
@TableName(value ="data_area")
@Data
public class DataArea implements Serializable {
    /**
     * 
     */
    @TableId
    private String id;

    /**
     * 名称
     */
    private String name;

    /**
     * 
     */
    private String fullName;

    /**
     * 父级区划代码
     */
    private String pid;

    /**
     * 简短区划代码，唯一
     */
    private String sid;

    /**
     * 简短父级区划代码
     */
    private String spid;

    /**
     * 同级排序
     */
    private Integer sort;

    /**
     * 叶节点
     */
    private Integer leaf;

    /**
     * 级别
     */
    private Integer level;

    /**
     * 
     */
    private String l1;

    /**
     * 
     */
    private String l2;

    /**
     * 
     */
    private String l3;

    /**
     * 
     */
    private String l4;

    /**
     * 
     */
    private String l5;

    /**
     * 居民总数
     */
    private Integer userTotal;

    /**
     * 65岁以上老人数量
     */
    private Integer theAgedTotal;

    /**
     * 慢病人群数量
     */
    private Integer chronicDiseaseTotal;

    /**
     * 重点人群数量
     */
    private Integer emphasisTotal;

    /**
     * 医生数量
     */
    private Integer doctorTotal;

    /**
     * 0-6岁孩子数量
     */
    private Integer childTotal;

    /**
     * 优抚人群
     */
    private Integer nurtureTotal;

    /**
     * 残疾人数量
     */
    private Integer spoilTotal;

    /**
     * 低保户
     */
    private Integer subsidyTotal;

    /**
     * 精神病
     */
    private Integer insanityTotal;

    /**
     * 户籍人数量
     */
    private Integer censusTotal;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}