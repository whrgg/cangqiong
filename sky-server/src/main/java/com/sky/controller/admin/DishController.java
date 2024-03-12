package com.sky.controller.admin;

import com.github.pagehelper.Page;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
public class DishController {

    @Autowired
    DishService service;



    @PostMapping
    public Result<String> save(@RequestBody DishDTO dishDTO){

        service.saveWithFlavor(dishDTO);

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

        return Result.success();
    }

}
