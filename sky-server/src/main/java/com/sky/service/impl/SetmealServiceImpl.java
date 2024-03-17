package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 套餐业务实现
 */
@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }

    @Override
    @Transactional
    public void save(SetmealDTO setmealDTO) {
        Setmeal setmeal =new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);

        setmealMapper.save(setmeal);

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmeal.getId());
            setmealDishMapper.save(setmealDish);
        }

    }

    @Override
    public PageResult pageQuery(SetmealPageQueryDTO queryDTO) {

        PageHelper.startPage(queryDTO.getPage(),queryDTO.getPageSize());
        Page<SetmealVO> page =setmealMapper.page(queryDTO);

        return new PageResult(page.getTotal(),page.getResult());
    }


    @Override
    public void updateStatus(Integer status, Long id) {
        if(status == StatusConstant.ENABLE){
            //如果要修改为上线的状态，要先判断套餐里所有的菜都是售卖的状态
            List<Dish> dishs = setmealDishMapper.getDishBySetmealId(id);

            if(dishs != null && dishs.size() > 0){
                dishs.forEach(dish -> {
                    if(StatusConstant.DISABLE == dish.getStatus()){
                        throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                    }
                });
            }

        }

        Setmeal setmeal =Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setmealMapper.update(setmeal);
    }

    @Override
    @Transactional
    public void delete(List<Long> ids) {

        ids.forEach(id->{
            Setmeal setmeal = setmealMapper.getById(id);
            if(setmeal.getStatus()==StatusConstant.DISABLE){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }

        });

        setmealMapper.deleteByIds(ids);
        setmealDishMapper.deleteByIds(ids);
    }

    @Override
    @Transactional
    public SetmealVO getSetmealVoById(Long id) {

        Setmeal setmeal = setmealMapper.getById(id);
        List<SetmealDish> setmealDishs = setmealDishMapper.getSetmealDishsById(id);

        SetmealVO setmealVO =new SetmealVO();
        BeanUtils.copyProperties(setmeal,setmealVO);
        setmealVO.setSetmealDishes(setmealDishs);


        return setmealVO;
    }

    @Override
    @Transactional
    public void update(SetmealDTO setmealDTO) {

        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.update(setmeal);

        setmealDishMapper.deleteByIds(List.of(setmealDTO.getId()));

        for (SetmealDish setmealDish : setmealDTO.getSetmealDishes()) {
            setmealDish.setSetmealId(setmealDTO.getId());
            setmealDishMapper.save(setmealDish);
        }

        return ;
    }


}
