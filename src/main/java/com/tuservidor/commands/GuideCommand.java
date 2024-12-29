package com.tuservidor.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GuideCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.GOLD + "=== Guía de Puntuación ===");
        sender.sendMessage(ChatColor.YELLOW + "• Kills (mobs/jugadores): +10 puntos");
        sender.sendMessage(ChatColor.YELLOW + "• Muertes: -5 puntos");
        sender.sendMessage(ChatColor.YELLOW + "• Bloques minados: +1 punto (necesitas pico/hacha)");
        sender.sendMessage(ChatColor.YELLOW + "• Logros: +15 puntos");
        sender.sendMessage(ChatColor.YELLOW + "• Tiempo jugado: +1 punto/minuto");
        sender.sendMessage(ChatColor.GOLD + "=== Logros que dan puntos ===");
        sender.sendMessage(ChatColor.YELLOW + "• Obtener materiales");
        sender.sendMessage(ChatColor.YELLOW + "• Craftear herramientas");
        sender.sendMessage(ChatColor.YELLOW + "• Matar mobs");
        sender.sendMessage(ChatColor.YELLOW + "• Explorar biomas");
        return true;
    }
}
