package com.example.bookrent2.rentBook.service;


import com.example.bookrent2.rentBook.model.book.Book;
import com.example.bookrent2.rentBook.model.book.BookRepository;
import com.example.bookrent2.rentBook.model.bookrental.BookRentalHistory;
import com.example.bookrent2.rentBook.model.bookrental.BookRentalHistoryRepository;
import com.example.bookrent2.rentBook.model.bookrental.RentStatus;
import com.example.bookrent2.rentBook.model.user.User;
import com.example.bookrent2.rentBook.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@RequiredArgsConstructor
@Service
public class BookRentalHistoryService {

    private final BookRentalHistoryRepository rentalHistoryRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;


    private BookRentalHistory rentBook(Long bookId, Long userId) {
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new IllegalStateException("Book not found")
        );
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalStateException("User not found")
        );

        BookRentalHistory bookRentalHistory1 = BookRentalHistory.builder()
                .book(book)
                .user(user)
                .rentStatus(RentStatus.RENTED)
                .build();

        book.setRented(true);
        bookRepository.save(book);

        bookRentalHistory1.setRentalDate(LocalDateTime.now());
        return rentalHistoryRepository.save(bookRentalHistory1);
    }


    public Integer returnBook(Long rentalId, Long bookId, Long userId) {
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new IllegalStateException("Book not found")
        );
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalStateException("User not found")
        );

        BookRentalHistory bookRentalHistory1 = rentalHistoryRepository.findById(rentalId).orElseThrow(
                () -> new IllegalStateException("Rental history not found")
        );

        if(bookRentalHistory1.getBook().getId().equals(book.getId())
                && bookRentalHistory1.getUser().getId().equals(user.getId())) {

            bookRentalHistory1.setActualReturnDate(LocalDateTime.now());
            bookRentalHistory1.setRentStatus(RentStatus.RETURNED);
            book.setRented(false);
            bookRepository.save(book);

            int penalty = bookRentalHistory1.calculatePenalty();
            rentalHistoryRepository.save(bookRentalHistory1);
            return penalty;
        }
        return null; // 만약 책 또는 사용자가 일치하지 않는다면 null 반환
    }





}
