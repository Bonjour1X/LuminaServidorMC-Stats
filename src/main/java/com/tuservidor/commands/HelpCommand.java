package com.tuservidor.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HelpCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.GOLD + "=== Comandos Disponibles ===");
        sender.sendMessage(ChatColor.YELLOW + "/stats - Ver tus estadísticas");
        sender.sendMessage(ChatColor.YELLOW + "/team create <nombre> - Crear equipo");
        sender.sendMessage(ChatColor.YELLOW + "/team join <nombre> - Unirse a equipo");
        sender.sendMessage(ChatColor.YELLOW + "/team leave - Salir del equipo");
        sender.sendMessage(ChatColor.YELLOW + "/team delete - Eliminar equipo (solo líder)");
        sender.sendMessage(ChatColor.YELLOW + "/team info - Ver info del equipo");
        sender.sendMessage(ChatColor.YELLOW + "/top players [cantidad] - Ver ranking de jugadores");
        sender.sendMessage(ChatColor.YELLOW + "/top teams [cantidad] - Ver ranking de equipos");
        sender.sendMessage(ChatColor.YELLOW + "/teammsg <mensaje> - Enviar mensaje al equipo");
        sender.sendMessage(ChatColor.YELLOW + "/guide - Ver sistema de puntuación");
        sender.sendMessage(ChatColor.YELLOW + "/winner - Ver equipo ganador actual");
        return true;
    }
}