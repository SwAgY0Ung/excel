package com.yhy.excel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 四诊症候程度详情
 * @TableName old_four_diag_degree_detail
 */
@TableName(value ="old_four_diag_degree_detail")
@Data
public class OldFourDiagDegreeDetail implements Serializable {
    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 症候程度类型id
     */
    private Long degreeId;

    /**
     * 名称
     */
    private String name;

    /**
     * 排序编码
     */
    private String sort;

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