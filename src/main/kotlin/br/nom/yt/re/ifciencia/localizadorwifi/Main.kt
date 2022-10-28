package br.nom.yt.re.ifciencia.localizadorwifi

import io.ktor.http.*
import io.ktor.network.tls.certificates.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import java.util.*

fun genCert() {
    val keyStoreFile = File("build/keystore.jks")
    if (keyStoreFile.exists()) return
    generateCertificate(
        file = keyStoreFile,
        keyAlias = "sampleAlias",
        keyPassword = "foobar",
        jksPassword = "foobar"
    )
}

fun main(args: Array<String>) {
    genCert()
    EngineMain.main(args)
}

fun Application.module() {
    val vehicles = hashMapOf<String, InfoVehicle>()
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }
    routing {
        static("/") {
            staticBasePackage = "static"
            resources()
            defaultResource("index.html")
        }
        get("/sendLocation") {
            val lat = call.request.queryParameters["lat"]!!.toDouble()
            val long = call.request.queryParameters["long"]!!.toDouble()
            val id = call.request.queryParameters["id"]!!
            val info = vehicles.computeIfAbsent(id) {
                InfoVehicle(id, 0.0, 0.0, Date())
            }
            info.latitude = lat
            info.longitude = long
            info.updatedAt = Date()
            call.respondText("OK", status = HttpStatusCode.OK)
        }
        get("/getLocations") {
            call.respond(vehicles)
        }
    }
}

