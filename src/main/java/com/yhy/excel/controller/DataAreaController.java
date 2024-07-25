package com.yhy.excel.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.yhy.excel.entity.DataArea;
import com.yhy.excel.entity.DataAreaTemp;
import com.yhy.excel.service.DataAreaService;
import com.yhy.excel.service.DataAreaTempService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 计算地区的数据之和
 *
 * @author Echo
 * @date 2024/7/12 16:04
 */
@RestController
public class DataAreaController {
    @Resource
    private DataAreaService dataAreaService;
    @Resource
    private DataAreaTempService dataAreaTempService;

    /**
     * 将临时表的数据更新到正式表
     * @return
     */
    @PutMapping("/update")
    public String update() {
        List<DataAreaTemp> tempList = dataAreaTempService.lambdaQuery().list();
        List<DataArea> list = dataAreaService.lambdaQuery().list();

        //将正式表数据转成map，key是l3+l4+l5
        Map<String, DataArea> collect = list.stream().collect(Collectors.toMap(
                vo -> vo.getL3() + "|" + vo.getL4() + "|" + vo.getL5(),
                v -> v,
                (existing, replacement) -> existing
        ));

        List<DataArea> updateList = new ArrayList<>();
        int count = 0;
        //遍历临时表，找到对应的正式表数据，更新
        for (DataAreaTemp vo : tempList) {
            //先看能不能获取到
            DataArea dataArea = collect.get(vo.getL3() + "|" + vo.getL4() + "|" + vo.getL5());
            if (dataArea != null) {
                count++;
                dataArea.setCensusTotal(vo.getCensusTotal());
                dataArea.setUserTotal(vo.getUserTotal());
                dataArea.setChronicDiseaseTotal(vo.getChronicDiseaseTotal());
                dataArea.setEmphasisTotal(vo.getEmphasisTotal());
                updateList.add(dataArea);
            }
        }
        dataAreaService.updateBatchById(updateList);
        return "一共修改了：" + count + " 条数据";
    }


    @PutMapping("/sumL5")
    @Transactional
    public String sumL5() {
        // 查询L3、L4、L5不是空串的DataArea，并按L3和L4分组计算各个字段的总和
        List<DataArea> nonEmptyL3L4L5List = dataAreaService.lambdaQuery()
                .ne(DataArea::getL5, "") // L5不是空串
                .ne(DataArea::getL4, "") // L4不是空串
                .isNotNull(DataArea::getL3) // L3不是NULL
                .select(DataArea::getL3, DataArea::getL4, DataArea::getL5,
                        DataArea::getCensusTotal, DataArea::getUserTotal,
                        DataArea::getChronicDiseaseTotal, DataArea::getEmphasisTotal)
                .list();

        // 使用Map来存储分组后的汇总结果
        Map<String, DataArea> groupedMap = nonEmptyL3L4L5List.stream()
                .filter(vo -> StringUtils.isNotBlank(vo.getL3()) && StringUtils.isNotBlank(vo.getL4())) // 过滤掉L3和L4为空串的情况
                .collect(Collectors.toMap(
                        vo -> vo.getL3() + "|" + vo.getL4(),
                        v -> {
                            DataArea dataArea = new DataArea();
                            dataArea.setL3(v.getL3());
                            dataArea.setL4(v.getL4());
                            dataArea.setCensusTotal(v.getCensusTotal());
                            dataArea.setUserTotal(v.getUserTotal());
                            dataArea.setChronicDiseaseTotal(v.getChronicDiseaseTotal());
                            dataArea.setEmphasisTotal(v.getEmphasisTotal());
                            return dataArea;
                        },
                        (existing, replacement) -> {
                            existing.setCensusTotal(existing.getCensusTotal() + replacement.getCensusTotal());
                            existing.setUserTotal(existing.getUserTotal() + replacement.getUserTotal());
                            existing.setChronicDiseaseTotal(existing.getChronicDiseaseTotal() + replacement.getChronicDiseaseTotal());
                            existing.setEmphasisTotal(existing.getEmphasisTotal() + replacement.getEmphasisTotal());
                            return existing;
                        }
                ));

        int count = 0;
        // 遍历map并更新L5为空串的DataArea的各个字段
        for (Map.Entry<String, DataArea> entry : groupedMap.entrySet()) {
            String[] keys = entry.getKey().split("\\|");
            String l3 = keys[0];
            String l4 = keys[1];
            DataArea totalDataArea = entry.getValue();

            // 更新L3和L4相同且L5为空串的DataArea
            LambdaUpdateWrapper<DataArea> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(DataArea::getL3, l3)
                    .eq(DataArea::getL4, l4)
                    .and(wrapper -> wrapper.isNull(DataArea::getL5).or().eq(DataArea::getL5, "")) // 注意这里使用isNull来判断L5为空串的情况
                    .set(DataArea::getCensusTotal, totalDataArea.getCensusTotal())
                    .set(DataArea::getUserTotal, totalDataArea.getUserTotal())
                    .set(DataArea::getChronicDiseaseTotal, totalDataArea.getChronicDiseaseTotal())
                    .set(DataArea::getEmphasisTotal, totalDataArea.getEmphasisTotal());

            dataAreaService.update(null, updateWrapper);
        }

        return "一共更新了：" + count + " 条数据";
    }


