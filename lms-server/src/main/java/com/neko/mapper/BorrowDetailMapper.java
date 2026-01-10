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
}
