# JCS by Herzank el monge.686
Libreria para facilitar el almacenamientos de objetos java en sqlite

*Genera tablas automaticamente en caso de que no existan, las tablas se deberan llamar conforme al nombre en plural de la clase
*Usa java reflective para crear las columnas de las tablas conforme a los campos de la clase
*variables del tipo String, Date y ArrayList se guardan en TEXTO, 
*los arraylist se guardan en formato TEXTO Json.
*int como INTEGER
*double como REAL

1 Agrega la dependencia a tu proyecto maven

<dependency>
    <groupId>com.monge</groupId>
    <artifactId>JCS</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>

2 crea una instancia de la base de datos e ingresa el nombre de tu base de datos

DataBase db = new DataBase();
        db.setFileName("bd.sqlite");


       
3 Extiendes las clases que deseas almacenar en la base de datos
  public class Usuario extends Storable{}

  Ejemplos de uso

  usuario.insertInto(); /*Almacena el objeto en la base de datos*/

  usuario.update() /*actualiza el objeto en la base de datos, el primer campo del objeto se usa como identificador o primary key*/

  usuario.select(clazz, propertyName, value) /*Es equivalente a select where, regresando un array de objetos que cumplan la condicion*/

  
