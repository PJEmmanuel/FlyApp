# FlyApp - Aplicación de Búsqueda de Vuelos

FlyApp es una aplicación para Android que permite a los usuarios buscar vuelos desde un aeropuerto de salida, seleccionar sus vuelos favoritos y almacenar información de búsqueda de manera persistente. Utiliza **Room Database**, **Preferences DataStore** y sigue el patrón de arquitectura **MVVM** con principios de **Clean Architecture**.

## Características
- Solicita al usuario un aeropuerto de salida.
- Proporciona sugerencias de autocompletado con base en una base de datos local.
- Muestra una lista de vuelos disponibles desde el aeropuerto seleccionado, con el identificador IATA y el nombre del aeropuerto.
- Permite al usuario guardar vuelos individuales como favoritos.
- Muestra una lista de rutas favoritas cuando no hay búsqueda activa.
- Guarda el texto de búsqueda en **Preferences DataStore** para que persista tras cerrar la aplicación.
- Utiliza **Room** para gestionar la base de datos de vuelos de manera eficiente.
- Implementa pruebas de integración en la base de datos.

## Tecnologías utilizadas
- **Kotlin** como lenguaje principal.
- **Room Database** para la gestión de datos locales.
- **Preferences DataStore** para almacenamiento de preferencias.
- **Clean Architecture** y **MVVM** para un código modular y mantenible.

## Uso
1. Ingresa el nombre o código IATA de un aeropuerto en el campo de búsqueda.
2. Selecciona una de las sugerencias de autocompletado.
3. Revisa la lista de vuelos disponibles desde ese aeropuerto.
4. Guarda tus vuelos favoritos para acceder a ellos fácilmente en el futuro.

## Fuente de inspiración

Este proyecto se basa en el siguiente codelab de Android Developers:
[Flight Search App - Android Basics with Compose](https://developer.android.com/courses/pathways/android-basics-compose-unit-6-pathway-3?hl=es-419#codelab-https://developer.android.com/codelabs/basic-android-kotlin-compose-flight-search)
