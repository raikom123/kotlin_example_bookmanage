package com.example.bookmanage

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BookmanageApplication

fun main(args: Array<String>) {
	runApplication<BookmanageApplication>(*args)
}
