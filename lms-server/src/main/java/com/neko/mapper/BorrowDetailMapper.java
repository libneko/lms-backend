package com.neko.mapper;

import com.neko.entity.BorrowDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BorrowDetailMapper {

    void insertBatch(List<BorrowDetail> borrowDetailList);

    /**
     * 根据借阅记录 id 查询借阅明细
     *
     * @param borrowRecordId
     * @return
     */
    @Select("select * from borrow_details where borrow_record_id = #{borrowRecordId}")
    List<BorrowDetail> getByBorrowRecordId(Long borrowRecordId);

    /**
     * 查询用户已借阅且未归还的图书ID列表
     *
     * @param userId 用户ID
     * @param status 借阅状态（1-已借出，2-已归还）
     * @return 图书ID列表
     */
    @Select("select DISTINCT bd.book_id from borrow_details bd " +
            "INNER JOIN borrow_records br ON bd.borrow_record_id = br.id " +
            "WHERE br.user_id = #{userId} AND br.status = #{status}")
    List<Long> getBorrowedBookIdsByUserAndStatus(Long userId, Integer status);
}
