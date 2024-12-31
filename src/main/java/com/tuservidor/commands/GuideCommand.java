package com.tuservidor.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GuideCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.GOLD + "=== Sistema de Puntuación ===");
        sender.sendMessage(ChatColor.YELLOW + "Combate:");
        sender.sendMessage(ChatColor.WHITE + "• Kill PvP: +10 puntos");
        sender.sendMessage(ChatColor.WHITE + "• Muerte: -5 puntos");
        sender.sendMessage(ChatColor.WHITE + "• Mob hostil: +5 puntos");

        sender.sendMessage(ChatColor.YELLOW + "\nRecursos:");
        sender.sendMessage(ChatColor.WHITE + "• Bloque minado: +1 punto");
        sender.sendMessage(ChatColor.WHITE + "• Bloque colocado: +0.5 puntos");
        sender.sendMessage(ChatColor.WHITE + "• Item encantado: +5 puntos");

        sender.sendMessage(ChatColor.YELLOW + "\nActividades:");
        sender.sendMessage(ChatColor.WHITE + "• Raid ganada: +15 puntos");
        sender.sendMessage(ChatColor.WHITE + "• Comercio aldeano: +2 puntos");
        sender.sendMessage(ChatColor.WHITE + "• Animal criado: +3 puntos");

        sender.sendMessage(ChatColor.YELLOW + "\nMovimiento:");
        sender.sendMessage(ChatColor.WHITE + "• Correr: +0.02 por bloque");
        sender.sendMessage(ChatColor.WHITE + "• Nadar: +0.03 por bloque");
        sender.sendMessage(ChatColor.WHITE + "• Montar: +0.02-0.03 por bloque");

        return true;
    }
}