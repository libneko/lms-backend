package com.neko.service;

import com.neko.dto.CategoryDTO;
import com.neko.dto.CategoryPageQueryDTO;
import com.neko.entity.Category;
import com.neko.result.PageResult;

import java.util.List;

public interface CategoryService {

    /**
     * 新增分类
     *
     * @param categoryDTO
     */
    void save(CategoryDTO categoryDTO);

    /**
     * 分页查询
     *
     * @param categoryPageQueryDTO
     * @return
     */
    PageResult<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 根据 id 删除分类
     *
     * @param id
     */
    void deleteById(Long id);

    /**
     * 修改分类
     *
     * @param categoryDTO
     */
    void update(CategoryDTO categoryDTO);

    /**
     * 启用、禁用分类
     *
     * @param status
     * @param id
     */
    void updateStatusById(Integer status, Long id);

    /**
     * 根据类型查询分类
     *
     * @return
     */
    List<Category> list();
}