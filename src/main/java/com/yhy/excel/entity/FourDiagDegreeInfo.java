package com.yhy.excel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 症候程度名称
 * @TableName four_diag_degree_info
 */
@TableName(value ="four_diag_degree_info")
@Data
public class FourDiagDegreeInfo implements Serializable {
    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 症候程度分组id
     */
    private Long fourDiagDegreeTypeId;

    /**
     * 名称
     */
    private String definition;

    /**
     * 排序编码
     */
    private String sort;

    /**
     * 解释
     */
    private String annotations;

    /**
     * 备注
     */
    private String notes;

    /**
     * 0禁用 1启用
     */
    private Integer state;

    /**
     * 0未删除 1已删除
     */
    private Integer delFlag;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private Long createUser;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 修改人
     */
    private Long updateUser;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}