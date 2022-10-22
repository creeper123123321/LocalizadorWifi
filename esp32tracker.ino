#include <WiFi.h>
#include <WifiLocation.h>

const char* googleApiKey = "";
const char* ssid = "";
const char* passwd = "";
const char* host = "";

WifiLocation location(googleApiKey);

void setClock() {
  configTime(0, 0, "0.pool.ntp.org", "1.pool.ntp.org");

  Serial.print("Waiting for NTP time sync: ");
  time_t now = time(nullptr);
  while (now < 8 * 3600 * 2) {
    delay(500);
    Serial.print(".");
    now = time(nullptr);
  }
  Serial.println();
  struct tm timeinfo;
  gmtime_r(&now, &timeinfo);
  Serial.println(asctime(&timeinfo));
}

void setup() {
  Serial.begin(9600);
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, passwd);

  while (WiFi.status() != WL_CONNECTED) {
    Serial.println("Connecting to wifi: " + String(ssid));
    Serial.println("Status = " + String(WiFi.status()));
    delay(500);
  }
  setClock();
}

void sendLocation(float lat, float longit) {
  WiFiClientSecure sslClient;
  sslClient.setInsecure();
  if (!sslClient.connect(host, 12388)) {
    Serial.println("Couldn't connect to server");
  }
  sslClient.println("GET /sendLocation?id=test&lat=" + String(lat) + "&long=" + longit + " HTTP/1.1");
  sslClient.println("Host: " + String(host));
  sslClient.println("User-Agent: LocalizadorWifi");
  sslClient.println("Connection: close");
  sslClient.println();
  sslClient.println();

  while (sslClient.connected()) {
    String line = sslClient.readStringUntil('\n');
    if (line == "\r" || line.isEmpty()) break;
    Serial.println("http response: " + line);
  }

  Serial.println("closing http");
  sslClient.stop();
}

void loop() {
  while (WiFi.status() != WL_CONNECTED) {
    Serial.println("Reconnecting wifi due to status " + String(WiFi.status()));
    WiFi.reconnect();
    delay(1000);
  }

  while (1) {
    int n = WiFi.scanNetworks();
    Serial.println(String(n) + " near wifi aps");
    if (n >= 3) break;
  }

  Serial.println("Location request data:");
  Serial.println(location.getSurroundingWiFiJson());

  location_t loc = location.getGeoFromWiFi();
  Serial.println("Location: " + String(loc.lat) + "," + String(loc.lon));
  Serial.println("Accuracy: " + String(loc.accuracy));
  Serial.println("Result: " + location.wlStatusStr(location.getStatus()));

  if (location.getStatus() == WL_OK) {
    sendLocation(loc.lat, loc.lon);
  }

  delay(60 * 1000);
}