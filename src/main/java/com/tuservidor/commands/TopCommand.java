package com.tuservidor.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import com.tuservidor.database.Database;
import java.util.Map;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.sql.ResultSet;

public class TopCommand implements CommandExecutor {
    private final Database database;

    public TopCommand(Database database) {
        this.database = database;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Uso: /top <players|teams> [cantidad]");
            return true;
        }

        // Determinar cantidad (por defecto 5)
        int limit = 5;
        if (args.length >= 2) {
            try {
                limit = Integer.parseInt(args[1]);
                if (limit < 1) limit = 5;
                if (limit > 50) limit = 50; // Máximo 50 para evitar spam
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Cantidad inválida. Usando 5 por defecto.");
            }
        }

        switch (args[0].toLowerCase()) {
            case "players":
                showTopPlayers(sender, limit);
                break;
            case "teams":
                showTopTeams(sender, limit);
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Uso: /top <players|teams> [cantidad]");
        }

        return true;
    }

    private void showTopPlayers(CommandSender sender, int limit) {
        sender.sendMessage(ChatColor.GOLD + "=== Top " + limit + " Jugadores ===");
        var topPlayers = database.getTopPlayers(limit);
        int rank = 1;
        for (Map<String, Object> player : topPlayers) {
            sender.sendMessage(ChatColor.YELLOW + "#" + rank + ". " +
                    player.get("name") + " - Puntos: " + player.get("score") +
                    " (Kills: " + player.get("kills") + ")");
            rank++;
        }
    }

    private void showTopTeams(CommandSender sender, int limit) {
        sender.sendMessage(ChatColor.GOLD + "=== Top " + limit + " Equipos ===");
        var topTeams = database.getTopTeams(limit);
        int rank = 1;
        for (Map<String, Object> team : topTeams) {
            sender.sendMessage(ChatColor.YELLOW + "#" + rank + ". " +
                    team.get("name") + " - Puntos: " + team.get("score") +
                    " (Miembros: " + team.get("members") + ")");
            rank++;
        }
    }
}

