package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.pojo.Employee;

import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
/**
 * <p>
 * 员工信息 前端控制器
 * </p>
 *
 * @author 黑马程序员
 * @since 2022-09-06
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 登录功能
     *
     * @param session
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpSession session, @RequestBody Employee employee) {

        //1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //简化写法
        //Employee dbEmployee = employeeService.getOne(
        //        Wrappers.<Employee>lambdaQuery() //new LambdaQueryWrapper<>();
        //                .eq(Employee::getUsername,employee.getUsername()));

        //3、如果没有查询到则返回登录失败结果
        if(emp == null){
            return R.error("登录失败");
        }

        //4、密码比对，如果不一致则返回登录失败结果
        if(!emp.getPassword().equals(password)){
            return R.error("登录失败");
        }

        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if(emp.getStatus() == 0){
            return R.error("账号已禁用");
        }

        //注意：此处必须使用数据库查询出来的emp对象，不能使用前端获取到的employee(里边没有员工ID)
        //6、登录成功，将员工id存入Session并返回登录成功结果
        session.setAttribute("employee",emp.getId());

        //注意：此处必须使用数据库查询出来的emp对象，不能使用前端获取到的employee(里边没有员工ID)
        return R.success(emp);
    }

    /**
     * 退出功能
     *
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpSession session) {
        //不管Session域中存储什么信息,将其清理
        //清理Session中保存的当前登录员工的id
        session.removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<Employee>> page(int page, int pageSize, String name) {
        log.info("page:{}, pageSize:{}, name:{}", page, pageSize, name);
        //构造分页构造器
        Page<Employee> pageInfo = new Page<>(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> qw = new LambdaQueryWrapper<>();
        qw.like(StringUtils.isNotBlank(name),Employee::getName,name);
        //添加排序条件: desc(倒序） order by update_time desc;
        qw.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo,qw);
        return R.success(pageInfo);
    }

    /**
     * 添加员工信息功能
     * @param session
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpSession session,@RequestBody Employee employee) {
        // log.info("{}",employee);
        // A. 在新增员工时， 按页面原型中的需求描述， 需要给员工设置初始默认密码 123456， 并对密码进行MD5加密。
        //初始化密码：123456
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        // B. 在组装员工信息时, 还需要封装创建时间、修改时间，创建人(id信息)、修改人(id)信息(从session中获取当前登录用户)。
//        employee.setCreateTime(LocalDateTime.now());//获取系统当前时间
//        employee.setUpdateTime(LocalDateTime.now());
//        //从session中获取当前登录用户的id信息
//        Long empId = (Long) session.getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);
        //添加功能
        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    /**
     * 根据ID修改员工信息
     * @param session
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpSession session,@RequestBody Employee employee){
        log.info(employee.toString());
        //获取当前线程ID
        long id = Thread.currentThread().getId();
        log.info("线程id为：{}",id);
//        long empId = (long) session.getAttribute("employee");
//        employee.setUpdateUser(empId);
//        employee.setUpdateTime(LocalDateTime.now());

        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    /**
     * 修改时数据回显
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable long id){
        log.info("根据id查询员工信息");
        Employee employee = employeeService.getById(id);
        if (employee != null){
            return R.success(employee);
        }
        return R.error("没有查询到对应员工信息");
    }





}

