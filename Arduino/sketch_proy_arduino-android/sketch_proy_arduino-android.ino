#include <Wire.h>
#include "rgb_lcd.h"
#include <Servo.h>
#include <SPI.h>
#include "Ethernet.h"

Servo myservo;                                        //declaro la variable que maneja el servo
rgb_lcd lcd;                                          //declaro la variable que maneja la pantalla
byte mac[] = { 0x98, 0x4F, 0xEE, 0x01, 0x0E, 0xFF };  //declaro y seteo la MAC de la placa Galileo
IPAddress ip(192,168,10,138);                         //declaro y seteo direccion IP de la placa Galileo
EthernetServer server(8080);                          //declaro el puerto que va a ser usado como servidor

  
int colorR = 0;
int colorG = 255;     //color incial = verde
int colorB = 0;
int puntaje = 0;
//int pinPote = 0; 
//int pinBoton = 4;
int pinSensor = 2;
int pinServo = 3;
int posServo=80;
int estadoPIR = LOW;
int val;
boolean flagGol = false;


void setup() {

  Ethernet.begin(mac,ip);   //inicia la conexion ethernet
  server.begin();           //inicia el servidor
  lcd.begin(16, 2);    
  myservo.attach(pinServo);
  myservo.write(posServo);                   // setea ese angulo al servo
  delay(150);                           // delay para que el servo se posicione en el lugar
  lcd.setRGB(colorR, colorG, colorB);
  escribirPuntaje(0);
  Serial.begin(115200);
  delay(800);
  Serial.println();
  Serial.print("El servidor se encuentra en la IP: ");
  Serial.println(ip);
  Serial.print("MAC: ");
//  Serial.println(mac);
}

void loop() {

 /* val = analogRead(pinPote);            // leo el valor del potenciometro (entre 0 y 1023)
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
*/

  String cadena="";
  
  EthernetClient client = server.available();   //escuchar cliente
  if (client)                 //si existe un cliente
  {
    Serial.println("Nuevo cliente.");
    //una http request termina con una linea blanca
    boolean currentLineIsBlank = true;
    while (client.connected()==true)
    {
      if (client.available())
      {
        char c = client.read();
        cadena.concat(c);//Unimos el String 'cadena' con la petición HTTP (c). De esta manera convertimos la petición HTTP a un String        

        //detecto a medida que se va concatenando el string si hubo una entrada de posicion
        detectarEntrada(cadena);    
  
        //si llego al fin de la linea y tengo un enter y la linea es blanca significa q la 
        //request de http termino, entonces envio una respuesta
        if (c == '\n' && currentLineIsBlank) {
          // enviar un encabezado estandar de http
          
         
          if(flagGol)         //si hubo gol le informo al cliente
          { 
            client.println("HTTP/1.1 201");
            flagGol = false;
          }
          else{
            client.println("HTTP/1.1 200");
          } 
          
          client.println("Content-Type: text/plain");
          client.println(puntaje);
          client.println("Connnection: close");
          client.println();
          break;
        }
        if (c == '\n') {
          // se empieza una nueva linea
          currentLineIsBlank = true;
        } 
        else if (c != '\r') {
          // tenes un caracter nuevo en la linea actual
          currentLineIsBlank = false;
        }
      }
    }
    delay(1);
    //cerrar conexion
    client.stop();
    Serial.println("Cliente desconectado");

    //detecto si hubo gol
/*    if(digitalRead(pinSensor) == HIGH)      //este metodo marca doble aveces y genera problemas con la conexion
    {                                         //despues de marcar el gol 
       puntaje++;
       flagGol = true;
       gol();                      //funcion de marcar gol
       escribirPuntaje(puntaje);   //actualizar contador
       delay(2000);                //delay para q no marque doble el gol
    }*/

    val=digitalRead(pinSensor);           //asigno a val la lectura del sensor
    if(val == HIGH)                       //si el sensor esta prendido
    {
      if(estadoPIR == LOW)                //si estaba apagado el sensor significa q es una nueva lectura
      {                                   //marco gol
        puntaje++;
        flagGol = true;
        estadoPIR = HIGH;
        gol();                           //funcion marcar gol
        escribirPuntaje(puntaje);       //actualizar contador
        delay(500);                     
      }
    }
    else                                //si el sensor esta desactivado
      if(estadoPIR == HIGH)             //y el flag decia que estaba activado
        estadoPIR = LOW;                //lo pongo como desactivado
    
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
  lcd.setRGB(255,255,0);  //cambio a amarillo
  delay(300);
  lcd.print("!");
  lcd.setRGB(0,255,255);  //cambio a celeste
  delay(300);
  lcd.print("!");
  lcd.setRGB(0,255,0);    //cambio a verde
  delay(300);
}

/*void moverServo(int dir)    //1 = izquierda ; 2 = derecha
{
  if (dir == 1 && pos > 0)
    posServo = posServo - 5;
  else
    if (dir == 2 && posServo < 160)
      posServo = posServo + 5;
 
  myservo.write(posServo);
  delay(150);
}*/

void detectarEntrada(String cadena)
{
  //Ya que hemos convertido la petición HTTP a una cadena de caracteres, ahora podremos buscar partes del texto
  int posReset = cadena.indexOf("reset=");
  if(posReset != -1 && cadena.length() == posReset + 7) //si encuentra "reset" y el largo de la cadena es justo el que tiene el valor
  {
    String strReset = cadena.substring(posReset+6,posReset+7);  //extraigo el valor del reset (1=reset; 0= nada)
    Serial.println(strReset);
    int valorReset = strReset.toInt();
    if(valorReset)
    {
      Serial.println("Se resetea el contador");
      puntaje=0;
      escribirPuntaje(puntaje);   //reiniciar contador    
      delay(100);
    }
  }

  int posicion=cadena.indexOf("position="); //Guardamos la posición de la instancia "position=" a la variable 'posicion'
  if(posicion != -1 && cadena.length() == posicion + 12)         //si se encuentra el string con los grados y el largo es justo el que tiene el valor
  {
     String grados = cadena.substring(posicion+9,posicion+12);   //extraigo el valor de los grados
     Serial.println(grados);
     posServo = grados.toInt();
     myservo.write(posServo);
     //delay(50);    
  }

}
