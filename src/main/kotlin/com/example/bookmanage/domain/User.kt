package com.example.bookmanage.domain

import javax.persistence.*

@Entity
@Table(name = "users")
data class User(
    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    /**
     * ユーザ名
     */
    var username: String? = null,

    /**
     * パスワード
     */
    var password: String? = null,

    /**
     * 有効か否か
     */
    var enabled: Boolean? = null,

    /**
     * 権限
     */
    var authority: String? = null
)
