package com.example.bookmanage.domain

import lombok.*
import javax.persistence.*

/**
 * 書籍のエンティティ
 */
@Entity
@Table(name = "book")
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
class Book : AbstractEntity() {

    /**
     * 書籍のID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    /**
     * タイトル
     */
    var title: String? = null

    /**
     * 著者
     */
    var author: String? = null

}