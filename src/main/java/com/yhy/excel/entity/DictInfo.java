package com.yhy.excel.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author Echo
 * @date 2024/5/7 18:01
 */
@Data
@TableName("dict_info")
public class DictInfo {
    private String name;

    private Integer value;
}
