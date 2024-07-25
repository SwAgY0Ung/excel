package com.yhy.excel.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author Echo
 * @date 2024/5/7 18:00
 */
@Data
@TableName("drug_info")
public class DrugInfo {
    private Long id;

    private String name;
}
