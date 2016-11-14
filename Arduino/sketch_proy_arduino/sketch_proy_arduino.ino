#include <Wire.h>
#include "rgb_lcd.h"
#include <Servo.h>

Servo myservo;                                        //declaro la variable que maneja el servo
rgb_lcd lcd;                                          //declaro la variable que maneja la pantalla
  
int colorR = 0;
int colorG = 255;     //color incial = verde
int colorB = 0;
int puntaje = 0;
int pinPote = 0; 
int pinBoton = 4;
int pinSensor = 2;
int pinServo = 3;
int val;


void setup() {
  
   lcd.begin(16, 2);    
   myservo.attach(pinServo);
   lcd.setRGB(colorR, colorG, colorB);
   escribirPuntaje(0);
   Serial.begin(115200);
 
}

void loop() {

  val = analogRead(pinPote);            // leo el valor del potenciometro (entre 0 y 1023)
  val = map(val, 0, 1023, 0, 160);      // lo cambio al angulo del servo (entre 0 y 160)
  myservo.write(val);                   // setea ese angulo al servo
  delay(150);                           // delay para que el servo se posicione en el lugar indicado

  //si apretas el boton reinicia puntaje
  if(digitalRead(pinBoton) == HIGH)
  {
    puntaje=0;
    escribirPuntaje(puntaje);   //reiniciar contador    
    delay(100);
   }

//forma de detectar gol aca
  if(digitalRead(pinSensor) == HIGH)
  {
    puntaje++;
    gol();                      //funcion de marcar gol
    escribirPuntaje(puntaje);   //actualizar contador
    delay(3000);
  }

}

void escribirPuntaje(int aux){
  lcd.clear();
  lcd.setCursor(0,0);
  lcd.print("Puntaje: ");
  lcd.print(aux);
}

void gol(){
  //titila la pantalla con 6 colores cuando se marca un gol 
  lcd.clear();            //limpio la pantalla 
  lcd.setCursor(0,0);     //me paro en la posicion 0 del LCD
  lcd.print("GOOOL!");    //escribo gol
  lcd.setRGB(255,0,0);    //cambio de color   (rojo)
  delay(300); 
  lcd.print("!");         //agrego un "!"
  lcd.setRGB(255,0,255);  //cambio a violeta
  delay(300);
  lcd.print("!");
  lcd.setRGB(0,0,255);    //cambio a azul
  delay(300);
  lcd.print("!");
  lcd.setRGB(255,255,0);  //cambio a 
  delay(300);
  lcd.print("!");
  lcd.setRGB(0,255,255);  //cambio a 
  delay(300);
  lcd.print("!");
  lcd.setRGB(0,255,0);    //cambio a verde
  delay(300);
}

