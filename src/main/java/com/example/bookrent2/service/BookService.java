package com.example.bookrent2.service;


import com.example.bookrent2.model.book.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;


}
