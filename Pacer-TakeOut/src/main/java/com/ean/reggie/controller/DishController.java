package com.ean.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ean.reggie.common.Result;
import com.ean.reggie.dto.DishDto;
import com.ean.reggie.entity.Category;
import com.ean.reggie.entity.Dish;
import com.ean.reggie.service.CategoryService;
import com.ean.reggie.service.DishFlavorService;
import com.ean.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @description:TODO
 * @author:Povlean
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public Result<String> save(@RequestBody DishDto dishDto){
        // 记录日志
        log.info(dishDto.toString());
        // 调用封装的方法，将flavor传到dish表单中
        dishService.saveWithFlavor(dishDto);
        // 返回json结果
        return Result.success("新增菜品成功");
    }

    @GetMapping("/page")
    public Result page(int page,int pageSize,String name){
        // 创建页面对象
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        // 创建Lambda表达式
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        // 添加查询条件
        queryWrapper.like(name != null,Dish::getName,name);
        // 添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        // 调用page()方法，发送页面对象和sql条件
        dishService.page(pageInfo,queryWrapper);
        // 拷贝对象
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();
            // 根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return Result.success(dishDtoPage);
    }

    @GetMapping("/{id}")
    public Result<DishDto> getDishFlavor(@PathVariable Long id){

        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return Result.success(dishDto);
    }

    @PutMapping
    public Result<String> update(@RequestBody DishDto dishDto){

        dishService.updateWithFlavor(dishDto);

        return Result.success("修改菜品成功");
    }

    @GetMapping("/list")
    //DQL sql语句
    public Result<List<Dish>> list(Dish dish){
        // 构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId() );
        // 添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        // 添加状态查询条件
        queryWrapper.eq(Dish::getStatus,1);
        List<Dish> dishes = dishService.list(queryWrapper);
        return Result.success(dishes);
    }

    @PostMapping("/status/{status}")
    public Result<String> updateStatus(@PathVariable Integer status,Long[] ids){
        log.info("查看菜品状态 {}，id为 {}",status,ids);
        for (Long id : ids) {
            Dish dish = dishService.getById(id);
            dish.setStatus(status);
            dishService.updateById(dish);
        }
        return Result.success("修改成功");
    }

}
