package com.ean.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ean.reggie.common.Result;
import com.ean.reggie.entity.Employee;
import com.ean.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;


@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService service;

    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request){
        // 获取网页中的session数据并删除
        HttpSession session = request.getSession();
        session.removeAttribute("employee");
        // 反馈登出成功的结果信息
        return Result.success("退出成功");
    }

    @PostMapping("/login")
    public Result<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        // String password1 = request.getParameter("password");
        // 将页面提交的密码进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        // 根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Employee::getUsername,employee.getUsername());
        Employee emp = service.getOne(lqw);
        // 判断用户是否存在
        if(emp == null){
            return Result.error("用户不存在，登录失败");
        }

        if(!emp.getPassword().equals(password)){
            return Result.error("密码错误，登录失败");
        }

        if(emp.getStatus() == 0){
            return Result.error("员工状态被禁用，登录失败");
        }

        // 获取session,将数据存入session
        HttpSession session = request.getSession();
        session.setAttribute("employee",emp.getId());

        // 返回结果
        return Result.success(emp);

    }

    @PostMapping
    public Result<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("员工信息为：{}",employee.toString());
        // 在业务层设置表单中没有的数据
        // 设置加密密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        // 设置表单中没有的数据
        /*employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        // 获取创建人的id，需要从request请求中获取数据
        Long empId = (Long) request.getSession().getAttribute("employee");
        // 在新员工的数据上设置创建人的id
        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);*/

        // 该接口的save()方法来自MP的IService接口
        service.save(employee);
        return Result.success("新增员工成功!");
    }

    @GetMapping("/page")
    public Result<Page> page(int page,int pageSize,String name){
        log.info("page = {},pageSize = {},name = {}",page,pageSize,name);
        Page pageInfo = new Page(page,pageSize);
        // 构造条件构造器
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        // 添加条件过滤
        lqw.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        // 添加排序条件
        lqw.orderByDesc(Employee::getUpdateTime);
        // 执行条件
        service.page(pageInfo, lqw);
        return Result.success(pageInfo);
    }

    @PutMapping
    public Result update(@RequestBody Employee employee){
        log.info(employee.toString());

        /*Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setUpdateUser(empId);
        employee.setUpdateTime(LocalDateTime.now());*/
        service.updateById(employee);

        return Result.success("修改成功");
    }

    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id){
        Employee emp = service.getById(id);
        if(emp != null){
            return Result.success(emp);
        }
        return Result.error("修改失败");
    }

}
