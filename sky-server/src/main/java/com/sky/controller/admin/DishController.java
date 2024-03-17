package com.sky.controller.admin;

import com.github.pagehelper.Page;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.exception.UserNotLoginException;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
public class DishController {

    @Autowired
    DishService service;
    @Autowired
    RedisTemplate redisTemplate;


    @PostMapping
    public Result<String> save(@RequestBody DishDTO dishDTO){

        service.saveWithFlavor(dishDTO);
        Set keys = redisTemplate.keys("dish_*");

        redisTemplate.delete(keys);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){

        PageResult pageResult = service.queryPage(dishPageQueryDTO);

        return Result.success(pageResult);
    }

    @DeleteMapping
    public Result<String> del(@RequestParam List<Long> ids){

        service.removeBatchId(ids);
        Set keys = redisTemplate.keys("dish_*");

        redisTemplate.delete(keys);

        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<DishVO> get(@PathVariable Long id){


        DishVO dishVO = service.getDishById(id);


        return Result.success(dishVO);
    }


    @PutMapping
    public Result<String> update(@RequestBody DishDTO dishDTO){

        service.updateById(dishDTO);
        Set keys = redisTemplate.keys("dish_*");

        redisTemplate.delete(keys);

        return Result.success();
    }


    @PostMapping("/status/{status}")
    public Result<String> updateStatus(Long id,@PathVariable Integer status){

        DishDTO dishDTO =new DishDTO();
        dishDTO.setStatus(status);
        dishDTO.setId(id);

        service.updateById(dishDTO);

        Set keys = redisTemplate.keys("dish_*");

        redisTemplate.delete(keys);

        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<Dish>> listDish(Integer categoryId){


        return Result.success(service.getDishsByCategoryId(categoryId));
    }

}
