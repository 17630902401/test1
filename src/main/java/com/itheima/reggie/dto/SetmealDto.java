package com.itheima.reggie.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.itheima.reggie.pojo.Setmeal;
import com.itheima.reggie.pojo.SetmealDish;
import lombok.Data;

import java.util.List;
@Data
public class SetmealDto extends Setmeal {

    //套餐关联的菜品集合
    private List<SetmealDish> setmealDishes;

    //分类名称
    private String categoryName;

    @TableField(exist = false)
    private String image;
}
