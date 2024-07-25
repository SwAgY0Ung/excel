package com.yhy.excel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 四诊症候程度类型
 * @TableName old_four_diag_degree
 */
@TableName(value ="old_four_diag_degree")
@Data
public class OldFourDiagDegree implements Serializable {
    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 四诊症候id
     */
    private Long fourDiagId;

    /**
     * 程度名称
     */
    private String degreeName;

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