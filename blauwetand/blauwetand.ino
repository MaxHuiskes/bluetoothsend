#include <SoftwareSerial.h>

SoftwareSerial BTserial(2, 3); // RX | TX

void setup() {
  Serial.begin(9600);
  BTserial.begin(9600); // Set Bluetooth serial baud rate
}

void loop() {
  if (BTserial.available()) {
    // Read incoming data from Bluetooth and print it to the serial monitor
    char c = BTserial.read();
    Serial.println(c);
  }
}