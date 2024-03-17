package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealOverViewVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
public class SetmealController {

    @Autowired
    SetmealService setmealService;



    @PostMapping
    public Result<String> saveSetmeal(@RequestBody SetmealDTO setmealDTO){
        setmealService.save(setmealDTO);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult> getPage(SetmealPageQueryDTO queryDTO){


        return Result.success(setmealService.pageQuery(queryDTO));
    }

    @PostMapping("/status/{status}")
    public Result<String> updateStatus(@PathVariable Integer status,Long id){

        setmealService.updateStatus(status,id);

        return Result.success();
    }

    @DeleteMapping
    public Result<String> delete(@RequestParam List<Long> ids){

        setmealService.delete(ids);

        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<SetmealVO> getById(@PathVariable Long id){

        SetmealVO setmealVo = setmealService.getSetmealVoById(id);

        return Result.success(setmealVo);
    }

    @PutMapping
    public Result update(@RequestBody SetmealDTO setmealDTO){

        setmealService.update(setmealDTO);

        return null;
    }

}
