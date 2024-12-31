package com.tuservidor.stats;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.tuservidor.database.Database;
import org.bukkit.plugin.Plugin;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import java.util.HashMap;
import java.util.Map;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class StatsWebServer {
    private final Database database;
    private final Plugin plugin;
    private HttpServer server;
    private final int port;

    public StatsWebServer(Plugin plugin, Database database, int port) {
        this.plugin = plugin;
        this.database = database;
        this.port = port;
    }

    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/stats", new StatsHandler());
            server.setExecutor(null);
            server.start();
            plugin.getLogger().info("Servidor web de estadísticas iniciado en el puerto " + port);
        } catch (IOException e) {
            plugin.getLogger().warning("Error al iniciar el servidor web: " + e.getMessage());
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    private class StatsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                Map<String, Object> response = new HashMap<>();
                response.put("topPlayers", database.getTopPlayers(10));
                response.put("topTeams", database.getTopTeams(5));

                String jsonResponse = convertMapToJson(response);
                byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);

                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.sendResponseHeaders(200, responseBytes.length);

                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(responseBytes);
                }
            }
        }

        private String convertMapToJson(Map<String, Object> map) {
            StringBuilder json = new StringBuilder("{");
            boolean first = true;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (!first) {
                    json.append(",");
                }
                first = false;
                json.append("\"").append(entry.getKey()).append("\":");
                if (entry.getValue() instanceof String) {
                    json.append("\"").append(entry.getValue()).append("\"");
                } else {
                    json.append(entry.getValue());
                }
            }
            json.append("}");
            return json.toString();
        }
    }
}