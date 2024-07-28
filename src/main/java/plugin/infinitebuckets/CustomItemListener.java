package plugin.infinitebuckets;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomItemListener implements Listener {

    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        String displayName = meta.getDisplayName();

        Block clickedBlock = event.getClickedBlock();
        BlockFace face = event.getBlockFace();
        Block targetBlock = clickedBlock.getRelative(face);

        if (displayName.equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Infinite Water")) {
            targetBlock.setType(Material.WATER);
            event.setCancelled(true);
        } else if (displayName.equals(ChatColor.RED + "" + ChatColor.BOLD + "Infinite Lava")) {
            targetBlock.setType(Material.LAVA);
            event.setCancelled(true);
        }
    }
}
