package com.yhy.excel.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@TableName("herb_pieces_info")
public class Excel implements Serializable {
    @ExcelIgnore
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ExcelProperty(value ="中药饮片代码",index = 0)
    private String code;

    @ExcelProperty(value ="中药饮片名称",index = 1)
    private String name;

    @ExcelProperty(value ="药材名称",index = 2)
    @TableField(exist = false)
    private String drugName;

    @ExcelIgnore
    private Long drugId;

    @ExcelProperty(value ="炮制方法",index = 3)
    private String processMethod;

    @ExcelProperty(value ="功效分类",index = 4)
    @TableField(exist = false)
    private String effectTypeName;

    @ExcelIgnore
    private Integer effectType;

    @ExcelProperty(value ="药材科来源",index = 5)
    private String section;

    @ExcelProperty(value ="药材种来源",index = 6)
    private String species;

    @ExcelProperty(value ="药用部位",index = 7)
    private String site;

    @ExcelProperty(value ="性味与归经",index = 8)
    private String natureFlavor;

    @ExcelProperty(value ="功能与主治",index = 9)
    private String indications;

    @ExcelProperty(value ="用法与用量",index = 10)
    private String usageDosage;

    @ExcelProperty(value ="国家医保支付政策",index = 11)
    private String chnInsurancePay;

    @ExcelProperty(value ="省级医保支付政策",index = 12)
    private String hbInsurancePay;

    @ExcelProperty(value ="标准名称来源",index = 13)
    private String source;

    @ExcelProperty(value ="标准页码",index = 14)
    private String pageNum;

    @ExcelProperty(value ="地区",index = 15)
    private String area;

    @ExcelProperty(value ="省码",index = 16)
    private String hbCode;

    @ExcelProperty(value ="省饮片名称",index = 17)
    private String hbName;

    @ExcelProperty(value ="医保支付类别",index = 18)
    private String insurancePayType;

    @ExcelProperty(value ="首付比例",index = 19)
    private Integer payRatio;
    /**
     * 公共字段 不需要动
     */
    @ExcelIgnore
    private LocalDateTime createTime;
    @ExcelIgnore
    private LocalDateTime updateTime;
    @ExcelIgnore
    private Long createUser;
    @ExcelIgnore
    private Long updateUser;
    @ExcelIgnore
    private Integer delFlag;
    @ExcelIgnore
    private Integer state;
    @ExcelIgnore
    private String sort;
}
