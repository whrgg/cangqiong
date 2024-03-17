package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import io.swagger.models.auth.In;

import java.util.List;

public interface SetmealService {

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    List<DishItemVO> getDishItemById(Long id);

    void save(SetmealDTO setmealDTO);

    PageResult pageQuery(SetmealPageQueryDTO queryDTO);


    void updateStatus(Integer status,Long id);

    void delete(List<Long> ids);

    SetmealVO getSetmealVoById(Long id);

    void update(SetmealDTO setmealDTO);
}
