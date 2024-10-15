package com.example.bookrent2.rentBook.model.bookrental;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRentalHistoryRepository extends JpaRepository<BookRentalHistory, Long> {
}
