package com.yhy.excel.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yhy.excel.entity.Excel;
import com.yhy.excel.service.ExcelService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.RecursiveAction;

@Slf4j
public class InsertBatchTask extends RecursiveAction {

//    private JdbcTemplate jdbcTemplate;
    private ExcelService excelService;

    /** 待插入数据  */
    List<Excel> excelList;

    // 每一批次插入的数据
    private final static int BATCH_SIZE = 1000;


    public InsertBatchTask(ExcelService excelService,List<Excel> excelList){
        this.excelService = excelService;
        this.excelList = excelList;
    }


    @Override
    protected void compute() {



        // 当要插入的数据<1000,则直接插入
        if (excelList.size() <= BATCH_SIZE){
            saveExcelByJdbcTemplate();
        } else {

            int size = excelList.size();

            // 进行分组
            InsertBatchTask insertBatchTask1 = new InsertBatchTask(excelService,excelList.subList(0,size/2));
            InsertBatchTask insertBatchTask2 = new InsertBatchTask(excelService,excelList.subList(size/2,size));

            // 任务并发执行
            invokeAll(insertBatchTask1, insertBatchTask2);

        }
    }


    private void saveExcelByJdbcTemplate(){
        excelService.saveBatch(excelList);
    }

}
