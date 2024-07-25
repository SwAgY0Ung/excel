package com.yhy.excel.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yhy.excel.entity.*;
import com.yhy.excel.mapper.OldFourDiagInfoMapper;
import com.yhy.excel.service.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Echo
 * @date 2024/6/14 09:20
 */
@RestController
public class ImportController {

    @Resource
    private OldFourDiagInfoMapper oldFourDiagInfoMapper;
    @Resource
    private FourDiagTypeService fourDiagTypeService;
    @Resource
    private FourDiagInfoService fourDiagInfoService;

    @Resource
    private OldFourDiagDegreeService oldFourDiagDegreeService;
    @Resource
    private OldFourDiagDegreeDetailService oldFourDiagDegreeDetailService;
    @Resource
    private FourDiagDegreeTypeService fourDiagDegreeTypeService;
    @Resource
    private FourDiagDegreeInfoService fourDiagDegreeInfoService;

    //用来保存需要忽略的老四诊的"问诊"下面的id
//    private List<Long> ignoreFourDiagTypeIds = new ArrayList<>();
    private List<Long> ignoreFourDiagInfoIds = new ArrayList<>();
    private List<Long> ignoreDegreeTypeIds = new ArrayList<>();
    private List<Long> ignoreDegreeInfoIds = new ArrayList<>();

    /**
     * 导入新的四诊类型和证型信息
     * @return 是否成功
     */
    @GetMapping("/insertFourDiagTypeAndInfo")
    public String insertFourDiagTypeAndInfo() {
        /*
        思路：
        1. 先获取所有 id
        2. 再获取所有 parent_id（并且查询条件in所有id，这样就可以排除parent_id是0的)
        3. 将上面两个集合进行比较，全部id中没有parent_id的部分，则说明没有下级，插入到four_diag_type表中
        4. 将上面两个集合进行比较, 全部id中有parent_id的部分，则说明有下级，插入到four_diag_info表中
         */
        // 获取所有记录的ID列表
        LambdaQueryWrapper<OldFourDiagInfo> oldFourDiagInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        oldFourDiagInfoLambdaQueryWrapper.eq(OldFourDiagInfo::getDelFlag, 0);
        List<Long> allIds = oldFourDiagInfoMapper.selectList(oldFourDiagInfoLambdaQueryWrapper)
                .stream()
                .map(OldFourDiagInfo::getId)
                .collect(Collectors.toList());
        // 获取所有 parent_id 在 allIds 中的记录ID列表
        List<Long> parentIds = oldFourDiagInfoMapper.selectList(Wrappers.<OldFourDiagInfo>lambdaQuery()
                        .select(OldFourDiagInfo::getParentId)
                        .in(OldFourDiagInfo::getParentId, allIds)
                        .eq(OldFourDiagInfo::getDelFlag, 0))
                .stream()
                .map(OldFourDiagInfo::getParentId)
                .distinct()
                .collect(Collectors.toList());

        // 获取所有有子级的记录,将所有有子级的记录插入到fourDiagType表中
        insertFourDiagType(parentIds);

        // 获取所有没有子级的记录,将所有没有子级的记录(最下级)插入到fourDiagInfo表中
        insertFourDiagInfo(parentIds);

//        System.out.println("排除问诊的四诊症候名称id:" + ignoreFourDiagTypeIds);
        System.out.println("排除问诊的四诊症候名称id:" + ignoreFourDiagInfoIds);
        return "four_diag_info 迁移到 four_diag_type成功";
    }

    private void insertFourDiagType(List<Long> parentIds) {
        //获取所有有子级的记录
        List<OldFourDiagInfo> oldFourDiagInfoHasChildren = oldFourDiagInfoMapper.selectList(Wrappers.<OldFourDiagInfo>lambdaQuery()
                .in(OldFourDiagInfo::getId, parentIds));
        //将所有有子级的记录插入到fourDiagType表中
        List<FourDiagType> entities = new ArrayList<>();
        oldFourDiagInfoHasChildren.forEach(oldFourDiagInfo -> {
//            if (oldFourDiagInfo.getParentId().equals(566217217671168000L)) {
//                ignoreFourDiagTypeIds.add(oldFourDiagInfo.getId());
//            } else {
                FourDiagType entity = new FourDiagType();
                //业务字段
                entity.setId(oldFourDiagInfo.getId());
                entity.setParentId(oldFourDiagInfo.getParentId());
                entity.setDefinition(oldFourDiagInfo.getName());
                entity.setSort(oldFourDiagInfo.getSort());
                //公共字段
                entity.setCreateTime(oldFourDiagInfo.getCreateTime());
                entity.setCreateUser(oldFourDiagInfo.getCreateUser());
                entity.setUpdateTime(oldFourDiagInfo.getUpdateTime());
                entity.setUpdateUser(oldFourDiagInfo.getUpdateUser());
                entity.setState(oldFourDiagInfo.getState());
                entity.setDelFlag(oldFourDiagInfo.getDelFlag());
                entities.add(entity);
//            }
        });
        fourDiagTypeService.saveBatch(entities);
    }

