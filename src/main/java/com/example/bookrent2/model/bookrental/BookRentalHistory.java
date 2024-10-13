package com.example.bookrent2.model.bookrental;

import com.example.bookrent2.model.book.Book;
import com.example.bookrent2.model.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class BookRentalHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime rentalDate;
    private LocalDateTime expectedReturnDate;

    @Setter //
    private LocalDateTime actualReturnDate;

    @Column(length = 1000)
    private String comment;

    @Enumerated(EnumType.STRING)
    private RentStatus rentStatus;

    private static final int PRICE_PER_DAY = 100;

    private int penaltyAmount; // 벌금 1 일 * 100 원씩 증가

    /** ***************************************** */

    // rentalDate 설정 시 expectedReturnDate 자동 계산
    // ((참고)) actualReturnDate ( 실제 반납일 설정 -> @setter 이용하였음 )
    public void setRentalDate(LocalDateTime rentalDate) {
        this.rentalDate = rentalDate;
        this.expectedReturnDate = rentalDate.plusDays(7);  // 7일 후 반납 예정일 계산
    }

    public boolean isPenalty() {
        return actualReturnDate != null && actualReturnDate.isAfter(expectedReturnDate);
    }

    public int calculatePenalty() {
        if(isPenalty()) {
            long daysLate = ChronoUnit.DAYS.between(expectedReturnDate, actualReturnDate);
            penaltyAmount = (int) (daysLate * PRICE_PER_DAY);
        } else {
            return penaltyAmount = 0;
        }
        return penaltyAmount;
    }

    /** *********************************************** */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;


    @Builder
    public BookRentalHistory(LocalDateTime rentalDate, RentStatus rentStatus, User user, Book book) {
        this.rentalDate = rentalDate;
        this.rentStatus = rentStatus;
        this.user = user;
        this.book = book;
    }
}
