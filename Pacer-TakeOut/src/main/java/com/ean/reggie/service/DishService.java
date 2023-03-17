package com.ean.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ean.reggie.dto.DishDto;
import com.ean.reggie.entity.Dish;

public interface DishService extends IService<Dish> {
    public void saveWithFlavor(DishDto dishDto);

    public DishDto getByIdWithFlavor(Long id);

    public void updateWithFlavor(DishDto dishDto);
}
