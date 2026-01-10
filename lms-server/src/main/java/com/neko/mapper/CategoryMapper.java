package com.neko.mapper;

import com.github.pagehelper.Page;
import com.neko.annotation.AutoFill;
import com.neko.dto.CategoryPageQueryDTO;
import com.neko.entity.Category;
import com.neko.enums.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {

    /**
     * 插入数据
     *
     * @param category
     */
    @Insert("insert into category(name, sort, status, create_time, update_time)" +
            " VALUES" +
            " (#{name}, #{sort}, #{status}, #{createTime}, #{updateTime})")
    @AutoFill(value = OperationType.INSERT)
    void insert(Category category);

    /**
     * 分页查询
     *
     * @param categoryPageQueryDTO
     * @return
     */
    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 根据 id 删除分类
     *
     * @param id
     */
    @Delete("delete from category where id = #{id}")
    void deleteById(Long id);

    /**
     * 根据 id 修改分类
     *
     * @param category
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Category category);

    /**
     * 查询分类
     *
     * @return
     */
    List<Category> list();
}