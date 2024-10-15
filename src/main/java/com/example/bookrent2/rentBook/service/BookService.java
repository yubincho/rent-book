package com.example.bookrent2.rentBook.service;


import com.example.bookrent2.rentBook.model.book.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;


}
