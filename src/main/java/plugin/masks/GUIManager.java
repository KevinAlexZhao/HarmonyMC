package plugin.masks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Map;

public class GUIManager implements Listener {

    private static Masks mask;

    public GUIManager(Masks mask) {
        GUIManager.mask = mask;
    }

    public static void openMaskMenu(Player player) {
        String guiTitle = ChatColor.translateAlternateColorCodes('&', "&5&lHarmony&d&lMasks &7| &5Select A Mask");
        Inventory maskMenu = Bukkit.createInventory(null, 27, guiTitle);

        // Add masks from config to the menu
        for (String key : mask.getConfig().getConfigurationSection("masks").getKeys(false)) {
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            String skinOwner = mask.getConfig().getString("masks." + key + ".skin");
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(skinOwner);
            meta.setOwningPlayer(offlinePlayer);
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', mask.getConfig().getString("masks." + key + ".displayName")));
            skull.setItemMeta(meta);
            maskMenu.addItem(skull);
        }
        player.openInventory(maskMenu);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack item = event.getItem();

        // Check if the item is not null and if the action is one of the right-click actions
        if (item != null && (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) {
            // Check if the item is a player head or a helmet
            if (item.getType() == Material.PLAYER_HEAD || item.getType().name().endsWith("_HELMET")) {
                // Get the helmet the player is currently wearing
                ItemStack helmet = player.getInventory().getHelmet();
                // Ensure helmet is not null and has item meta
                if (helmet != null && helmet.hasItemMeta() && helmet.getItemMeta() instanceof SkullMeta) {
                    SkullMeta meta = (SkullMeta) helmet.getItemMeta();
                    // Check if the helmet meta is a mask item
                    if (isMaskItem(meta)) {
                        // Check if the interaction is from the main hand
                        if (event.getHand() == EquipmentSlot.HAND) {
                            event.setCancelled(true);
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lHarmony&d&lMasks &7> &cYou cannot equip a helmet while wearing a mask. Please remove the mask first."));
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        // Handle mask menu interactions
        if (event.getView().getTitle().equals(ChatColor.translateAlternateColorCodes('&', "&5&lHarmony&d&lMasks &7| &5Select A Mask"))) {
            event.setCancelled(true);

            if (clickedItem != null && clickedItem.hasItemMeta()) {
                if (clickedItem.getType() == Material.PLAYER_HEAD) {
                    String displayName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
                    String maskName = null;

                    for (String key : mask.getConfig().getConfigurationSection("masks").getKeys(false)) {
                        String configDisplayName = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', mask.getConfig().getString("masks." + key + ".displayName")));
                        if (configDisplayName.equals(displayName)) {
                            maskName = key;
                            break;
                        }
                    }

                    if (maskName != null) {
                        // Check if the player has the required permission
                        String permissionNode = mask.getConfig().getString("masks." + maskName + ".permission");
                        if (permissionNode == null || player.hasPermission(permissionNode)) {
                            // Check if the player's inventory is full before equipping the mask
                            if (player.getInventory().firstEmpty() == -1 && player.getInventory().getHelmet() != null) {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lHarmony&d&lMasks &7> &cCould not equip mask because inventory is full. Please make space in inventory!"));
                                return;
                            }

                            // Remove current mask effects before applying new ones
                            removeCurrentMaskEffects(player);

                            ConfigurationSection maskSection = mask.getConfig().getConfigurationSection("masks." + maskName);
                            List<Map<?, ?>> effects = maskSection.getMapList("effects");

                            if (effects != null) {
                                for (Map<?, ?> effect : effects) {
                                    String type = (String) effect.get("type");
                                    int duration = (Integer) effect.get("duration");
                                    int amplifier = (Integer) effect.get("amplifier");

                                    PotionEffectType effectType = PotionEffectType.getByName(type.toUpperCase());
                                    if (effectType != null) {
                                        player.addPotionEffect(new PotionEffect(effectType, duration, amplifier));
                                    } else {
                                        player.sendMessage("Invalid potion effect type: " + type);
                                    }
                                }

                                // Store the current helmet if it's not a mask
                                storeCurrentHelmet(player);

                                // Equip the new mask
                                player.getInventory().setHelmet(clickedItem);

                                // Remove the mask from the player's inventory to prevent duplication
                                player.getInventory().remove(clickedItem);

                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lHarmony&d&lMasks &7> " + displayName + " &aequipped!"));
                            } else {
                                player.sendMessage("Could not find effects configuration for: " + displayName);
                            }
                        } else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lHarmony&d&lMasks &7> &cYou do not have permission to equip this mask."));
                        }
                    } else {
                        player.sendMessage("Could not find mask configuration for: " + displayName);
                    }
                } else if (clickedItem.getType() == Material.BARRIER) {
                    // Remove helmet and potion effects
                    player.getInventory().setHelmet(null);
                    removeCurrentMaskEffects(player);

                    // Send message
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lHarmony&d&lMasks &7> &aMask Successfully Removed!"));
                }

                player.closeInventory();
            }
        }

        // Handle manual helmet changes (including pumpkin head and /hat)
        if (event.getSlotType() == InventoryType.SlotType.ARMOR && event.getSlot() == 39) { // Helmet slot
            ItemStack currentItem = event.getCurrentItem();
            ItemStack cursorItem = event.getCursor();

            // Detecting if any block or item is placed on the head
            if (currentItem != null || cursorItem != null) {
                ItemStack helmet = currentItem != null ? currentItem : cursorItem;

                // Remove current mask effects if a mask is being replaced
                ItemStack currentHelmet = player.getInventory().getHelmet();
                if (currentHelmet != null && currentHelmet.hasItemMeta() && currentHelmet.getItemMeta() instanceof SkullMeta) {
                    SkullMeta meta = (SkullMeta) currentHelmet.getItemMeta();
                    if (isMaskItem(meta)) {
                        removeCurrentMaskEffects(player);
                        player.getInventory().setHelmet(null);
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lHarmony&d&lMasks &7> &aMask Successfully Removed!"));
                    }
                }

                // Additional check for carved pumpkin
                if (helmet.getType() == Material.CARVED_PUMPKIN) {
                    removeCurrentMaskEffects(player);
                    purgeMasksFromInventory(player);
                    player.getInventory().setHelmet(null); // This ensures the pumpkin gets placed on the head
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lHarmony&d&lMasks &7> &aMask Successfully Removed!"));
                }
            }
        }
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (message.toLowerCase().startsWith("/hat")) {
            ItemStack currentHelmet = player.getInventory().getHelmet();
            if (currentHelmet != null && currentHelmet.hasItemMeta() && currentHelmet.getItemMeta() instanceof SkullMeta) {
                SkullMeta meta = (SkullMeta) currentHelmet.getItemMeta();
                if (isMaskItem(meta)) {
                    removeCurrentMaskEffects(player);
                    purgeMasksFromInventory(player);
                    player.getInventory().setHelmet(null); // This ensures the new item gets placed on the head
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lHarmony&d&lMasks &7> &aMask Successfully Removed!"));
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        // Do nothing to avoid removing effects when inventory is closed
    }

    @EventHandler
    public void onInventoryClickManualRemoval(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getSlotType() == InventoryType.SlotType.ARMOR && event.getSlot() == 39) { // Helmet slot
            ItemStack currentItem = event.getCurrentItem();
            if (currentItem != null && currentItem.getType() == Material.PLAYER_HEAD) {
                // Check if the current helmet is a mask
                if (isMaskItem((SkullMeta) currentItem.getItemMeta())) {
                    // Remove helmet and potion effects
                    event.setCurrentItem(null);
                    removeCurrentMaskEffects(player);

                    // Send message
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lHarmony&d&lMasks &7> &aMask Successfully Removed!"));
                }
            }
        }
    }

    private void storeCurrentHelmet(Player player) {
        ItemStack currentHelmet = player.getInventory().getHelmet();
        if (currentHelmet != null && currentHelmet.getType() != Material.AIR) {
            ItemMeta meta = currentHelmet.getItemMeta();
            if (!(meta instanceof SkullMeta) || !isMaskItem((SkullMeta) meta)) {
                // Store the current helmet in the player's inventory
                player.getInventory().addItem(currentHelmet);
            }
            // Remove the current helmet
            player.getInventory().setHelmet(null);
        }
    }

    private boolean isMaskItem(SkullMeta skullMeta) {
        if (skullMeta == null) return false;
        for (String key : mask.getConfig().getConfigurationSection("masks").getKeys(false)) {
            String skinOwner = mask.getConfig().getString("masks." + key + ".skin");
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(skinOwner);
            if (skullMeta.getOwningPlayer() != null && skullMeta.getOwningPlayer().equals(offlinePlayer)) {
                return true;
            }
        }
        return false;
    }

    private void removeCurrentMaskEffects(Player player) {
        for (String key : mask.getConfig().getConfigurationSection("masks").getKeys(false)) {
            ConfigurationSection maskSection = mask.getConfig().getConfigurationSection("masks." + key);
            List<Map<?, ?>> effects = maskSection.getMapList("effects");

            if (effects != null) {
                for (Map<?, ?> effect : effects) {
                    String type = (String) effect.get("type");
                    PotionEffectType effectType = PotionEffectType.getByName(type.toUpperCase());
                    if (effectType != null) {
                        player.removePotionEffect(effectType);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClosePreventMaskInInventory(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        purgeMasksFromInventory(player);
    }

    private void purgeMasksFromInventory(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.PLAYER_HEAD) {
                ItemMeta meta = item.getItemMeta();
                if (meta instanceof SkullMeta) {
                    SkullMeta skullMeta = (SkullMeta) meta;
                    if (skullMeta.hasOwner() && isMaskItem(skullMeta)) {
                        player.getInventory().remove(item);
                    }
                }
            }
        }
    }

    private boolean isInventoryFull(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() == Material.AIR) {
                return false;
            }
        }
        return true;
    }
}
