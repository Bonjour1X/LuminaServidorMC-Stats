package com.tuservidor.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import com.tuservidor.database.Database;

import java.util.Map;

public class WinnerCommand implements CommandExecutor {
    private final Database database;

    public WinnerCommand(Database database) {
        this.database = database;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!database.hasTeams()) {
            sender.sendMessage(ChatColor.RED + "No hay equipos registrados");
            return true;
        }

        Map<String, Object> winner = database.getWinningTeam();
        if (winner != null) {
            sender.sendMessage(ChatColor.GOLD + "=== Equipo Líder ===");
            sender.sendMessage(ChatColor.YELLOW + "Equipo: " + winner.get("name"));
            sender.sendMessage(ChatColor.YELLOW + "Puntos: " + winner.get("total_points"));
            sender.sendMessage(ChatColor.YELLOW + "Miembros: " + winner.get("members"));
            sender.sendMessage(ChatColor.YELLOW + "Kills: " + winner.get("total_kills"));
        } else {
            sender.sendMessage(ChatColor.RED + "No hay equipos registrados aún");
        }
        return true;
    }
}