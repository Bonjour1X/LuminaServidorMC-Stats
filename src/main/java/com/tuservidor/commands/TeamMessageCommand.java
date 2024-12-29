package com.tuservidor.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.tuservidor.database.Database;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

public class TeamMessageCommand implements CommandExecutor {
    private final Database database;

    public TeamMessageCommand(Database database) {
        this.database = database;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser usado por jugadores");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Uso: /teammsg <mensaje>");
            return true;
        }

        Player player = (Player) sender;
        String message = String.join(" ", args);
        sendTeamMessage(player, message);

        return true;
    }

    private void sendTeamMessage(Player sender, String message) {
        String teamName = database.getPlayerTeam(sender.getUniqueId().toString());
        if (teamName == null) {
            sender.sendMessage(ChatColor.RED + "No estás en ningún equipo");
            return;
        }

        String formattedMessage = String.format("%s[Team] %s%s: %s",
                ChatColor.GREEN,
                ChatColor.YELLOW,
                sender.getName(),
                message);

        // Enviar mensaje a cada miembro una sola vez
        database.getTeamMembers(teamName).stream()
                .map(memberId -> sender.getServer().getPlayer(UUID.fromString(memberId)))
                .filter(player -> player != null)
                .distinct() // Elimina duplicados
                .forEach(player -> player.sendMessage(formattedMessage));
    }
}