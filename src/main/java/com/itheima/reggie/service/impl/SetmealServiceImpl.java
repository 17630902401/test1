package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.pojo.Setmeal;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.pojo.SetmealDish;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 套餐 服务实现类
 * </p>
 *
 * @author 黑马程序员
 * @since 2022-09-06
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息，操作setmeal，执行insert操作
        this.save(setmealDto); //会获取到一个套餐的ID

        //方式一：
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //保存套餐和菜品的关联信息，操作setmeal_dish,执行insert操作
        setmealDishService.saveBatch(setmealDishes);

        //方式二：
        //for (SetmealDish setmealDish : setmealDishes) {
        //    setmealDish.setSetmealId(setmealDto.getId());
        //    setmealDishService.save(setmealDish);
        //}

        //foreach
        //setmealDto.getSetmealDishes().forEach(setmealDish -> {
        //    setmealDish.setSetmealId(setmealDto.getId());
        //    setmealDishService.save(setmealDish);
        //});
    }

    /**
     * 删除套餐，同时需要删除套餐和菜品的关联数据
     * @param ids
     */
    @Transactional
    @Override
    public void removeWithDish(List<Long> ids) {
        //1.查询套餐状态，确定是否可用删除
        LambdaQueryWrapper<Setmeal> qw1 = new LambdaQueryWrapper<>();
        qw1.in(Setmeal::getId,ids);
        qw1.eq(Setmeal::getStatus, 1);

        int count = this.count(qw1);
        if (count > 0){
            //如果不能删除，抛出一个业务异常
            throw new CustomException("套餐正在售卖中，不能删除");

        }
        //如果可以删除，先删除套餐表中的数据---setmeal
        this.removeByIds(ids);

        LambdaQueryWrapper<SetmealDish> qw2 = new LambdaQueryWrapper<>();
        qw2.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(qw2);
    }
}
