package plugin.masks;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GUIManager implements Listener {

    private static Masks mask;

    public GUIManager(Masks mask) {
        GUIManager.mask = mask;
    }

    public static void openMaskMenu(Player player) {
        Inventory maskMenu = Bukkit.createInventory(null, 27, "Select a Mask");

        // Add masks from config to the menu
        for (String key : mask.getConfig().getConfigurationSection("masks").getKeys(false)) {
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            meta.setOwner(mask.getConfig().getString("masks." + key + ".skin"));
            meta.setDisplayName(key);
            skull.setItemMeta(meta);
            maskMenu.addItem(skull);
        }

        // Add remove mask option
        ItemStack removeMask = new ItemStack(Material.BARRIER);
        ItemMeta removeMeta = removeMask.getItemMeta();
        removeMeta.setDisplayName("Remove Mask");
        removeMask.setItemMeta(removeMeta);
        maskMenu.addItem(removeMask);

        player.openInventory(maskMenu);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Select a Mask")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null && clickedItem.hasItemMeta()) {
                if (clickedItem.getType() == Material.PLAYER_HEAD) {
                    String maskName = clickedItem.getItemMeta().getDisplayName();
                    String potionEffect = mask.getConfig().getString("masks." + maskName + ".effect");
                    int duration = mask.getConfig().getInt("masks." + maskName + ".duration");
                    int amplifier = mask.getConfig().getInt("masks." + maskName + ".amplifier");

                    PotionEffectType effectType = PotionEffectType.getByName(potionEffect);
                    if (effectType != null) {
                        player.getInventory().setHelmet(clickedItem);
                        player.addPotionEffect(new PotionEffect(effectType, duration, amplifier));
                    } else {
                        player.sendMessage("Invalid potion effect type: " + potionEffect);
                    }
                } else if (clickedItem.getType() == Material.BARRIER) {
                    // Remove helmet
                    player.getInventory().setHelmet(null);

                    // Remove potion effects associated with the masks
                    for (String key : mask.getConfig().getConfigurationSection("masks").getKeys(false)) {
                        String potionEffect = mask.getConfig().getString("masks." + key + ".effect");
                        PotionEffectType effectType = PotionEffectType.getByName(potionEffect);
                        if (effectType != null) {
                            player.removePotionEffect(effectType);
                        }
                    }

                    player.sendMessage("Mask removed.");
                }

                player.closeInventory();
            }
        }
    }
}
