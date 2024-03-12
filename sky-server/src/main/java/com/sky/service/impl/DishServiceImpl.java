package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.setmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    DishMapper dishMapper;
    @Autowired
    DishFlavorMapper dishFlavorMapper;
    @Autowired
    CategoryMapper categoryMapper;
    @Autowired
    setmealDishMapper setmealDishMapper;

    /**
     * 新增菜品和对应的口味
     * @param dishDTO
     */

    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish =new Dish();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.insert(dish);

        Long dishId=dish.getId();

        if(flavors!=null&&flavors.size()>0){
            flavors.forEach(item->{
                item.setDishId(dishId);
            });
            dishFlavorMapper.insertBatch(flavors);
        }

    }

    @Override
    @Transactional
    public PageResult queryPage(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> dishes = dishMapper.queryPage(dishPageQueryDTO);

        PageResult pageResult = new PageResult();
        pageResult.setRecords(dishes.getResult());
        pageResult.setTotal(dishes.getTotal());


        return pageResult;
    }

    @Override
    public void removeBatchId(List<Long> ids) {

        for (Long id : ids){
            Dish dish = dishMapper.getById(id);
            if(dish.getStatus()== StatusConstant.ENABLE){
                //如果处于正在售卖状态抛出异常
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //判断是否和套餐关联

        List<Long> setmealIds=setmealDishMapper.getSetmealIdsByDishIds(ids);
        if(setmealIds!=null&&setmealIds.size()>0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }


        //删除菜品id
        dishMapper.removeBatchIds(ids);

        //删除口味数据
        for (Long id : ids) {
            dishFlavorMapper.removeByDishId(id);
        }

    }

    @Override
    public DishVO getDishById(Long id) {

        DishVO vo =new DishVO();

        Dish dish = dishMapper.getById(id);

        BeanUtils.copyProperties(dish,vo);

        List<DishFlavor> dishFlavor = dishFlavorMapper.getByDishId(vo.getId());

        vo.setFlavors(dishFlavor);

        return vo;
    }

    @Override
    public void updateById(DishDTO dishDTO) {
        Dish dish =new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.updateById(dish);

        dishFlavorMapper.removeByDishId(dishDTO.getId());
        List<DishFlavor> flavors = dishDTO.getFlavors();

        if(flavors!=null&&flavors.size()>0){
            flavors.forEach(item->{
                item.setDishId(dishDTO.getId());
            });
            dishFlavorMapper.insertBatch(flavors);
        }

    }
}
