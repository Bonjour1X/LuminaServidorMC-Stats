# Sistema de Estadísticas para Servidor Minecraft
Sistema de estadísticas que permite trackear jugadores y equipos en un servidor de Minecraft, incluyendo sistema de puntuación, rankings y gestión de equipos.

## Características
- 📊 Estadísticas individuales detalladas
- 👥 Sistema de equipos (administrado por staff)
- 🏆 Rankings de jugadores y equipos
- ⭐ Sistema de puntuación en tiempo real
- 🔍 Tracking de IP de jugadores

## Sistema de Puntuación
### Movimiento
- Caminar: +0.01 puntos/bloque
- Correr: +0.02 puntos/bloque
- Nadar: +0.03 puntos/bloque
- Bote: +0.02 puntos/bloque
- Caballo: +0.03 puntos/bloque

### Combate y Recursos
- Kills: +10 puntos
- Muertes: -5 puntos
- Bloques minados: +1 punto (requiere herramienta correcta)
- Bloques colocados: +0.5 puntos

### Tiempo
- Tiempo jugado: +0.1 puntos/minuto

## Comandos Disponibles
### Para Jugadores
- `/stats` - Ver tus estadísticas
- `/top players [cantidad]` - Ver ranking de jugadores
- `/top teams [cantidad]` - Ver ranking de equipos
- `/guide` - Ver sistema de puntuación

### Para Staff
- `/team create <nombre>` - Crear equipo
- `/team delete <nombre>` - Eliminar equipo
- `/team add <jugador>` - Añadir jugador a equipo
- `/team remove <jugador>` - Remover jugador de equipo

## Base de Datos
El plugin utiliza SQLite para almacenar:
- Estadísticas de jugadores
- Información de equipos y miembros
- Rankings y puntuaciones
- IPs de jugadores

La base de datos se crea automáticamente en:
`plugins/EstadisticasServer/stats.db`

## Requisitos
- Java 17 o superior
- Servidor Minecraft 1.20.1 (Paper/Spigot)
- 1GB RAM mínimo recomendado

## Instalación
1. Descarga el archivo .jar
2. Colócalo en la carpeta `plugins` de tu servidor
3. Reinicia el servidor

## Desarrollado con
- Java
- Maven
- Spigot API
- SQLite

## Licencia
Distribuido bajo la Licencia MIT. Ver `LICENSE` para más información.
```
holas 
