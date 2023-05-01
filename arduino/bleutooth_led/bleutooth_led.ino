#include <SoftwareSerial.h>
#define LIGHT 13
#define BUZZER 12
SoftwareSerial btm(2, 3);  // rx tx
int index = 0;
char data[10];
char c;
int buzz = 0;
bool on = 0;
boolean flag = false;
void setup() {
  pinMode(LIGHT, OUTPUT);
  digitalWrite(LIGHT, LOW);
  pinMode(BUZZER, OUTPUT);
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
    buzzersound();
    flag = false;
    index = 0;
    data[0] = '\\0';
  }

}
void processCommand() {
  char command = data[0];
  char inst = data[1];
  char exit = data[2];
  if (inst == '3'){
    on = !on;
  }
  if (inst == '1' || (inst == '3' && on)) {
    digitalWrite(LIGHT, HIGH);
    buzz = 1;
    btm.println("2");
  } else if (inst == '2' || (inst == '3' && !on) ) {
    digitalWrite(LIGHT, LOW);
    buzz = 0;
    btm.println("2");
  } else {
    btm.println("5");
  }
}

void buzzersound() {
  if (buzz == 1) {
    tone(BUZZER, 1000);
  } else {
    noTone(BUZZER);
  }
}
