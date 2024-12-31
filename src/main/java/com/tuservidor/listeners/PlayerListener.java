package com.tuservidor.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Boat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.raid.RaidFinishEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.entity.EntityType;

import com.tuservidor.database.Database;
import com.tuservidor.stats.ScoreType;
import org.bukkit.ChatColor;

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
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();

        if (to.getBlock().equals(from.getBlock())) {
            return;
        }

        // Usamos Math.max para asegurar que la distancia no sea negativa
        double distance = Math.max(0.0, from.distance(to));

        // Redondear la distancia a 2 decimales para evitar números muy largos
        distance = Math.round(distance * 100.0) / 100.0;

        if (distance > 0) {  // Solo actualizar si hay movimiento real
            if (player.isSprinting()) {
                database.updateScore(player.getUniqueId().toString(), ScoreType.DISTANCE_SPRINTED, distance);
            } else {
                database.updateScore(player.getUniqueId().toString(), ScoreType.DISTANCE_WALKED, distance);
            }

            if (player.isSwimming()) {
                database.updateScore(player.getUniqueId().toString(), ScoreType.DISTANCE_SWUM, distance);
            }

            Entity vehicle = player.getVehicle();
            if (vehicle instanceof Boat) {
                database.updateScore(player.getUniqueId().toString(), ScoreType.DISTANCE_BOAT, distance);
            } else if (vehicle instanceof AbstractHorse) {
                database.updateScore(player.getUniqueId().toString(), ScoreType.DISTANCE_HORSE, distance);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (isValidTool(player.getInventory().getItemInMainHand(), block)) {
            database.updateScore(player.getUniqueId().toString(), ScoreType.BLOCK_MINED, 1);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Material blockType = event.getBlock().getType();

        database.updateScore(player.getUniqueId().toString(), ScoreType.BLOCKS_PLACED, 1);

    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        ItemStack result = event.getCurrentItem();

        if (result != null) {
            String itemType = result.getType().name();
            if (itemType.contains("SWORD") || itemType.contains("BOW") || itemType.contains("CROSSBOW")) {
                database.updateScore(player.getUniqueId().toString(), ScoreType.WEAPONS_CRAFTED, 1);
            } else if (itemType.contains("PICKAXE") || itemType.contains("AXE") || itemType.contains("SHOVEL")) {
                database.updateScore(player.getUniqueId().toString(), ScoreType.TOOLS_CRAFTED, 1);
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            Player killer = event.getEntity().getKiller();
            if (event.getEntityType().equals(EntityType.PLAYER)) {
                database.updateScore(killer.getUniqueId().toString(), ScoreType.KILL, 1);
            } else if (event.getEntity().isValid()) {
                database.updateScore(killer.getUniqueId().toString(), ScoreType.HOSTILE_KILLS, 1);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = event.getEntity().getKiller();

        if (killer != null) {
            database.updateScore(killer.getUniqueId().toString(), ScoreType.KILL, 1);
            killer.sendMessage(ChatColor.GREEN + "✦ +10 puntos por kill");
        }
        database.updateScore(victim.getUniqueId().toString(), ScoreType.DEATH, 1);
        victim.sendMessage(ChatColor.RED + "✦ -5 puntos por muerte");
    }

    @EventHandler
    public void onRaidFinish(RaidFinishEvent event) {
        for (Player player : event.getWinners()) {
            database.updateScore(player.getUniqueId().toString(), ScoreType.RAIDS_WON, 1);
            player.sendMessage(ChatColor.GREEN + "✦ +15 puntos por ganar raid!");
        }
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player)) return;
        Player player = (Player) event.getEntered();
    }


    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            database.updateScore(player.getUniqueId().toString(), ScoreType.DAMAGE_DEALT, (int)event.getFinalDamage());
        }


    }

    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        Player player = event.getEnchanter();
        database.updateScore(player.getUniqueId().toString(), ScoreType.ITEMS_ENCHANTED, 1);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (event.getInventory().getType() != InventoryType.MERCHANT) return;

        Player player = (Player) event.getWhoClicked();
        if (event.getCurrentItem() != null) {
            database.updateScore(player.getUniqueId().toString(), ScoreType.VILLAGER_TRADES, 1);
        }
    }


    private boolean isValidTool(ItemStack tool, Block block) {
        Material toolType = tool.getType();
        Material blockType = block.getType();

        return (toolType.name().contains("PICKAXE") && blockType.name().contains("STONE")) ||
                (toolType.name().contains("AXE") && blockType.name().contains("LOG")) ||
                (toolType.name().contains("SHOVEL") && blockType == Material.DIRT);
    }

    private boolean isValuableBlock(Material block) {
        return block.name().contains("ORE") ||
                block == Material.STONE ||
                block == Material.DEEPSLATE;
    }

    private boolean hasTrimmededArmor(Player player) {
        for (ItemStack item : player.getInventory().getArmorContents()) {
            if (item != null && item.hasItemMeta() && item.getItemMeta().hasCustomModelData()) {
                return true;
            }
        }
        return false;
    }
}