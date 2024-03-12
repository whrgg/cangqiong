package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {


    void saveWithFlavor(DishDTO dishDTO);

    PageResult queryPage(DishPageQueryDTO dishPageQueryDTO);


    void removeBatchId(List<Long> ids);

    DishVO getDishById(Long id);

    void updateById(DishDTO dishDTO);
}