package com.yhy.excel.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yhy.excel.entity.DictInfo;
import com.yhy.excel.entity.DrugInfo;
import com.yhy.excel.entity.Excel;
//import cn.litblue.excel.mapper.ExcelJdbcTemplate;
import com.yhy.excel.mapper.DictInfoMapper;
import com.yhy.excel.mapper.DrugInfoMapper;
import com.yhy.excel.mapper.ExcelMapper;
import com.yhy.excel.service.ExcelService;
import com.yhy.excel.service.ExcelServiceImpl;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

import static com.baomidou.mybatisplus.core.toolkit.Wrappers.lambdaQuery;

/**
 * 这里的 DataListListener 不能被spring管理，要每次读取excel都要new,然后里面用到spring可以构造方法传进去
 */

@Slf4j
@Component
@Scope("prototype")
public class DataListListener extends AnalysisEventListener<Excel> {

    /**
     * 每隔3000条存储数据库，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 20000;
    private List<Excel> excelList = new ArrayList<>();

    /**
     * 数据操作
     */
    private ExcelService excelService = new ExcelServiceImpl();

    private Integer IgnoreNum = 0;

    /**
     * 线程池
     */
    ForkJoinPool forkJoinPool = new ForkJoinPool(8);

    public DataListListener(){}






    /**
     * 这个每一条数据解析都会来调用
     *
     * @param excel
     * @param analysisContext
     */
    @Override
    @Transactional
    public void invoke(Excel excel, AnalysisContext analysisContext) {

        if (excel.getDrugName() == null || excel.getDrugName().isEmpty()) {
            System.out.println("没对应药材的行数" + ++IgnoreNum);
        } else {

            /**
             * 公共字段设置
             */
            excel.setCreateTime(LocalDateTime.now());
            excel.setUpdateTime(LocalDateTime.now());
            excel.setCreateUser(1L);
            excel.setUpdateUser(1L);
            excel.setDelFlag(0);
            excel.setState(1);
            excel.setSort("1");

//        excel.setEffectType(1);
//        excel.setDrugId(1L);

//      查字典，给effectType赋值
            DictInfoMapper dictInfoMapper = SpringContextUtil.getBean(DictInfoMapper.class);
            LambdaQueryWrapper<DictInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DictInfo::getName, excel.getEffectTypeName());
            DictInfo dictInfo = dictInfoMapper.selectOne(wrapper);
            if (dictInfo != null) {
                excel.setEffectType(dictInfo.getValue());
            } else {
                int i = 1 / 0;
            }

            //查drug
            excel.setDrugName(excel.getDrugName().replace("（", "(").replace("）", ")"));
            DrugInfoMapper drugInfoMapper = SpringContextUtil.getBean(DrugInfoMapper.class);
            LambdaQueryWrapper<DrugInfo> wrapper1 = new LambdaQueryWrapper<>();
            wrapper1.eq(DrugInfo::getName, excel.getDrugName());
            DrugInfo drugInfo = drugInfoMapper.selectOne(wrapper1);
            if (drugInfo != null) {
                excel.setDrugId(drugInfo.getId());
            } else {
                System.out.println(excel.getDrugName());
                int i = 2 / 0;
            }


            //添加
            excelList.add(excel);
        }


        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (excelList.size() >= BATCH_COUNT) {
            saveExcelByJoinFork();
            // 存储完成清理 list
            excelList.clear();
        }
    }

    /**
     * 所有数据解析完成了，会来调用
     * 这里处理的是分批剩下的最后一批数据.
     *
     * @param analysisContext
     */
    @Override
    @Transactional
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        excelList.forEach(excel -> {
            /**
             * 公共字段设置
             */
            excel.setCreateTime(LocalDateTime.now());
            excel.setUpdateTime(LocalDateTime.now());
            excel.setCreateUser(1L);
            excel.setUpdateUser(1L);
            excel.setDelFlag(0);
            excel.setState(1);
        });
        saveExcelByJoinFork();
        System.out.println("doAfterAllAnalysed方法执行完毕");
        System.out.println("没对应药材的行数" + ++IgnoreNum);
        excelList.clear();
    }

    private void saveExcelByJoinFork(){
        InsertBatchTask insertBatchTask = new InsertBatchTask(excelService, excelList);
        forkJoinPool.invoke(insertBatchTask);
    }


}
