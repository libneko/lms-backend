package com.neko.mapper;

import com.neko.entity.BorrowCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface BorrowCartMapper {

    List<BorrowCart> list(BorrowCart borrowCart);

    @Update("update borrow_cart set number = #{number} where id = #{id}")
    void updateNumberById(BorrowCart borrowCart);

    @Insert("insert into borrow_cart (name, user_id, book_id, number, image, create_time) " +
            " values (#{name},#{userId},#{bookId},#{number},#{image},#{createTime})")
    void insert(BorrowCart borrowCart);

    @Delete("delete from borrow_cart where user_id = #{userId}")
    void deleteByUserId(Long userId);

    /**
     * 根据用户ID和图书ID列表批量删除借阅车中的图书
     *
     * @param userId  用户ID
     * @param bookIds 图书ID列表
     */
    void deleteByUserIdAndBookIds(Long userId, List<Long> bookIds);

    /**
     * 批量插入借阅车数据
     *
     * @param borrowCartList
     */
    void insertBatch(List<BorrowCart> borrowCartList);

    @Delete("delete from borrow_cart where user_id = #{userId} and book_id = #{bookId}")
    void delete(BorrowCart borrowCart);
}