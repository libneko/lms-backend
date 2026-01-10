package com.neko.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.neko.constant.MessageConstant;
import com.neko.dto.CategoryDTO;
import com.neko.dto.CategoryPageQueryDTO;
import com.neko.entity.Category;
import com.neko.enums.Status;
import com.neko.exception.DeletionNotAllowedException;
import com.neko.mapper.BookMapper;
import com.neko.mapper.CategoryMapper;
import com.neko.result.PageResult;
import com.neko.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper categoryMapper;
    private final BookMapper bookMapper;

    public CategoryServiceImpl(CategoryMapper categoryMapper, BookMapper bookMapper) {
        this.categoryMapper = categoryMapper;
        this.bookMapper = bookMapper;
    }

    @Override
    public void save(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        category.setStatus(Status.DISABLE.getCode());

        categoryMapper.insert(category);
    }

    @Override
    public PageResult<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        Page<Category> page = categoryMapper.pageQuery(categoryPageQueryDTO);
        return new PageResult<>(page.getTotal(), page.getResult());
    }

    @Override
    public void deleteById(Long id) {
        Integer count = bookMapper.countByCategoryId(id);
        if (count > 0) {
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_BOOK);
        }

        categoryMapper.deleteById(id);
    }

    @Override
    public void update(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);

        categoryMapper.update(category);
    }

    @Override
    public void updateStatusById(Integer status, Long id) {
        Category category = new Category();
        category.setId(id);
        category.setStatus(status);

        categoryMapper.update(category);
    }

    @Override
    public List<Category> list() {
        return categoryMapper.list();
    }
}