    private void insertFourDiagInfo(List<Long> parentIds) {
        //获取所有没有子级的记录
        List<OldFourDiagInfo> oldFourDiagInfoDontHasChildren = oldFourDiagInfoMapper.selectList(Wrappers.<OldFourDiagInfo>lambdaQuery()
                .eq(OldFourDiagInfo::getDelFlag, 0)
                .notIn(OldFourDiagInfo::getId, parentIds));
        //将所有没有子级的记录(最下级)插入到fourDiagInfo表中
        //但是存在特殊情况：有些最下级的记录和别的不是最下级的记录是平级，也就是parentId相同，这是不对的
        //解决方法，首先要找到parentId相同的记录，
        List<FourDiagType> entities = new ArrayList<>();
        oldFourDiagInfoDontHasChildren.forEach(oldFourDiagInfo -> {
            //TODO 在老四诊的最下级中：如果上级是问诊，则记录一下，不新增
            if (oldFourDiagInfo.getParentId().equals(566217217671168000L)) {
                ignoreFourDiagInfoIds.add(oldFourDiagInfo.getId());
            } else {
                //老逻辑，全部添加
                FourDiagType entity = new FourDiagType();
                //业务字段
                entity.setId(oldFourDiagInfo.getId());
                entity.setParentId(oldFourDiagInfo.getParentId());
                entity.setDefinition(oldFourDiagInfo.getName());
                entity.setSort(oldFourDiagInfo.getSort());
//                entity.setSex(oldFourDiagInfo.getSex());
//                entity.setNotes(oldFourDiagInfo.getNotes());
                //公共字段
                entity.setCreateTime(oldFourDiagInfo.getCreateTime());
                entity.setCreateUser(oldFourDiagInfo.getCreateUser());
                entity.setUpdateTime(oldFourDiagInfo.getUpdateTime());
                entity.setUpdateUser(oldFourDiagInfo.getUpdateUser());
                entity.setState(oldFourDiagInfo.getState());
                entity.setDelFlag(oldFourDiagInfo.getDelFlag());
                entities.add(entity);
            }
        });
        fourDiagTypeService.saveBatch(entities);
    }


    /**
     * 四诊程度迁移到四诊分组
     *
     * @return 是否导入成功
     */
    @Transactional
    @GetMapping("/insertDegreeTypeAndInfo")
    public String insertDegreeTypeAndInfo() {
        //症候程度分组导入
        List<OldFourDiagDegree> oldFourDiagDegreeList = oldFourDiagDegreeService.lambdaQuery()
                .eq(OldFourDiagDegree::getDelFlag, 0)
                .list();
        List<FourDiagType> fourDiagDegreeTypeList = new ArrayList<>();
        oldFourDiagDegreeList.forEach(oldFourDiagDegree -> {
            //TODO 如果typeInfoId是要忽略的问诊的小类，不能导入
            if (ignoreFourDiagInfoIds.contains(oldFourDiagDegree.getFourDiagId())) {
                ignoreDegreeTypeIds.add(oldFourDiagDegree.getId());
            } else {
                FourDiagType entity = new FourDiagType();
                // 业务字段
                entity.setId(oldFourDiagDegree.getId());
                entity.setParentId(oldFourDiagDegree.getFourDiagId());
                entity.setDefinition(oldFourDiagDegree.getDegreeName());
                entity.setSort("1");
                // 公共字段
                entity.setCreateTime(oldFourDiagDegree.getCreateTime());
                entity.setCreateUser(oldFourDiagDegree.getCreateUser());
                entity.setUpdateTime(oldFourDiagDegree.getUpdateTime());
                entity.setUpdateUser(oldFourDiagDegree.getUpdateUser());
                entity.setState(oldFourDiagDegree.getState());
                entity.setDelFlag(oldFourDiagDegree.getDelFlag());
                fourDiagDegreeTypeList.add(entity);
            }
        });
        boolean result1 = fourDiagTypeService.saveBatch(fourDiagDegreeTypeList);
        if (result1) {
            System.out.println("症候程度分组迁移至四诊分组成功");
        }

        //症候程度名称导入
        List<OldFourDiagDegreeDetail> oldFourDiagDegreeDetailList = oldFourDiagDegreeDetailService.lambdaQuery()
                .eq(OldFourDiagDegreeDetail::getDelFlag, 0)
                .list();
        List<FourDiagInfo> fourDiagInfoList = new ArrayList<>();
        oldFourDiagDegreeDetailList.forEach(oldFourDiagDegreeDetail -> {
            //TODO 如果在排除的四诊类型里，则不能导入
            if (ignoreDegreeTypeIds.contains(oldFourDiagDegreeDetail.getDegreeId())) {
                ignoreDegreeInfoIds.add(oldFourDiagDegreeDetail.getId());
            } else {
                FourDiagInfo entity = new FourDiagInfo();
                //业务字段
                entity.setId(oldFourDiagDegreeDetail.getId());
                entity.setFourDiagTypeId(oldFourDiagDegreeDetail.getDegreeId());
                entity.setDefinition(oldFourDiagDegreeDetail.getName());
                entity.setSort(oldFourDiagDegreeDetail.getSort());
                //TODO 少一个sex业务字段
                entity.setAnnotations(oldFourDiagDegreeDetail.getNotes());
                // 公共字段
                entity.setCreateTime(oldFourDiagDegreeDetail.getCreateTime());
                entity.setCreateUser(oldFourDiagDegreeDetail.getCreateUser());
                entity.setUpdateTime(oldFourDiagDegreeDetail.getUpdateTime());
                entity.setUpdateUser(oldFourDiagDegreeDetail.getUpdateUser());
                entity.setState(oldFourDiagDegreeDetail.getState());
                entity.setDelFlag(oldFourDiagDegreeDetail.getDelFlag());
                fourDiagInfoList.add(entity);
            }
        });
        boolean result2 = fourDiagInfoService.saveBatch(fourDiagInfoList);
        if (result2) {
            System.out.println("症候程度分组迁移至四诊分组成功");
        }
        System.out.println("排除问诊的程度类型id:" + ignoreDegreeTypeIds);
        System.out.println("排除问诊的程度信息id:" + ignoreDegreeInfoIds);
        return "four_diag_degree_type 迁移到 four_diag_type，four_diag_degree_info 迁移到 four_diag_info成功";

    }
}
