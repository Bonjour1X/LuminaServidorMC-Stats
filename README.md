# Sistema de Estadísticas para Servidor Minecraft

Sistema de estadísticas que permite trackear jugadores y equipos en un servidor de Minecraft, incluyendo sistema de puntuación, rankings y gestión de equipos.

## Características
- 📊 Estadísticas individuales (kills, muertes, bloques minados)
- 👥 Sistema de equipos
- 🏆 Rankings de jugadores y equipos
- 💬 Chat de equipo
- ⭐ Sistema de puntuación en tiempo real

## Sistema de Puntuación
- Kills: +10 puntos
- Bloques minados: +1 punto
- Muertes: -5 puntos

## Comandos Disponibles
- `/stats` - Ver tus estadísticas
- `/team create <nombre>` - Crear equipo
- `/team join <nombre>` - Unirse a equipo
- `/team leave` - Salir del equipo
- `/team delete` - Eliminar equipo (solo líder)
- `/team info` - Ver info del equipo
- `/top players [cantidad]` - Ver ranking de jugadores
- `/top teams [cantidad]` - Ver ranking de equipos
- `/teammsg <mensaje>` - Enviar mensaje al equipo
- `/guide` - Ver sistema de puntuación
- `/winner` - Ver equipo ganador actual

## Requisitos
- Java 17 o superior
- Servidor Minecraft 1.20.1 (Paper/Spigot)
- 1GB RAM mínimo recomendado

## Instalación

### Para Jugadores
1. Descarga el archivo `estadisticas-1.0-SNAPSHOT.jar`
2. Colócalo en la carpeta `plugins` de tu servidor
3. Reinicia el servidor

### Para Desarrolladores
1. Clona el repositorio
2. Importa el proyecto en IntelliJ IDEA
3. Asegúrate de tener Maven configurado
4. Ejecuta `mvn clean package`

### Configuración del Servidor de Prueba
1. Crear una carpeta para el servidor
```
TestServer/
├── plugins/
├── server.jar     # Paper/Spigot 1.20.1
└── start.bat
```

2. Contenido de start.bat:
```batch
java -Xmx1G -jar server.jar nogui
pause
```

3. Primera Ejecución:
- Ejecuta start.bat
- Acepta el eula.txt cambiando `eula=false` a `eula=true`
- Reinicia el servidor

## Base de Datos
El plugin utiliza SQLite para almacenar:
- Estadísticas de jugadores
- Información de equipos
- Rankings y puntuaciones

La base de datos se crea automáticamente en:
`plugins/EstadisticasServer/stats.db`

## Desarrollado con
- Java
- Maven
- Spigot API
- SQLite

## Contribuir
1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## Soporte
Si encuentras algún bug o tienes sugerencias, por favor abre un issue en este repositorio.

## Licencia
Distribuido bajo la Licencia MIT. Ver `LICENSE` para más información.
```
holas 
