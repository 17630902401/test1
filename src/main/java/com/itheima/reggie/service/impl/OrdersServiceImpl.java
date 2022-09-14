package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.OrdersQueryDTO;
import com.itheima.reggie.pojo.*;
import com.itheima.reggie.mapper.OrdersMapper;
import com.itheima.reggie.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author 黑马程序员
 * @since 2022-09-06
 */
@Service
@Slf4j
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;
    @Override
    @Transactional
    public void submit(Orders orders) {
        //获取当前用户ID
        Long userId = BaseContext.getCurrentId();
        //查询当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(qw);
        //使用Spring提供的CollectionUtils工具类
        if(CollectionUtils.isEmpty(shoppingCarts)){
            throw new CustomException("购物车为空，不能下单");
        }
        //查询用户数据：头像，昵称，手机号
        User user = userService.getById(userId);

        //查询地址数据
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if(addressBook == null){
            throw new CustomException("用户地址信息有误，不能下单");
        }

        //生成订单号：雪花算法
        long orderId = IdWorker.getId();

        //3.向订单表插入数据，一条数据
        //定义订单总金额
        AtomicInteger amount = new AtomicInteger(0);

        //遍历购物车：计算总金额&组装订单明细数据
        //1、将购物车中的数据转换成订单详情数据
        //2、计算购物车中所有商品的总金额
        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);

            //只要属性名称（number,name）保持一致就能拷贝，和对象的类型无关
            BeanUtils.copyProperties(item, orderDetail);

            //订单总金额 += 单价*数量
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(userId);
        //订单编号：20221215000001
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress(addressBook.getDetail());
        this.save(orders); //insert orders
        //4.向订单明细表插入数据，多条数据 insert order_detail
        orderDetailService.saveBatch(orderDetails);

        //5.清空购物车数据
        shoppingCartService.remove(qw);

    }

    @Override
    @Transactional
    public Page<Orders> page(OrdersQueryDTO queryDTO, int page, int pageSize) {
        log.info("OrdersQueryDTO: {}, page：{}, pageSize：{}", queryDTO, page, pageSize);
        //构造分页构造器
        Page<Orders> pageInfo = new Page<>(page, pageSize);

        //构建查询条件
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();

        //用户ID不为空，则查询指定用户的ID：user_id = ?
        wrapper.eq(queryDTO.getUserId() != null,
                Orders::getUserId, queryDTO.getUserId());

        //订单ID不为空，进行模糊查询
        wrapper.like(queryDTO.getNumber() != null,
                Orders::getNumber, queryDTO.getNumber());

        //开始时间不为空，则添加：where order_time >= beginTime
        // greater or equal
        wrapper.ge(queryDTO.getBeginTime() != null,
                Orders::getOrderTime, queryDTO.getBeginTime());

        //结束时间不为空，则添加：where order_time <= endTime
        //less than or equal
        wrapper.le(queryDTO.getEndTime() != null,
                Orders::getOrderTime, queryDTO.getEndTime());

        //排序
        wrapper.orderByDesc(Orders::getOrderTime);

        //1.查询当前登录用户订单列表
        this.page(pageInfo, wrapper);

        List<Orders> collect = pageInfo.getRecords().stream().map(item -> {
            //2.查询当前订单的详情：（菜品or套餐）
            List<OrderDetail> details =
                    orderDetailService.list(
                            Wrappers.<OrderDetail>lambdaQuery()
                                    .eq(OrderDetail::getOrderId, item.getId()));
            item.setOrderDetails(details);
            return item;
        }).collect(Collectors.toList());
        pageInfo.setRecords(collect);
        return pageInfo;
    }
}
