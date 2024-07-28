package plugin.infinitebuckets;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemManager {

    public static ItemStack infiniteWater;
    public static ItemStack infiniteLava;

    public static void init() {
        createInfiniteWater();
        createInfiniteLava();
    }

    private static void createInfiniteWater() {
        ItemStack item = new ItemStack(Material.WATER_BUCKET);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Infinite Water");
        item.setItemMeta(meta);
        infiniteWater = item;
    }

    private static void createInfiniteLava() {
        ItemStack item = new ItemStack(Material.LAVA_BUCKET);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Infinite Lava");
        item.setItemMeta(meta);
        infiniteLava = item;
    }

    public static ItemStack getInfiniteWater(int amount) {
        ItemStack item = infiniteWater.clone();
        item.setAmount(amount);
        return item;
    }

    public static ItemStack getInfiniteLava(int amount) {
        ItemStack item = infiniteLava.clone();
        item.setAmount(amount);
        return item;
    }
}
