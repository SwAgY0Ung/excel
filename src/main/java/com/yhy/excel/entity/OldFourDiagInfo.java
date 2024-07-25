package com.yhy.excel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 四诊症候数据
 * @TableName old_four_diag_info
 */
@TableName(value ="old_four_diag_info")
@Data
public class OldFourDiagInfo implements Serializable {
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
     * 性别 0不区分 1男 2女
     */
    private Integer sex;

    /**
     * 有无程度 0无 1有
     */
    private Integer haveDegree;

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