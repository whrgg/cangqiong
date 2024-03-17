package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
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

        List<LocalDate> dateLists =new ArrayList<>();

        dateLists.add(begin);
        while(!begin.equals(end)){
            begin=begin.plusDays(1);
            dateLists.add(begin);
        }

        List<Double> turnoverList=new ArrayList<>();
        for (LocalDate dateList : dateLists) {
            LocalDateTime beginTime =  LocalDateTime.of(dateList, LocalTime.MIN);
            LocalDateTime endTime =  LocalDateTime.of(dateList,LocalTime.MAX);

            Map map =new HashMap<>();
            map.put("begin",beginTime);
            map.put("end",endTime);
            map.put("status", Orders.COMPLETED);

           Double turnover = orderMapper.sumByMap(map);
            turnover =turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }

        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(dateLists,","))
                .turnoverList(StringUtils.join(turnoverList,","))
                .build();
    }

    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateLists =new ArrayList<>();


        dateLists.add(begin);
        while(!begin.equals(end)){
            begin=begin.plusDays(1);
            dateLists.add(begin);
        }

        List<Integer> allUsers= new ArrayList<>();
        List<Integer> newUsers =new ArrayList<>();

        for (LocalDate date : dateLists) {
            LocalDateTime beginTime = LocalDateTime.of(date,LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date,LocalTime.MAX);
            Map map1 =new HashMap<>();
            map1.put("begin",beginTime);
            map1.put("end",endTime);

            Map map =new HashMap();
            map1.put("end",endTime);
            Integer allUser = orderMapper.selectUser(map);
            Integer newUser = orderMapper.selectUser(map1);
            allUsers.add(allUser);
            newUsers.add(newUser=newUser==null?0:newUser);
        }




        return UserReportVO.builder()
                .dateList(StringUtils.join(dateLists,","))
                .totalUserList(StringUtils.join(allUsers,","))
                .newUserList(StringUtils.join(newUsers,","))
                .build();
    }
}
