package com.hong.es;

import com.hong.es.entity.Book;
import com.hong.es.service.BookService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wanghong
 * @date 2019/08/22 22:35
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class EsDemoTests {
    @Autowired
    private BookService bookService;

    @Test
    public void getOne() {
        System.out.println(bookService.getById(1).toString());
    }

    @Test
    public void getAll() {
        List<Book> res = bookService.getAll();
        res.forEach(System.out::println);
    }

    @Test
    public void addOneTest() {
        bookService.putOne(new Book(1, 1, "格林童话"));
        bookService.putOne(new Book(2, 1, "美人鱼"));
    }

    @Test
    public void addBatchTest() {
        List<Book> list = new ArrayList<>();
        list.add(new Book(3, 1, "第一本书"));
        list.add(new Book(4, 1, "第二本书"));
        bookService.putList(list);
    }

    @Test
    public void deleteBatch() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(3);
        bookService.deleteBatch(list);
    }

    @Test
    public void deleteByQuery(){
        bookService.deleteByUserId(1);
    }

}
