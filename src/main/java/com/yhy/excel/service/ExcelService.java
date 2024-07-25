package com.yhy.excel.service;

import com.yhy.excel.entity.Excel;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

public interface ExcelService extends IService<Excel> {
    boolean importExcelByEasyExcel(MultipartFile file);
}
