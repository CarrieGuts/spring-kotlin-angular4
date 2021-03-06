package com.shardis.query.domain

import javax.persistence.*

@Entity
@Table(name = "users")
@SequenceGenerator(allocationSize = 1, name = "sequenceIdGenerator", sequenceName = "sequence_users")
class User(
    @Column(nullable = false, length = 250, unique = true) var username: String,
    @Column(nullable = false, length = 120) var password: String,
    @Column(nullable = false, length = 120) var firstName: String,
    @Column(nullable = false, length = 120) var lastName: String,
    @Column(nullable = false, length = 250) var email: String,
    @Column(nullable = false) var enabled: Boolean,
    @Column(nullable = false) var expired: Boolean,
    @Column(nullable = false) var locked: Boolean,
    @ManyToMany(fetch = FetchType.LAZY) var roles: MutableSet<Role> = mutableSetOf()
) : BaseEntity()
