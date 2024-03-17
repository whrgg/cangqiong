package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {


    List<Long> getSetmealIdsByDishIds(List<Long> ids);

    @Insert("insert into setmeal_dish (setmeal_id, dish_id, name, price, copies) VALUES (#{setmealId},#{dishId},#{name},#{price},#{copies})")
    void save(SetmealDish setmealDish);


    //通过setmeal的Id获取在setmealdish中的Dish的状态
    @Select("select a.* from dish a left join setmeal_dish b on a.id = b.dish_id where b.setmeal_id = #{id}")
    List<Dish> getDishBySetmealId(Long id);

    void deleteByIds(List<Long> ids);

    @Select("select * from setmeal_dish where setmeal_id=#{id}")
    List<SetmealDish> getSetmealDishsById(Long id);
}

