package com.yhy.excel.service;

import com.yhy.excel.entity.Excel;
import com.yhy.excel.mapper.ExcelMapper;
import com.yhy.excel.utils.DataListListener;
//import cn.litblue.excel.utils.ThreadQuery;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
public class ExcelServiceImpl extends ServiceImpl<ExcelMapper,Excel> implements ExcelService {

    /**
     * 读取Excel，  并传入数据库
     * @param file
     * @return 是否成功导入数据库
     */
    @Transactional
    public boolean importExcelByEasyExcel(MultipartFile file){
        try {
            // 这里 需要指定读用哪个class去读，然后读取第一个sheet
            // 需要将 ExcelMapper 传入构造器
            // doRead() 方法中有 finish() 方法，文件流会自动关闭

            /* 只读第一张工作表 */
            // EasyExcel.read(file.getInputStream(), Excel.class, new DataListListener(excelMapper)).sheet().doRead();
            // EasyExcel.read(file.getInputStream(), Excel.class, new DataListListener(jdbcTemplate)).sheet().doRead();


            // 读取所有工作表
            //EasyExcel.read(file.getInputStream(), Excel.class, new DataListListener(excelMapper)).doReadAll();
            /**
             * 传三个参数
             * 1. 输入流：读取Excel文件
             * 2. Excel表头对应的实体类
             * 3. 监听器：读取到每行代码就会触发
             */
            EasyExcel.read(file.getInputStream(),
                            Excel.class,
                            new DataListListener())
                    .sheet().doRead();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
