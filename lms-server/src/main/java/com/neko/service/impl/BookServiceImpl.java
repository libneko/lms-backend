package com.neko.service.impl;

import com.neko.constant.MessageConstant;
import com.neko.dto.BookDTO;
import com.neko.dto.BookPageQueryDTO;
import com.neko.entity.Book;
import com.neko.enums.Status;
import com.neko.exception.DeletionNotAllowedException;
import com.neko.mapper.BookMapper;
import com.neko.result.PageResult;
import com.neko.service.BookService;
import com.neko.vo.BookVO;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.opensearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookServiceImpl implements BookService {
    private final BookMapper bookMapper;
    private final RestHighLevelClient restHighLevelClient;

    public BookServiceImpl(BookMapper bookMapper, RestHighLevelClient restHighLevelClient) {
        this.bookMapper = bookMapper;
        this.restHighLevelClient = restHighLevelClient;
    }

    @Override
    @Transactional
    public void save(BookDTO bookDTO) {
        Book book = new Book();
        BeanUtils.copyProperties(bookDTO, book);

        bookMapper.insert(book);
    }

    @Override
    public PageResult<BookVO> pageQuery(BookPageQueryDTO bookPageQueryDTO, boolean filterOutOfStock) throws IOException {
        int from = (bookPageQueryDTO.getPage() - 1) * bookPageQueryDTO.getPageSize();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (bookPageQueryDTO.getName() != null && !bookPageQueryDTO.getName().isEmpty()) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("after.name", bookPageQueryDTO.getName()));
        }

        if (bookPageQueryDTO.getCategoryId() != null) {
            boolQueryBuilder.must(QueryBuilders.termQuery("after.category_id", bookPageQueryDTO.getCategoryId()));
        }

        if (bookPageQueryDTO.getStatus() != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("after.status", bookPageQueryDTO.getStatus()));
        }

        // 根据参数决定是否过滤无库存的书籍
        if (filterOutOfStock) {
            boolQueryBuilder.filter(QueryBuilders.rangeQuery("after.stock").gt(0));
        }

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(boolQueryBuilder)
                .from(from)
                .size(bookPageQueryDTO.getPageSize())
                .sort("after.create_time", SortOrder.DESC);

        SearchRequest searchRequest = new SearchRequest("lms.public.book")
                .source(searchSourceBuilder);

        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        List<BookVO> books = Arrays.stream(response.getHits().getHits())
                .map(hit -> {
                    Map<String, Object> source = (Map<String, Object>) hit.getSourceAsMap().get("after");
                    return BookVO.builder()
                            .id(((Number) source.get("id")).longValue())
                            .name((String) source.get("name"))
                            .author((String) source.get("author"))
                            .categoryId(((Number) source.get("category_id")).longValue())
                            .image((String) source.get("image"))
                            .description((String) source.get("description"))
                            .status((Integer) source.get("status"))
                            .updateTime(LocalDateTime.ofInstant(
                                    Instant.ofEpochMilli(((Number) source.get("update_time")).longValue() / 1000),
                                    ZoneId.systemDefault()
                            ))
                            .stock(((Number) source.get("stock")).longValue())
                            .isbn((String) source.get("isbn"))
                            .location((String) source.get("location"))
                            .publisher((String) source.get("publisher"))
                            .build();
                }).toList();

        long total = response.getHits().getTotalHits().value();
        return new PageResult<>(total, books);
    }

    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        List<Book> books = bookMapper.getByIds(ids);
        if (books.stream().anyMatch(book -> Objects.equals(book.getStatus(), Status.ENABLE.getCode()))) {
            throw new DeletionNotAllowedException(MessageConstant.BOOK_ON_SALE);
        }

        bookMapper.deleteByIds(ids);
    }

    @Override
    public BookVO getById(Long id) {
        Book book = bookMapper.getById(id);
        BookVO bookVO = new BookVO();
        BeanUtils.copyProperties(book, bookVO);
        return bookVO;
    }

    @Override
    public void update(BookDTO bookDTO) {
        Book book = new Book();
        BeanUtils.copyProperties(bookDTO, book);

        bookMapper.update(book);
    }

    @Override
    public void setStatus(Integer status, Long id) {
        Book book = Book.builder()
                .id(id)
                .status(status)
                .build();
        bookMapper.update(book);
    }

    @Override
    public List<Book> list(Long categoryId) {
        Book book = Book.builder()
                .categoryId(categoryId)
                .status(Status.ENABLE.getCode())
                .build();
        return bookMapper.list(book);
    }

    @Override
    public List<BookVO> list(Book book) {
        List<Book> books = bookMapper.list(book);

        return books.stream()
                .map(b -> {
                    BookVO vo = new BookVO();
                    BeanUtils.copyProperties(b, vo);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<BookVO> randomList(Long number) {
        List<Book> books = bookMapper.randomList(number);

        return books.stream()
                .map(b -> {
                    BookVO vo = new BookVO();
                    BeanUtils.copyProperties(b, vo);
                    return vo;
                })
                .collect(Collectors.toList());
    }
}
