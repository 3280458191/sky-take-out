package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 套餐
 */
@Mapper
public interface SetmealDishMapper {
    /**
     * 根据菜品id查询套餐
     * @param dishIds
     * @return
     */
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);


}
