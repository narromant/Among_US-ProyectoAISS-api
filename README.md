# AMONG US. NO MORE IMPOSTORS

Proyecto de AISS creado por Alejandro Mateo Capilla, Ignacio Arroyo Mantero y Tadeo Cabrera Gómez

Among US es una API de gestión de datos de usuario especializada en la seguridad y administración de los mismos mediante el uso de contraseñas de usuario, tokens de identificación y almacenamiento de datos de usuario.

La API REST estará formada por tres recursos que permitirán manipular grupos de usuarios, tokens de usuarios, y a los mismos usuarios. 

### Recurso User ###
| HTTP  | URI | Descripción |
| ------------- | ------------- | ------------- |
| GET |  /user | Devuelve todos los usuarios de la aplicación. Es posible filtrar los usuarios devueltos de dos maneras, con el parámetro de query Boolean *“onlyAdmins”*, que devuelve aquellos usuarios con un rol de ADMIN (role=1) o de OWNER (role=2), o con el parametro de query String *"name"*, que solo devuelve el usuario que tenga el nombre dado, en caso de que exista. **No se pueden usar los dos filtros a la vez**|
| GET | /song/{songId}  |  Devuelve la canción con id=songId. Si la canción no existe devuelve un “404 Not Found”. |
| POST | /song | Añade una nueva canción cuyos datos se pasan en el cuerpo de la petición en formato JSON (no se debe pasar id, se genera automáticamente). Si el nombre de la canción no es válido (null o vacío) devuelve un error “400 Bad Request”. Si se añade satisfactoriamente, devuelve “201 Created” con la referencia a la URI y el contenido de la canción. |
| PUT | /song  | Actualiza la canción cuyos datos se pasan en el cuerpo de la petición en formato JSON (deben incluir el id de la canción). Si la canción no existe, devuelve un “404 Not Found”. Si se realiza correctamente, devuelve “204 No Content”. |
| DELETE | /song/{songId}  |  Elimina la canción con id=songId. Si la canción no existe, devuelve un “404 Not Found”. Si se realiza correctamente, devuelve “204 No Content”.|

Cada **canción** tiene un identificador, _título, nombre del artista, álbum y año_. La representación JSON del recurso es:

```cpp
{
	"id":"s3",
	"title":"Smell Like Teen Spirit",
	"artist":"Nirvana",
	"album":"Nevermind",
	"year":"1991"
}
```


### Recurso Playlist ###
| HTTP  | URI | Descripción |
| ------------- | ------------- | ------------- |
| GET | /lists  | Ver todas las listas de reproducción existentes. •	Es posible ordenar las listas por nombre con el parámetro de query “order”, que solo acepta dos valores, “name” o “-name”. •	También es posible filtrar las listas devueltas con dos parámetros de query: “isEmpty”, que devuelve listas sin canciones si vale “true” o listas con canciones si vale “false”; “name”, que devuelve las listas cuyo nombre coincida exactamente con el valor del parámetro. |
| GET | /lists/{playlistId} | Devuelve la lista con id=playlistId. Si la lista no existe devuelve un “404 Not Found”. |
| POST | /lists | Añadir una nueva lista de reproducción. Los datos de la lista (nombre y descripción) se proporcionan en el cuerpo de la petición en formato JSON. Las canciones de la lista no se pueden incluir aquí, para ello se debe usar  la operación POST específica para añadir una canción a una lista (a continuación). Si el nombre de la lista no es válido (nulo o vacío), o se intenta crear una lista con canciones, devuelve un error “400 Bad Request”. Si se añade satisfactoriamente, devuelve “201 Created” con la referencia a la URI y el contenido de la lista. |
| PUT | /lists | Actualiza la lista cuyos datos se pasan en el cuerpo de la petición en formato JSON (deben incluir el id de la lista).  Si la lista no existe, devuelve un “404 Not Found”. Si se intenta actualizar las canciones de la lista, devuelve un error “400 Bad Request”. Para actualizar las canciones se debe usar el recurso Song mostrado previamente. Si se realiza correctamente, devuelve “204 No Content”. |
| DELETE | /lists/{playlistId} | Elimina la lista con id=playlistId. Si la lista no existe, devuelve un “404 Not Found”. Si se realiza correctamente, devuelve “204 No Content”. |
| POST |  /lists/{playlistId}/{songId} | Añade la canción con id=songId a la lista con id=playlistId. Si la lista o la canción no existe, devuelve un “404 Not Found”. Si la canción ya está incluida en la lista devuelve un “400 Bad Request”. Si se añade satisfactoriamente, devuelve “201 Created” con la referencia a la URI y el contenido de la lista. |
| DELETE | /lists/{playlistId}/{songId}  | Elimina la canción con id=songId de la lista con id=playlistId. Si la lista o la canción no existe, devuelve un “404 Not Found”. Si se realiza correctamente, devuelve “204 No Content”.|


Una **lista de reproducción** tiene un _identificador, nombre, descripción y un conjunto de canciones_. La representación JSON de este recurso es:

```cpp
{
	"id":"p5",
	"name":"AISSPlayList",
	"description":"AISS PlayList",
	"songs":[
		{
			"id":"s0",
			"title":"Rolling in the Deep",
			"artist":"Adele",
			"album":"21",
			"year":"2011"
		},

		{			
			"id":"s1",
			"title":"One",
			"artist":"U2",
			"album":"Achtung Baby",
			"year":"1992"
		}
		]
}

```
