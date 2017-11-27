package net.pranavprakash.app

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.response.*
import io.ktor.routing.*
import java.text.*
import org.jetbrains.exposed.sql.*
import io.ktor.request.receive
import net.pranavprakash.app.controller.MessagesController
import net.pranavprakash.app.model.Message
import mu.*

/*
    Init Postgresql database connection
 */
fun initDB() {
    val url = "jdbc:postgresql://localhost/testdb?user=pranavprakash&password="
    val driver = "org.postgresql.Driver"
    Database.connect(url, driver)
}

fun Application.main() {
    install(Compression)
    install(CORS)
    install(DefaultHeaders)
    install(CallLogging)
    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }
    }
    initDB()
    install(Routing) {

        val logger = KotlinLogging.logger { }

        route("/api") {
            route("/messages") {

                get("/") {
                    call.respond(MessagesController().index())
                }

                post("/") {
                    val message = call.receive<Message>()
                    logger.debug { message }
                    call.respond(MessagesController().create(message))
                }

                get("/{id}") {
                    val id = call.parameters["id"]!!.toInt()
                    call.respond(MessagesController().show(id))
                }

                put("/{id}") {
                    val id = call.parameters["id"]!!.toInt()
                    val message = call.receive<Message>()
                    call.respond(MessagesController().update(id, message))
                }

                delete("/{id}") {
                    val id = call.parameters["id"]!!.toInt()
                    call.respond(MessagesController().delete(id))
                }
            }
        }
    }
}