package com.example.bookmanage.repository

import com.example.bookmanage.domain.Book
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * 書籍のリポジトリ
 */
@Repository
interface BookRepository : JpaRepository<Book, Long>