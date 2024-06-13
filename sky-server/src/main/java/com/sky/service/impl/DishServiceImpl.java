package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
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

    @Autowired
    private SetmealDishMapper setmealDishMapper;

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
        if (flavors != null && !flavors.isEmpty()) {     //说明有口味数据
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);   //修改口味表中的id值为对应的菜品的id
            });
            dishFlavorMapper.insertBatch(flavors);
        }

    }


    /**
     * 分页查询菜品
     *
     * @param dishPageQueryDTO
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());    //开始分页
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);

        return new PageResult(page.getTotal(), page.getResult());
    }


    /**
     * 批量删除菜品
     *
     * @param ids
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {

        //判断当前菜品是否能够删除----处于起售状态,与套餐关联
        for (Long id : ids) {
            //获取每一个id，判断是否满足删除条件
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {   //位于起售中
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);    //位于起售中的菜品不能删除
            }
        }
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids); //ids中关联的套餐数量
        if(setmealIds!=null&&setmealIds.size()>0){      //存在与套餐关联的id
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }

        //批量删除菜品
       /* for (Long id : ids) {
            //主键删除菜品
            dishMapper.deletebyId(id);
            //连同对应的口味数据一起删除
            dishFlavorMapper.deleteByDishId(id);
        }*/
        //优化
        // sql:delete from dish where id in (ids)
        dishMapper.deletebyIds(ids);
        // sql:delete from dish_flavor where dish_id in (ids)
        dishFlavorMapper.deleteByDishIds(ids);


    }
}
