package com.yhy.excel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 四诊分组
 * @TableName four_diag_type
 */
@TableName(value ="four_diag_type")
@Data
public class FourDiagType implements Serializable {
    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 上级id，默认0为最高级
     */
    private Long parentId;

    /**
     * 顶级id(暂不使用)
     */
    private Long topId;

    /**
     * 名称
     */
    private String definition;

    /**
     * 排序编码
     */
    private String sort;

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

    /**
     * 0禁用 1启用
     */
    private Integer state;

    /**
     * 0未删除 1已删除
     */
    private Integer delFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}