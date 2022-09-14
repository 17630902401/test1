package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.dto.OrdersQueryDTO;
import com.itheima.reggie.pojo.Orders;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author 黑马程序员
 * @since 2022-09-06
 */
public interface OrdersService extends IService<Orders> {

    /**
     * 用户下单
     * @param orders
     */
    public void submit(Orders orders);

    /**
     * 根据条件分页查询订单信息
     *
     * @param queryDTO 查询条件
     * @param page
     * @param pageSize
     * @return
     */
    Page<Orders> page(OrdersQueryDTO queryDTO, int page, int pageSize);
}