    @PutMapping("/sumL4")
    @Transactional
    public String sumL4() {
        // 查询L4、L3不是空串或null，但L5是null或空串的DataArea，并按L4和L3分组计算各个字段的总和
        List<DataArea> nonEmptyL3L4List = dataAreaService.lambdaQuery()
                .and(wrapper -> wrapper.isNotNull(DataArea::getL4).and(wrapper1 -> wrapper1.ne(DataArea::getL4, "")))
                .and(wrapper -> wrapper.isNotNull(DataArea::getL3).and(wrapper1 -> wrapper1.ne(DataArea::getL3, "")))
                .and(wrapper -> wrapper.isNull(DataArea::getL5).or().eq(DataArea::getL5, "")) // L5是空或空串
                .select(DataArea::getL3, DataArea::getL4,
                        DataArea::getCensusTotal, DataArea::getUserTotal,
                        DataArea::getChronicDiseaseTotal, DataArea::getEmphasisTotal)
                .list();

        // 使用Map来存储分组后的汇总结果
        Map<String, DataArea> groupedMap = nonEmptyL3L4List.stream()
                .collect(Collectors.toMap(
                        vo -> vo.getL3(),
                        v -> {
                            DataArea dataArea = new DataArea();
                            dataArea.setL3(v.getL3());
                            dataArea.setL4(v.getL4());
                            dataArea.setCensusTotal(v.getCensusTotal());
                            dataArea.setUserTotal(v.getUserTotal());
                            dataArea.setChronicDiseaseTotal(v.getChronicDiseaseTotal());
                            dataArea.setEmphasisTotal(v.getEmphasisTotal());
                            return dataArea;
                        },
                        (existing, replacement) -> {
                            existing.setCensusTotal(existing.getCensusTotal() + replacement.getCensusTotal());
                            existing.setUserTotal(existing.getUserTotal() + replacement.getUserTotal());
                            existing.setChronicDiseaseTotal(existing.getChronicDiseaseTotal() + replacement.getChronicDiseaseTotal());
                            existing.setEmphasisTotal(existing.getEmphasisTotal() + replacement.getEmphasisTotal());
                            return existing;
                        }
                ));

        int count = 0;
        // 遍历map并更新L3为空串的DataArea的各个字段
        for (Map.Entry<String, DataArea> entry : groupedMap.entrySet()) {
            String[] keys = entry.getKey().split("\\|");
            String l3 = keys[0];
            DataArea totalDataArea = entry.getValue();

            // 更新L4和L3相同且L5为空串/null的DataArea
            LambdaUpdateWrapper<DataArea> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper
                    .eq(DataArea::getL3, l3)
                    .isNull(DataArea::getL4)
                    .isNull(DataArea::getL5)// 注意这里使用isNull来判断L3为空串的情况
                    .and(wrapper -> wrapper.isNull(DataArea::getL5).or().eq(DataArea::getL5, "")) // L5是空或空串
                    .set(DataArea::getCensusTotal, totalDataArea.getCensusTotal())
                    .set(DataArea::getUserTotal, totalDataArea.getUserTotal())
                    .set(DataArea::getChronicDiseaseTotal, totalDataArea.getChronicDiseaseTotal())
                    .set(DataArea::getEmphasisTotal, totalDataArea.getEmphasisTotal());

            dataAreaService.update(null, updateWrapper);
        }

        return "一共更新了：" + count + " 条数据";
    }
}
