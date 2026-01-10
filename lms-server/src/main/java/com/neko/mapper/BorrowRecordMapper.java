package com.neko.mapper;

import com.neko.dto.BorrowPageQueryDTO;
import com.neko.entity.BorrowRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface BorrowRecordMapper {

    void insert(BorrowRecord borrowRecord);

    /**
     * 根据借阅号和用户 id 查询借阅记录
     *
     * @param borrowNumber
     * @param userId
     */
    @Select("select * from borrow_records where number = #{borrowNumber} and user_id= #{userId}")
    BorrowRecord getByNumberAndUserId(String borrowNumber, Long userId);

    /**
     * 修改借阅信息
     *
     * @param borrowRecord
     */
    void update(BorrowRecord borrowRecord);

    /**
     * 分页条件查询并按借阅时间排序
     *
     * @param borrowPageQueryDTO
     */
    List<BorrowRecord> pageQuery(BorrowPageQueryDTO borrowPageQueryDTO);

    /**
     * 条件查询借阅记录数量
     *
     * @param borrowPageQueryDTO
     */
    Long count(BorrowPageQueryDTO borrowPageQueryDTO);

    /**
     * 根据 id 查询借阅记录
     *
     * @param id
     */
    @Select("select * from borrow_records where id=#{id}")
    BorrowRecord getById(Long id);

    @Select("select * from borrow_records where status = #{status} and borrow_time < #{borrowTime}")
    List<BorrowRecord> getByStatusAndBorrowTimeLT(Integer status, LocalDateTime borrowTime);

    /**
     * 根据状态统计借阅记录数量
     *
     * @param status
     */
    @Select("select count(id) from borrow_records where status = #{status}")
    Integer countStatus(Integer status);

    Double sumByMap(Map<String, Object> map);

    Integer countByMap(Map<String, Object> map);

    @Select("select * from borrow_records where number = #{borrowNumber}")
    BorrowRecord getByNumber(String borrowNumber);
}
