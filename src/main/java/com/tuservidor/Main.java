package com.tuservidor;

import com.tuservidor.commands.*;
import com.tuservidor.stats.ScoreType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import com.tuservidor.database.Database;
import com.tuservidor.listeners.PlayerListener;


public class Main extends JavaPlugin {
    private Database database;

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

        // Timer para tiempo de juego (cada minuto)
        // Cambiar el timer para que solo actualice el tiempo sin dar puntos
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                database.updatePlaytime(player.getUniqueId().toString());
            }
        }, 1200L, 1200L); // 1200 ticks = 1 minuto

        getLogger().info("Plugin de estadísticas activado!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin de estadísticas desactivado!");
    }
}