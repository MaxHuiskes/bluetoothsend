#include <SoftwareSerial.h>
#define LIGHT 13
SoftwareSerial btm(2, 3);  // rx tx
int index = 0;
char data[10];
char c;
boolean flag = false;
void setup() {
  pinMode(LIGHT, OUTPUT);
  digitalWrite(LIGHT, LOW);
  btm.begin(9600);
  Serial.begin(9600);
}
void loop() {
  if (btm.available() > 0) {
    while (btm.available() > 0) {
      c = btm.read();
      delay(10);  //Delay required
      data[index] = c;
      index++;
    }
    data[index] = '\\0';
    Serial.println(data);
    flag = true;
  }
  if (flag) {
    processCommand();
    flag = false;
    index = 0;
    data[0] = '\\0';
  }
}
void processCommand() {
  char command = data[0];
  char inst = data[1];
  char exit = data[2];
  if (inst == '1') {
    digitalWrite(LIGHT, HIGH);
    btm.println("2");
  } else if (inst == '2') {
    digitalWrite(LIGHT, LOW);
    btm.println("2");
  } else{
    btm.println("5");
  }
}
