package com.shop.tables

import org.jetbrains.exposed.sql.Table

object ColorTable : Table("colors") {
    val id = integer("id").autoIncrement()
    override val primaryKey = PrimaryKey(id)
    val name = varchar("name", 50)
}