package com.itheima.reggie.dto;

import com.itheima.reggie.pojo.Dish;
import com.itheima.reggie.pojo.DishFlavor;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString(callSuper = true)
public class DishDto extends Dish {
    //菜品的口味集合
    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}