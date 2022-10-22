package br.nom.yt.re.ifciencia.localizadorwifi

import java.util.Date

data class InfoVehicle(
    val identifier: String,
    var latitude: Double,
    var longitude: Double,
    var updatedAt: Date
)