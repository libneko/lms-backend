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

    @Insert("insert into borrow_cart (name, user_id, book_id, number, amount, image, create_time) " +
            " values (#{name},#{userId},#{bookId},#{number},#{amount},#{image},#{createTime})")
    void insert(BorrowCart borrowCart);

    @Delete("delete from borrow_cart where user_id = #{userId}")
    void deleteByUserId(Long userId);

    /**
     * 批量插入借阅车数据
     *
     * @param borrowCartList
     */
    void insertBatch(List<BorrowCart> borrowCartList);

    @Delete("delete from borrow_cart where user_id = #{userId} and book_id = #{bookId}")
    void delete(BorrowCart borrowCart);
}