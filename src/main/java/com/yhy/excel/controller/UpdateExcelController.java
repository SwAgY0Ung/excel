package com.yhy.excel.controller;


import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 舌象excel数据校验
 * @author Echo
 * @date 2024/6/24 09:00
 */
@Slf4j
@RestController
public class UpdateExcelController {


    @PostMapping("/update")
    public void test(HttpServletResponse response, @RequestParam("file") MultipartFile file) {
        HashMap<String, Integer> strMap = new HashMap<>();
        strMap.put("红舌", 4);
        strMap.put("淡红舌", 5);
        strMap.put("淡白舌", 6);
        strMap.put("紫舌", 7);
        strMap.put("绛舌", 8);
        strMap.put("薄白苔", 9);
        strMap.put("白苔", 10);
        strMap.put("黄苔", 11);
        strMap.put("灰苔", 12);
        strMap.put("黑苔", 13);
        strMap.put("薄苔", 14);
        strMap.put("厚苔", 15);
        strMap.put("少苔", 16);
        strMap.put("无苔", 17);
        strMap.put("腻苔", 18);
        strMap.put("燥苔", 19);


        try {

            InputStream inputStream = file.getInputStream();
            ExcelReader reader = ExcelUtil.getReader(inputStream);
            Workbook workbook = reader.getWorkbook();
            //获取index0，即第一个sheet页
            Sheet sheetAt = workbook.getSheetAt(0);
            //获取总行数
            int lastRowNum = sheetAt.getLastRowNum();
            System.out.println(("文件总行数:{" + lastRowNum + "}"));

            //从第4行开始遍历:从第2列开始,第1列是标题
            for (int i = 3; i < lastRowNum; i++) {

                Row row = sheetAt.getRow(i);
                if (Objects.isNull(row)) continue;

//                //获取第1列数据
//                Cell cell = row.getCell(0);
//                String str = cell.getStringCellValue();
////                str = str.substring(0,4);
//                System.out.println("文本内容:{"+ str +"}");
////                str = str + ".jpg";
//                str = "00" + str;
//                Cell add = row.createCell(0);
//                add.setCellValue(str);

                //获取第2列数据
                Cell cell = row.getCell(1);
                String str = cell.getStringCellValue();

                //去掉最后的.jpg/.png/jpg
                if (str != null && str.endsWith(".jpg")) {
                    str = str.substring(0, str.length() - 4);
                }
                if (str != null && str.endsWith(".png")) {
                    str = str.substring(0, str.length() - 4);
                }
                if (str != null && str.endsWith("jpg")) {
                    str = str.substring(0, str.length() - 3);
                }
                //去掉最后的数字
                str = str.replaceAll("\\d{1,3}$", "");
                //去掉最后可能包含的-
                if (str != null && str.endsWith("-")) {
                    str = str.substring(0, str.length() - 1);
                }
                //去掉最后可能包含的(2)
                String regex = "\\(\\d{1,3}\\)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(str);
                // 如果匹配到括号内容，移除它们
                if (matcher.find()) {
                    str = str.substring(0, matcher.start()) + str.substring(matcher.end());
                }

                //去掉最后可能包含的(2)
                String regex1 = "\\（\\d{1,3}\\)";
                Pattern pattern1 = Pattern.compile(regex1);
                Matcher matcher1 = pattern1.matcher(str);
                // 如果匹配到括号内容，移除它们
                if (matcher1.find()) {
                    str = str.substring(0, matcher1.start()) + str.substring(matcher1.end());
                }

                //使用、进行分割
                String[] split = str.split("、");
                System.out.println("文本内容:{"+ str +"}");

                //初始化变量
                HashMap<Integer, Integer> temp = new HashMap<>();
                String string = null;

                for (String key : split) {
                    //在常量池中获取 词汇 对应的 列值
                    Integer value = strMap.get(key);
                    //如果获取到了
                    if (value != null) {
//                        设置值
                        temp.put(value, 1);
                    } else {
                        //如果没获取到，则添加到未匹配数据中
                        if (string == null) {
                            string = key;
                        } else {
                            string = string + "、" + key;
                        }
                    }
                }

//                修改本列内容
//                cell.setCellValue("设置新的内容");
//                给对应的词汇下面赋值
                if (!temp.isEmpty()) {
                    temp.forEach((k, v) -> {
//                        写入第四列中
                        Cell add = row.createCell(k);
                        add.setCellValue(v);
                    });
                }
//                给未匹配数据赋值
                if (string != null) {
                    Cell add = row.createCell(3);
                    add.setCellValue(string);
                    Cell add1 = row.createCell(2);
                    add1.setCellValue("否");
                }
            }

            //将结果输出

            File outputFile = new File("C:\\Users\\Administrator\\Desktop\\01标注-王帅浩（原文件1609条）V3.xlsx");
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            workbook.write(fileOutputStream);

            // 关闭资源
            workbook.close();
            reader.close();
            inputStream.close();

            log.info("修改excel成功");
//            out.close();

//            //将结果输出
//            ExcelWriter writer = reader.getWriter();
//            ServletOutputStream out = response.getOutputStream();
//            writer.flush(out);
//            out.flush();
//
//            reader.close();
//            inputStream.close();
//            out.close();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

}
