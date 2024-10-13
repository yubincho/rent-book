package com.example.bookrent2.controller;



import com.example.bookrent2.service.BookRentalHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("/rent-history")
public class BookRentalHistoryController {

    private final BookRentalHistoryService rentalHistoryService;




}
