package com.neko.mapper;

import com.github.pagehelper.Page;
import com.neko.annotation.AutoFill;
import com.neko.dto.BookPageQueryDTO;
import com.neko.entity.Book;
import com.neko.enums.OperationType;
import com.neko.vo.BookVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BookMapper {

    @Select("select count(id) from book where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    @AutoFill(value = OperationType.INSERT)
    void insert(Book book);

    Page<BookVO> pageQuery(BookPageQueryDTO bookPageQueryDTO);

    @Select("select * from book where id = #{id}")
    Book getById(Long id);

    List<Book> list(Book book);

    List<Book> getByIds(List<Long> ids);

    void deleteByIds(List<Long> ids);

    @AutoFill(value = OperationType.UPDATE)
    void update(Book book);

    List<Book> randomList(Long number);
}
