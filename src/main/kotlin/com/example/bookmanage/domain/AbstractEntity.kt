package com.example.bookmanage.domain

import org.springframework.security.core.context.SecurityContextHolder
import java.time.LocalDateTime
import javax.persistence.*

/**
 * Entityクラスの共通クラス<br></br>
 *
 * 共通項目の定義と更新処理を実装している。
 */
@MappedSuperclass
abstract class AbstractEntity {

    /**
     * 作成ユーザ
     */
    @Column(name = "created_user")
    var createdUser: String? = null

    /**
     * 作成日時
     */
    @Column(name = "created_date_time")
    var createdDateTime: LocalDateTime? = null

    /**
     * 更新ユーザ
     */
    @Column(name = "updated_user")
    var updatedUser: String? = null

    /**
     * 更新日時
     */
    @Column(name = "updated_date_time")
    var updatedDateTime: LocalDateTime? = null

    /**
     * バージョン
     */
    @Version
    var version: Long = 0

    /**
     * 新規登録時に呼び出される前処理
     */
    @PrePersist
    fun prePersist() {
        val datetime = LocalDateTime.now()
        createdDateTime = datetime
        updatedDateTime = datetime
        val context = SecurityContextHolder.getContext()
        val userName = context.authentication.name
        createdUser = userName
        updatedUser = userName
    }

    /**
     * 更新時に呼び出される前処理
     */
    @PreUpdate
    fun preUpdate() {
        updatedDateTime = LocalDateTime.now()
        val context = SecurityContextHolder.getContext()
        updatedUser = context.authentication.name
    }

}