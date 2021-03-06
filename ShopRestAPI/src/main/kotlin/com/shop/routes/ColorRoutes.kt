package com.shop.routes

import com.shop.models.*
import com.shop.tables.ColorTable
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.colorSerialization() {
    getColor()
    postColor()
    putColor()
    deleteColor()
}

private fun Application.getColor() {
    routing {
        get(colorSign) {
            var colors = mutableListOf<Color>()
            transaction {
                colors = ColorTable.selectAll().map { it.toColor() }.toMutableList()
            }
            call.respond(colors)
        }

        get(colorIdSign) {
            val id: Int = call.parameters["id"]!!.toInt()
            var color = Color()
            transaction {
                color = ColorTable.select { ColorTable.id eq id }.map { it.toColor() }.first()
            }
            call.respond(color)
        }
    }
}

private fun Application.postColor() {
    routing {
        post(colorSign) {
            val color = call.receive<Color>()
            addColorToDatabase(color)
            call.respondText("Color stored correctly", status = HttpStatusCode.Created)
        }
    }
}

private fun Application.putColor() {
    routing {
        put(colorIdSign) {
            val id = call.parameters["id"]
            val color = call.receive<Color>()
            transaction {
                ColorTable.update({ ColorTable.id eq id!!.toInt() }) {
                    with(SqlExpressionBuilder) {
                        it[ColorTable.name] = color.name
                    }
                }
            }
        }
    }
}

private fun Application.deleteColor() {
    routing {
        delete(colorSign) {
            transaction {
                SchemaUtils.drop(ColorTable)
                SchemaUtils.create(ColorTable)
            }
        }

        delete(colorIdSign) {
            val id = call.parameters["id"]
            transaction {
                ColorTable.deleteWhere { ColorTable.id eq id!!.toInt() }
            }
        }
    }
}

private fun addColorToDatabase(color: Color) {
    transaction {
        ColorTable.insert {
            it[name] = color.name
        }
    }
}
