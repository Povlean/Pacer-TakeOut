package com.ean.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ean.reggie.common.Result;
import com.ean.reggie.dto.SetmealDto;
import com.ean.reggie.entity.Category;
import com.ean.reggie.entity.Setmeal;
import com.ean.reggie.service.CategoryService;
import com.ean.reggie.service.SetmealDishService;
import com.ean.reggie.service.SetmealService;
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
@RequestMapping("/setmeal")
@RestController
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public Result<String> save(@RequestBody SetmealDto setmealDto){
        log.info("套餐信息: {}",setmealDto);

        setmealService.saveWithDishes(setmealDto);

        return Result.success("新增套餐成功");
    }

    @GetMapping("/page")
    public Result<Page> page(int page,int pageSize,String name){
        // 分页构造器对象
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> dtoPage = new Page<>();
        // 添加lambda条件
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null,Setmeal::getName,name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo,queryWrapper);
        // 对象拷贝
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list = records.stream().map((item) -> {
            // new 对象，用于封装
            SetmealDto setmealDto = new SetmealDto();
            // 对象拷贝，将item中的值拷贝到dto，最后返回dto对象
            BeanUtils.copyProperties(item,setmealDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                // 在records中获取category的属性值
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(list);
        return Result.success(dtoPage);
    }

    @DeleteMapping()
    public Result<String> delete(@RequestParam List<Long> ids){
        log.info("删除数据为: {}",ids);

        setmealService.deleteWithDishes(ids);

        return Result.success("删除数据成功");
    }

    @GetMapping("/{id}")
    public Result<SetmealDto> getSetmealDishse(@PathVariable Long id){

        SetmealDto setmealDto = setmealService.getByIdWithdishes(id);

        return Result.success(setmealDto);
    }

    @PutMapping
    public Result<SetmealDto> update(@RequestBody SetmealDto setmealDto){

        setmealService.updateWithDishes(setmealDto);

        return Result.success(setmealDto);
    }

    @PostMapping("/status/{status}")
    public Result<String> updateSale(@PathVariable Integer status,Long[] ids){
        for(Long id : ids){
            Setmeal setmeal = setmealService.getById(id);
            setmeal.setStatus(status);
            setmealService.updateById(setmeal);
        }
        return Result.success("修改成功");
    }

}
