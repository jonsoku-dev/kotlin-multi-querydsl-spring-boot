package com.tamastudy.common.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class User(
        @Id @GeneratedValue
        @Column(name = "user_id")
        var id: Long = 1L,
        var username: String = "",
        var email: String = "",
        var password: String = ""
)