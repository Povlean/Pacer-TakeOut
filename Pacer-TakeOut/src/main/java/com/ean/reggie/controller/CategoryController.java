package com.ean.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ean.reggie.common.Result;
import com.ean.reggie.entity.Category;
import com.ean.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description:TODO
 * @author:Povlean
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService service;

    @PostMapping
    public Result<String> save(@RequestBody Category category){
        log.info(category.toString());
        service.save(category);
        return Result.success("添加成功");
    }

    @GetMapping("/page")
    public Result<Page> page(int page,int pageSize){
        // 创建页面信息
        Page<Category> pageInfo = new Page<>(page,pageSize);
        // 创建lambda条件语句
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        // 添加升序排序的条件
        queryWrapper.orderByAsc(Category::getSort);
        // 调用MP封装好的page方法
        service.page(pageInfo,queryWrapper);
        return Result.success(pageInfo);
    }

    @DeleteMapping
    public Result<String> delete(Long ids){
        log.info("删除分类 {}",ids);
        // service.removeById(ids);
        service.remove(ids);
        return Result.success("删除成功");
    }

    @PutMapping
    public Result<String> update(@RequestBody Category category){
        log.info("更新菜单为 {}",category);
        // 传入实体参数
        service.updateById(category);
        return Result.success("菜单更新成功");
    }

    @GetMapping("/list")
    public Result<List<Category>> list(Category category){
        // 创建Lambda条件表达式
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        // 添加映射条件
        queryWrapper.eq(category.getType() != null,Category::getType,category.getType());
        // 添加升序和降序条件
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        // 调用MP中封装好的Mapper.list方法
        List<Category> list = service.list(queryWrapper);
        // 返回list数据
        return Result.success(list);
    }

}
