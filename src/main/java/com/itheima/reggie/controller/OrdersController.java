package com.itheima.reggie.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.OrdersQueryDTO;
import com.itheima.reggie.pojo.Orders;
import com.itheima.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 订单表 前端控制器
 * </p>
 *
 * @author 黑马程序员
 * @since 2022-09-06
 */
@RestController
@RequestMapping("/order")
@Slf4j
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单数据：{}",orders);
        ordersService.submit(orders);
        return R.success("下单成功");
    }

    /**
     * 用户付完钱后---查看用户订单功能 ------
     * http://localhost:8080/front/page/order.html
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page<Orders>> userPage(int page, int pageSize) {
        OrdersQueryDTO queryDTO = new OrdersQueryDTO();
        //查询当前登录用户订单列表
        queryDTO.setUserId(BaseContext.getCurrentId());
        return R.success(ordersService.page(queryDTO, page, pageSize));
    }

    /**
     * 后台查询订单列表
     * @param queryDTO
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page<Orders>> page(OrdersQueryDTO queryDTO, int page, int pageSize) {
        return R.success(ordersService.page(queryDTO, page, pageSize));
    }
}

