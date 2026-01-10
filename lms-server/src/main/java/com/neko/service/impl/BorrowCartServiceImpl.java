package com.neko.service.impl;

import com.neko.context.BaseContext;
import com.neko.dto.BorrowCartDTO;
import com.neko.entity.Book;
import com.neko.entity.BorrowCart;
import com.neko.mapper.BookMapper;
import com.neko.mapper.BorrowCartMapper;
import com.neko.service.BorrowCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class BorrowCartServiceImpl implements BorrowCartService {

    private final BorrowCartMapper borrowCartMapper;
    private final BookMapper bookMapper;

    public BorrowCartServiceImpl(BorrowCartMapper borrowCartMapper, BookMapper bookMapper) {
        this.borrowCartMapper = borrowCartMapper;
        this.bookMapper = bookMapper;
    }

    @Override
    public void addOrUpdate(BorrowCartDTO borrowCartDTO, boolean isAdd) {
        BorrowCart borrowCart = new BorrowCart();
        BeanUtils.copyProperties(borrowCartDTO, borrowCart);
        Long userId = BaseContext.getCurrentId();
        borrowCart.setUserId(userId);

        List<BorrowCart> list = borrowCartMapper.list(borrowCart);

        if (list != null && !list.isEmpty()) {
            BorrowCart cart = list.getFirst();
            cart.setNumber(borrowCartDTO.getNumber() + (isAdd ? cart.getNumber() : 0));
            borrowCartMapper.updateNumberById(cart);
        } else {
            Long bookId = borrowCartDTO.getBookId();
            Book book = bookMapper.getById(bookId);
            borrowCart.setName(book.getName());
            borrowCart.setImage(book.getImage());
            borrowCart.setAmount(book.getPrice());

            borrowCart.setNumber(borrowCartDTO.getNumber());
            borrowCart.setCreateTime(LocalDateTime.now());

            borrowCartMapper.insert(borrowCart);
        }
    }

    @Override
    public List<BorrowCart> show() {
        Long userId = BaseContext.getCurrentId();
        BorrowCart borrowCart = new BorrowCart();
        borrowCart.setUserId(userId);
        return borrowCartMapper.list(borrowCart);
    }

    @Override
    public void clean() {
        Long userId = BaseContext.getCurrentId();
        borrowCartMapper.deleteByUserId(userId);
    }

    @Override
    public void delete(BorrowCart borrowCart) {
        borrowCartMapper.delete(borrowCart);
    }
}
