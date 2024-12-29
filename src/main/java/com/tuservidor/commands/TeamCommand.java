package com.tuservidor.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.tuservidor.database.Database;

public class TeamCommand implements CommandExecutor {
    private final Database database;

    public TeamCommand(Database database) {
        this.database = database;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Este comando solo puede ser usado por jugadores");
            return true;
        }

        Player player = (Player) sender;
        String playerId = player.getUniqueId().toString();

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Uso: /team <create|join|leave|delete|info>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Uso: /team create <nombre>");
                    return true;
                }
                if (database.hasTeam(playerId)) {
                    sender.sendMessage(ChatColor.RED + "Ya perteneces a un equipo. Debes salir primero.");
                    return true;
                }
                database.createTeam(args[1], playerId);
                player.sendMessage(ChatColor.GREEN + "Equipo creado: " + args[1]);
                break;

            case "delete":
                if (!database.isTeamOwner(playerId)) {
                    player.sendMessage(ChatColor.RED + "Solo el creador puede eliminar el equipo");
                    return true;
                }
                String teamName = database.getPlayerTeam(playerId);
                database.deleteTeam(playerId);
                player.sendMessage(ChatColor.GREEN + "Equipo " + teamName + " eliminado");
                break;

            case "leave":
                if (!database.hasTeam(playerId)) {
                    player.sendMessage(ChatColor.RED + "No estás en ningún equipo");
                    return true;
                }
                teamName = database.getPlayerTeam(playerId);
                database.leaveTeam(playerId);
                player.sendMessage(ChatColor.GREEN + "Has abandonado el equipo: " + teamName);

                // Si no quedan miembros, borrar el equipo
                if (database.getTeamMemberCount(teamName) == 0) {
                    database.deleteTeamByName(teamName);
                }
                break;

            case "join":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Uso: /team join <nombre>");
                    return true;
                }
                if (!database.teamExists(args[1])) {
                    sender.sendMessage(ChatColor.RED + "El equipo no existe");
                    return true;
                }
                database.joinTeam(args[1], player.getUniqueId().toString());
                player.sendMessage(ChatColor.GREEN + "Te has unido al equipo: " + args[1]);
                break;

            case "info":
                String info = database.getTeamInfo(player.getUniqueId().toString());
                player.sendMessage(info);
                break;
        }

        return true;
    }
}