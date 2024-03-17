package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.models.auth.In;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    OrderMapper orderMapper;

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

    private Integer getOrderCount(LocalDateTime beginTime, LocalDateTime endTime, Integer status) {

        Map map = new HashMap();
        map.put("begin", beginTime);
        map.put("end", endTime);
        map.put("status", status);


        return orderMapper.orderCount(map);
    }
}
