package com.ean.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ean.reggie.dto.DishDto;
import com.ean.reggie.entity.Dish;
import com.ean.reggie.entity.DishFlavor;
import com.ean.reggie.mapper.DishMapper;
import com.ean.reggie.service.DishFlavorService;
import com.ean.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @description:TODO
 * @author:Povlean
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Override
    public void saveWithFlavor(DishDto dishDto) {
        // 保存菜品的基本信息到菜品表dish
        this.save(dishDto);
        // 获取菜品id
        Long dishId = dishDto.getId();
        // 菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map( (item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        // 获取菜品基本信息
        // 从dish表中查出数据，并封装
        Dish dish = this.getById(id);
        // 创建Dto对象
        DishDto dishDto = new DishDto();
        // 将dish的属性值封装到dishDto中
        BeanUtils.copyProperties(dish,dishDto);
        // 创建Lambda条件语句
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        // flavors -> ['微辣','中辣','超辣','变态辣']
        // 所以需要List集合接收
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    @Override
    // 添加事务标签，代表该类为事务类。
    // 在代码出错时，能够执行事务回滚。
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        // 更新dish表中的信息
        this.updateById(dishDto);
        // 删除当前菜品表中的口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        // 在口味信息中插入数据
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map( (item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

}
