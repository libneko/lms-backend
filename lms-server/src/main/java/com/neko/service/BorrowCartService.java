package com.neko.service;

import com.neko.dto.BorrowCartDTO;
import com.neko.entity.BorrowCart;

import java.util.List;

public interface BorrowCartService {
    void addOrUpdate(BorrowCartDTO borrowCartDTO, boolean isAdd);

    List<BorrowCart> show();

    void clean();

    void delete(BorrowCart borrowCart);
}
