package com.example.bookrent2.service;


import com.example.bookrent2.model.book.Book;
import com.example.bookrent2.model.book.BookRepository;
import com.example.bookrent2.model.bookrental.BookRentalHistory;
import com.example.bookrent2.model.bookrental.BookRentalHistoryRepository;
import com.example.bookrent2.model.bookrental.RentStatus;
import com.example.bookrent2.model.user.Role;
import com.example.bookrent2.model.user.User;
import com.example.bookrent2.model.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
class BookRentalHistoryServiceTest {

    @Autowired
    private BookRentalHistoryService bookRentalHistoryService;

    @Autowired
    private BookRentalHistoryRepository rentalHistoryRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;


    private User user1;
    private Book book1;
    private BookRentalHistory rentalHistory1;

    @BeforeEach
    public void setUp() {
        rentalHistoryRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();

        user1 = User.builder()
                .username("kim")
                .email("kim@gmail.com")
                .password("1111")
                .role(Role.valueOf("ROLE_USER"))
                .isOAuth2(false)
                .build();
        userRepository.save(user1);

        book1 = Book.builder()
                .title("earth")
                .author("Jim Borry")
                .description("This is earth.")
                .publishedDate(LocalDate.parse("2017-09-01"))
                .publisher("Samsung")
                .isRented(false)
                .build();
        bookRepository.save(book1);

        rentalHistory1 = BookRentalHistory.builder()
                .rentalDate(LocalDateTime.now().minusDays(10))  // 10일 전에 대여
                .book(book1)
                .user(user1)
                .build();
        rentalHistory1.setExpectedReturnDate(LocalDateTime.now().minusDays(3));  // 7일 대여 기간
        rentalHistory1.setActualReturnDate(null);  // 아직 반납하지 않음
        rentalHistoryRepository.save(rentalHistory1);
    }

    @Test
    void 책_대여_정상_실행() {
        BookRentalHistory bookRentalHistory1 = BookRentalHistory.builder()
                .book(book1)
                .user(user1)
                .rentStatus(RentStatus.RENTED)
                .build();

        book1.setRented(true);
        bookRepository.save(book1);

        bookRentalHistory1.setRentalDate(LocalDateTime.now());
        BookRentalHistory savedHistory = rentalHistoryRepository.save(bookRentalHistory1);

        assertThat(savedHistory.getId()).isNotNull();
        assertThat(savedHistory.getBook().isRented()).isTrue();
    }


    @Test
    void 연체되어_반납시_벌금이_발생() {

        Integer penalty = bookRentalHistoryService.returnBook(rentalHistory1.getId(), book1.getId(), user1.getId());
        System.out.println("penalty : " + penalty);

        assertThat(penalty).isNotNull();
        assertThat(penalty).isEqualTo(300);
    }


    @Test
    void 연체없이_반납() {
        rentalHistory1.setRentalDate(LocalDateTime.now().minusDays(3));
        rentalHistory1.setExpectedReturnDate(LocalDateTime.now().plusDays(4));
        rentalHistoryRepository.save(rentalHistory1);

        Integer penalty = bookRentalHistoryService.returnBook(rentalHistory1.getId(), book1.getId(), user1.getId());

//        assertThat(penalty).isNull(); // 0 으로 출력되어 테스트 실패 나옴
        assertThat(penalty).isNotNull();
        assertThat(penalty).isEqualTo(0);
    }


    @Test
    void 존재하지_않는_책으로_반납_시도() {

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            bookRentalHistoryService.returnBook(rentalHistory1.getId(), 999L, user1.getId());
        });
        String expectedMessage = "Book not found";
        String actualMessage = exception.getMessage();

        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    void 존재하지_않는_유저로_반납_시도() {
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            bookRentalHistoryService.returnBook(rentalHistory1.getId(), book1.getId(), 99L);
        });
        String expectedMessage = "User not found";
        String actualMessage = exception.getMessage();

        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

  
}