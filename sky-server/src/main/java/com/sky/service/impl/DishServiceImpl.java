package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    /**
     * 新增菜品和相应的口味
     *
     * @param dishDTO
     */
    @Override
    @Transactional      //注意启动类要开启事务管理（spring默认就是开启）
    public void saveWithFlavor(DishDTO dishDTO) {

        //向菜品表插入一条数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);    //不传递口味变量
        dishMapper.insert(dish);

        //向口味表插入n条数据
        Long dishId = dish.getId();     //获取dish生成的id值
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors!=null&& !flavors.isEmpty()){     //说明有口味数据
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);   //修改口味表中的id值为对应的菜品的id
            });
            dishFlavorMapper.insertBatch(flavors);
        }

    }
}
