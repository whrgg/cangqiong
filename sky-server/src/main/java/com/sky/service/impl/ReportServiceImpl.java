package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    WorkspaceService workspaceService;
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {

        List<LocalDate> dateLists = new ArrayList<>();

        dateLists.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateLists.add(begin);
        }

        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate data : dateLists) {
            LocalDateTime beginTime = LocalDateTime.of(data, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(data, LocalTime.MAX);

            Map map = new HashMap<>();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);

            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }

        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(dateLists, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateLists = new ArrayList<>();


        dateLists.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateLists.add(begin);
        }

        List<Integer> allUsers = new ArrayList<>();
        List<Integer> newUsers = new ArrayList<>();

        for (LocalDate date : dateLists) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map1 = new HashMap<>();
            map1.put("begin", beginTime);
            map1.put("end", endTime);

            Map map = new HashMap();
            map1.put("end", endTime);
            Integer allUser = orderMapper.selectUser(map);
            Integer newUser = orderMapper.selectUser(map1);
            allUsers.add(allUser);
            newUsers.add(newUser = newUser == null ? 0 : newUser);
        }


        return UserReportVO.builder()
                .dateList(StringUtils.join(dateLists, ","))
                .totalUserList(StringUtils.join(allUsers, ","))
                .newUserList(StringUtils.join(newUsers, ","))
                .build();
    }

    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateLists = new ArrayList<>();
        List<Integer> orderAll = new ArrayList<>();
        List<Integer> orderComps = new ArrayList<>();

        dateLists.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateLists.add(begin);
        }

        for (LocalDate data : dateLists) {
            LocalDateTime beginTime = LocalDateTime.of(data, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(data, LocalTime.MAX);
            //该天的所有订单
            Integer orderCount = getOrderCount(beginTime, endTime, null);
            orderAll.add(orderCount=orderCount==null?0:orderCount);
            //该天所有完成的订单
            Integer orderCompCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);
            orderComps.add( orderCompCount=orderCompCount==null?0:orderCompCount);
        }

        Integer totalOrderCount = orderAll.stream().reduce(Integer::sum).get();
        Integer ComOrderCount = orderComps.stream().reduce(Integer::sum).get();
        Double orderCompletionRate=0.0;
        if(totalOrderCount != 0){
            orderCompletionRate = ComOrderCount.doubleValue() / totalOrderCount;
        }


        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateLists, ","))
                .orderCountList(StringUtils.join(orderAll, ","))
                .validOrderCountList(StringUtils.join(orderComps, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(ComOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }


    /**
     * 获取前十的热销产品
     * @return
     */
    @Override
    public SalesTop10ReportVO getSales10Report(LocalDate begin, LocalDate end) {

        List<LocalDate> dateLists = new ArrayList<>();
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> top10 = orderMapper.getTop10(beginTime, endTime);

        String nameList = StringUtils.join(top10.stream().map(item -> {
            return item.getName();
        }).collect(Collectors.toList()), ",");

        String numberList = StringUtils.join(top10.stream()
                .map(GoodsSalesDTO::getNumber)
                .collect(Collectors.toList()), ",");


        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }

    @Override
    public void exportBusinessData(HttpServletResponse response) {
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);

        BusinessDataVO businessData = workspaceService
                .getBusinessData(LocalDateTime.of(begin,LocalTime.MIN), LocalDateTime.of(end, LocalTime.MAX));
        InputStream inputStream = this
                .getClass().getClassLoader()
                .getResourceAsStream("template/运营数据报表模板.xlsx");
        try {
            //基于提供好的模板文件创建一个新的Excel表格对象
            XSSFWorkbook excel = new XSSFWorkbook(inputStream);
            //获得Excel文件中的一个Sheet页
            XSSFSheet sheet = excel.getSheet("Sheet1");

            sheet.getRow(1).getCell(1).setCellValue(begin + "至" + end);
            //获得第4行
            XSSFRow row = sheet.getRow(3);
            //获取单元格
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessData.getNewUsers());
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessData.getValidOrderCount());
            row.getCell(4).setCellValue(businessData.getUnitPrice());
            for (int i = 0; i < 30; i++) {
                LocalDate date = begin.plusDays(i);
                //准备明细数据
                businessData = workspaceService.getBusinessData(LocalDateTime.of(date,LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());
            }
            //通过输出流将文件下载到客户端浏览器中
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);
            //关闭资源
            out.flush();
            out.close();
            excel.close();

        }catch (IOException e){

            e.printStackTrace();
        }


    }

    private Integer getOrderCount(LocalDateTime beginTime, LocalDateTime endTime, Integer status) {

        Map map = new HashMap();
        map.put("begin", beginTime);
        map.put("end", endTime);
        map.put("status", status);


        return orderMapper.orderCount(map);
    }
}
