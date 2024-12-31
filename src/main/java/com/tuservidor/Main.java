package com.tuservidor;

import com.tuservidor.commands.*;
import com.tuservidor.stats.ScoreType;
import com.tuservidor.stats.StatsWebServer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import com.tuservidor.database.Database;
import com.tuservidor.listeners.PlayerListener;
import org.bukkit.ChatColor;

public class Main extends JavaPlugin {
    private Database database;
    private StatsWebServer statsServer;

    @Override
    public void onEnable() {
        // Inicializar base de datos
        database = new Database(getDataFolder() + "/stats.db");
        database.connect();

        // Registrar eventos
        PlayerListener playerListener = new PlayerListener(database);
        getServer().getPluginManager().registerEvents(playerListener, this);

        // Registrar comandos
        getCommand("stats").setExecutor(new StatsCommand(database));
        getCommand("team").setExecutor(new TeamCommand(database));
        getCommand("top").setExecutor(new TopCommand(database));
        getCommand("help").setExecutor(new HelpCommand());
        getCommand("teammsg").setExecutor(new TeamMessageCommand(database));
        getCommand("winner").setExecutor(new WinnerCommand(database));
        getCommand("guide").setExecutor(new GuideCommand());

        // Iniciar servidor web de estadísticas
        statsServer = new StatsWebServer(this, database, 8080);
        statsServer.start();

        // Timer para tiempo de juego
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                database.updatePlaytime(player.getUniqueId().toString());
                database.updatePlayerIP(player.getUniqueId().toString(),
                        player.getAddress().getAddress().getHostAddress());
            }
        }, 1200L, 1200L);

        // En el método onEnable()
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                String uuid = player.getUniqueId().toString();
                int pointsGained = database.getPointsGainedLastMinute(uuid);
                if (pointsGained != 0) {
                    player.sendMessage(ChatColor.GOLD + "=== Resumen de Puntos ===");
                    player.sendMessage(ChatColor.YELLOW + "Puntos ganados: " +
                            (pointsGained > 0 ? ChatColor.GREEN : ChatColor.RED) +
                            pointsGained);
                }
            }
        }, 1200L, 1200L); // Cada minuto

        getLogger().info("Plugin de estadísticas activado!");
    }

    @Override
    public void onDisable() {
        if (statsServer != null) {
            statsServer.stop();
        }
        getLogger().info("Plugin de estadísticas desactivado!");
    }

}