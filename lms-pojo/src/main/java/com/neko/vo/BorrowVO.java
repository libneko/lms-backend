package com.neko.vo;

import com.neko.entity.BorrowDetail;
import com.neko.entity.BorrowRecord;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BorrowVO extends BorrowRecord {

    // 借阅书籍信息
    private String borrowBooks;

    // 借阅详情
    private List<BorrowDetail> borrowDetailList;

}