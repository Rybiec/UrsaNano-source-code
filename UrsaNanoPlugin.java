//There is source code:

package com.example;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class UrsaNanoPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Register events
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("UrsaNanoPlugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("UrsaNanoPlugin disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            // Create a black candle
            ItemStack ursaNano = new ItemStack(Material.BLACK_CANDLE);
            ItemMeta meta = ursaNano.getItemMeta();
            meta.setDisplayName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Ursa Nano");

            // Add lore with the number of uses
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Remaining uses: 500");
            meta.setLore(lore);

            // Add unique value to the item to track its usage
            NamespacedKey key = new NamespacedKey(this, "usage_count");
            meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 500);
            ursaNano.setItemMeta(meta);

            // Give the item to the player
            player.getInventory().addItem(ursaNano);
            player.sendMessage(ChatColor.GREEN + "You received Ursa Nano!");

            return true;
        }
        return false;
    }

    // Event that handles right-click action
    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        if (event.getItem() != null && event.getItem().getType() == Material.BLACK_CANDLE) {
            ItemStack item = event.getItem();
            ItemMeta meta = item.getItemMeta();
            NamespacedKey key = new NamespacedKey(this, "usage_count");

            PersistentDataContainer data = meta.getPersistentDataContainer();
            if (data.has(key, PersistentDataType.INTEGER)) {
                int usesLeft = data.get(key, PersistentDataType.INTEGER);
                usesLeft--;

                if (usesLeft > 0) {
                    data.set(key, PersistentDataType.INTEGER, usesLeft);
                    item.setItemMeta(meta);

                    // Update lore with the remaining uses
                    List<String> lore = meta.getLore();
                    if (lore != null) {
                        lore.set(0, ChatColor.GRAY + "Remaining uses: " + usesLeft);
                        meta.setLore(lore);
                        item.setItemMeta(meta);
                    }

                    // Add particle effect in front of the player
                    showParticleCloud(event.getPlayer());

                } else {
                    event.getPlayer().getInventory().remove(item);
                    event.getPlayer().sendMessage(ChatColor.RED + "The coil burned out!");
                }
            }
        }
    }

    // Method to display a particle cloud in front of the player
    private void showParticleCloud(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                long duration = 2000; // 2 seconds

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        long elapsedTime = System.currentTimeMillis() - startTime;

                        if (elapsedTime >= duration) {
                            this.cancel(); // Stop the task after 2 seconds
                            return;
                        }

                        // Set the dimensions of the cloud
                        double length = 3;
                        double heightWidth = 0.3;

                        // Get the player's facing direction
                        Vector direction = player.getLocation().getDirection().normalize();

                        // Get the player's eye location
                        Location eyeLocation = player.getEyeLocation();

                        // Generate particles in a rectangular shape in front of the player
                        for (double x = -length / 2; x <= length / 2; x += 0.3) {
                            for (double y = -heightWidth / 2; y <= heightWidth / 2; y += 0.3) {
                                for (double z = -heightWidth / 2; z <= heightWidth / 2; z += 0.3) {
                                    // Shift particles in the player's facing direction
                                    Vector offset = direction.clone().multiply(x).add(new Vector(0, y, 0)).add(direction.clone().crossProduct(new Vector(0, 1, 0)).multiply(z));
                                    Location particleLocation = eyeLocation.clone().add(direction.clone().multiply(1).add(offset));
                                    player.getWorld().spawnParticle(Particle.CLOUD, particleLocation, 1, 0, 0, 0, 0.1);
                                }
                            }
                        }
                    }
                }.runTaskTimer(UrsaNanoPlugin.this, 0, 1); // Run every tick
            }
        }.runTask(UrsaNanoPlugin.this); // Start after 0 ticks
    }
}










//Ryba
