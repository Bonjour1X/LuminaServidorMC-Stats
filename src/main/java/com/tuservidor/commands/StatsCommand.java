package com.tuservidor.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.tuservidor.database.Database;

public class StatsCommand implements CommandExecutor {
    private final Database database;

    public StatsCommand(Database database) {
        this.database = database;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser usado por jugadores");
            return true;
        }

        Player player = (Player) sender;

        player.sendMessage(ChatColor.GOLD + "=== Tus Estadísticas ===");
        player.sendMessage(ChatColor.YELLOW + "Nombre: " + player.getName());
        player.sendMessage(ChatColor.YELLOW + "Nivel: " + player.getLevel());
        player.sendMessage(ChatColor.YELLOW + "Vida: " + (int)player.getHealth() + "/20");
        player.sendMessage(ChatColor.YELLOW + "XP: " + player.getExp());
        player.sendMessage(ChatColor.GOLD + "Puntos: " + database.getPlayerPoints(player.getUniqueId().toString()));
        player.sendMessage(ChatColor.YELLOW + "Kills: " + database.getPlayerKills(player.getUniqueId().toString()));
        player.sendMessage(ChatColor.YELLOW + "Muertes: " + database.getPlayerDeaths(player.getUniqueId().toString()));
        player.sendMessage(ChatColor.YELLOW + "Bloques Minados: " + database.getPlayerBlocksMined(player.getUniqueId().toString()));
        player.sendMessage(ChatColor.YELLOW + "Tiempo Jugado: " +
                formatTime(database.getPlayerPlaytime(player.getUniqueId().toString())));
        return true;
    }

    private String formatPlaytime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        return String.format("%dh %dm", hours, minutes);
    }

    private String formatTime(long minutes) {  // Cambiar de int a long
        long hours = minutes / 60;
        long mins = minutes % 60;
        return String.format("%dh %dm", hours, mins);
    }
}