package com.ean.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ean.reggie.dto.DishDto;
import com.ean.reggie.dto.SetmealDto;
import com.ean.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    public void saveWithDishes(SetmealDto setmealDto);
    public void deleteWithDishes(List<Long> ids);
    public SetmealDto getByIdWithdishes(Long id);
    public void updateWithDishes(SetmealDto setmealDto);
}
