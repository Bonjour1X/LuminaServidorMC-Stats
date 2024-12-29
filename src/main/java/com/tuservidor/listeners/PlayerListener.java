package com.tuservidor.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import com.tuservidor.database.Database;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import com.tuservidor.stats.ScoreType;
import org.bukkit.event.entity.EntityDeathEvent;

public class PlayerListener implements Listener {
    private final Database database;

    public PlayerListener(Database database) {
        this.database = database;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        database.updatePlayerStats(player.getUniqueId().toString(),
                player.getName(),
                0, 0);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        // Solo contar si usa herramienta apropiada
        if (isValidTool(player.getInventory().getItemInMainHand(), block)) {
            database.updateScore(player.getUniqueId().toString(), ScoreType.BLOCK_MINED, 1);
        }
    }

    private boolean isValidTool(ItemStack tool, Block block) {
        Material toolType = tool.getType();
        Material blockType = block.getType();

        // Solo contar bloques valiosos
        return (toolType.name().contains("PICKAXE") && isValuableBlock(blockType)) ||
                (toolType.name().contains("AXE") && blockType.name().contains("LOG")) ||
                (toolType.name().contains("SHOVEL") && blockType == Material.DIRT);
    }

    private boolean isValuableBlock(Material block) {
        return block.name().contains("ORE") ||
                block == Material.STONE ||
                block == Material.DEEPSLATE;
    }

    // Para kills de mobs
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        // Verificar si fue matado por un jugador
        if (event.getEntity().getKiller() != null) {
            Player killer = event.getEntity().getKiller();
            // Actualizar puntuación
            database.updateScore(killer.getUniqueId().toString(), ScoreType.KILL, 1);
        }
    }

    // Para kills de jugadores (ya deberías tener algo similar)
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = event.getEntity().getKiller();

        if (killer != null) {
            // Dar puntos al asesino
            database.updateScore(killer.getUniqueId().toString(), ScoreType.KILL, 1);
        }
        // Restar puntos al que muere
        database.updateScore(victim.getUniqueId().toString(), ScoreType.DEATH, 1);
    }
}