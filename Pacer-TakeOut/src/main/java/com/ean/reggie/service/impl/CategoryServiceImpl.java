package com.ean.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ean.reggie.common.CustomException;
import com.ean.reggie.entity.Category;
import com.ean.reggie.entity.Dish;
import com.ean.reggie.entity.Setmeal;
import com.ean.reggie.mapper.CategoryMapper;
import com.ean.reggie.service.CategoryService;
import com.ean.reggie.service.DishService;
import com.ean.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description:TODO
 * @author:Povlean
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;


    @Override
    public void remove(Long ids) {

        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();

        dishLambdaQueryWrapper.eq(Dish::getCategoryId,ids);
        int count1 = dishService.count(dishLambdaQueryWrapper);

        if(count1 > 0){
            // 已经关联的菜品，抛出一个业务异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();

        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,ids);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);

        if(count2 > 0){
            // 已经关联的套餐，抛出一个异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }
        super.removeById(ids);
    }
}
