let busIcon = L.icon({
    iconSize: [32, 32], // size of the icon
    iconUrl: "https://cdnjs.cloudflare.com/ajax/libs/bootstrap-icons/1.9.1/icons/truck-front-fill.svg"
})

let map = L.map('map')
map.locate({setView: true, maxZoom: 16})
L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
}).addTo(map)

let userLatLng = [0, 0];

map.on("locationerror", it => console.log(it.message));
map.on("locationfound", onLocationFound);
function onLocationFound(e) {
    userLatLng = e.latlng;
}

let vehicleMarkers = {};
let synth = window.speechSynthesis;

let lc = L.control.locate({clickBehavior: {inViewNotFollowing: "setView"}}).addTo(map);
lc.start();

setInterval(() => {
    $.ajax("/getLocations", {
        dataType: "json"
    }).done(data => {
        Object.entries(data).forEach(entry => {
            let name = entry[0];
            let info = entry[1];
            let marker = vehicleMarkers[name];
            if (marker == null) {
                vehicleMarkers[name] = L.marker([0, 0], {alt: "Vehicle " + name}).addTo(map);
                marker = vehicleMarkers[name]
            }
            marker.setLatLng([info.latitude, info.longitude]);
            marker.setIcon(busIcon)
            marker.bindPopup(name + " Last updated at: " + info["updatedAt"])
        })
    })
}, 3000);

setInterval(() => {
    Object.entries(vehicleMarkers).forEach(it => {
        let dist = map.distance(it[1].getLatLng(), userLatLng);
        if (dist <= 200 && $("#enableSound").is(":checked")) {
            let utterance = new SpeechSynthesisUtterance(it[0] + " is near you");
            utterance.lang = "en";
            synth.speak(utterance);
        }
    });
}, 4000)