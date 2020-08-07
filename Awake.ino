#include "BluetoothSerial.h"
BluetoothSerial ESP_BT;
#include <stdlib.h>
unsigned long timer; // the timer
unsigned long INTERVAL_5 ;// 2 นาที
unsigned long INTERVAL_2 = 60000;
//unsigned long previousMillis = 0;
int interval = 10000;
int inputPir = 34;
int relayPin = 18;
int motionDetected = 0;
String datas;
int val;
void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  pinMode(inputPir, INPUT);
  pinMode(relayPin, OUTPUT);
  ESP_BT.begin("ESP32_Board");
}

void loop() {
  // put your main code here, to run repeatedly:
  unsigned long currentMillis = millis();
  digitalWrite(relayPin, HIGH);
  if (ESP_BT.available()) {
    while (ESP_BT.available()) {
      char chr = ESP_BT.read(); //gets one byte from serial buffer
      datas += chr;
      delay(10);
    }
    Serial.println("Connected Bluetooth");
    int i = datas.toInt();
    INTERVAL_5 = i;
    sendSettime(INTERVAL_5);
    int k = 1;
    if (k == val) {
      Serial.println("Connected and received Text");
      while (1) {
        digitalWrite(relayPin, LOW);
        int value = digitalRead(inputPir);
        if (value == HIGH)
        {
          motionDetected++;
          Serial.println("Event: READ");
          Serial.println(motionDetected);
          delay(1000);

        } if (value == LOW) {
          motionDetected = 0;
          Serial.println("Event: LOW");
          delay(100);
        }
        if (motionDetected > 10)
        {
          Serial.println("AWAKE");
          ESP_BT.print("AWAKE");
          digitalWrite(relayPin, HIGH);
          INTERVAL_5 = 0;
          break;
        }
      }//end while
      if (datas.indexOf("OFF"))
      {
        while (1)
        {
          INTERVAL_5 = 0;
          break;
        }
      }
    }
    if (datas.indexOf("OFF"))
    {
      while (1)
      {
        INTERVAL_5 = 0;
        break;
      }
    }
  }
}

int sendSettime(int i)
{
  Serial.println(INTERVAL_5);
  while (1) {
    Serial.println(millis());
    if ((millis() - timer) > INTERVAL_5) {
      val = 1;
      return val;
      break;
    }
    delay(1000);
  }
}
