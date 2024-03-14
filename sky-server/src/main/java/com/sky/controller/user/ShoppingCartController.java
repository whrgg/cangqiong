package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
public class ShoppingCartController {


    @Autowired
    private ShoppingCartService service;

    @PostMapping("/add")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO){

        service.addShoppingCart(shoppingCartDTO);

        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<ShoppingCart>> list(){
        List<ShoppingCart> list =service.showShoppingCart();
        return Result.success(list);
    }
    @DeleteMapping("/clean")
    public Result delete(@RequestBody ShoppingCartDTO shoppingCartDTO){

        service.cleanShoppingCart();
        return Result.success();
    }

}
