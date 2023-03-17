package com.ean.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ean.reggie.common.CustomException;
import com.ean.reggie.dto.SetmealDto;
import com.ean.reggie.entity.Setmeal;
import com.ean.reggie.entity.SetmealDish;
import com.ean.reggie.mapper.SetmealMapper;
import com.ean.reggie.service.SetmealDishService;
import com.ean.reggie.service.SetmealService;
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
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService{
    /*
        保存菜品功能
     */
    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    @Transactional
    public void saveWithDishes(SetmealDto setmealDto) {
        this.save(setmealDto);
        // 获取套餐中的菜品信息
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    public void deleteWithDishes(List<Long> ids) {
        // 构造sql条件
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        // 统计记录条数
        int count = this.count(queryWrapper);
        if(count > 0){
            // 如果满足综上sql条件，则不可删除。
            throw new CustomException("该套餐正在售卖中，删除失败。");
        }
        // 如果可以删除，则先删除套餐表中的数据
        this.removeByIds(ids);

        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(SetmealDish::getDishId,ids);
        setmealDishService.remove(queryWrapper1);
    }

    @Override
    public SetmealDto getByIdWithdishes(Long id) {
        // 从套餐表中查到套餐的基础信息。
        Setmeal setmeal = this.getById(id);
        // 准备Dto对象，将setmeal中的属性传给Dto中
        SetmealDto setmealDto = new SetmealDto();
        // 复制属性
        BeanUtils.copyProperties(setmeal,setmealDto);
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        // 使用list查询，返回dish值
        List<SetmealDish> dishes = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(dishes);
        return setmealDto;
    }

    @Override
    @Transactional
    public void updateWithDishes(SetmealDto setmealDto) {
        // 只更新了Dishes中的表数据
        this.updateById(setmealDto);
        // 删除当前菜品表中的口味信息
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);
        // 在套餐信息中插入菜品
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }

}
