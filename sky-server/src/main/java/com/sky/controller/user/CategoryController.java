package com.sky.controller.user;

import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController("userCategoryController")
@RequestMapping("/user/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查询分类
     * @param type
     * @return
     */
    @GetMapping("/list")
    public Result<List<Category>> list(Integer type) {



        List<Category> list = (List<Category>) redisTemplate.opsForValue().get("category_" + type);
        if(list !=null&& list.size()>0){
            return Result.success(list);
        }


        list = categoryService.list(type);
        redisTemplate.opsForValue().set("category_" + type,list);

        return Result.success(list);
    }
}
