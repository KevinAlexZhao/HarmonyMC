package plugin.infinitebuckets;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandManager implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <get|give> <amount> [player]");
            return false;
        }

        String action = args[0];
        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Amount must be a number.");
            return false;
        }

        Player target = sender instanceof Player ? (Player) sender : null;
        if (args.length > 2) {
            target = Bukkit.getPlayer(args[2]);
        }

        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return false;
        }

        ItemStack item = null;
        if (command.getName().equalsIgnoreCase("infwater")) {
            item = ItemManager.getInfiniteWater(amount);
        } else if (command.getName().equalsIgnoreCase("inflava")) {
            item = ItemManager.getInfiniteLava(amount);
        }

        if (item == null) {
            sender.sendMessage(ChatColor.RED + "Invalid item.");
            return false;
        }

        if (action.equalsIgnoreCase("get")) {
            target.getInventory().addItem(item);
            target.sendMessage(ChatColor.GREEN + "You received " + amount + " " + item.getItemMeta().getDisplayName());
        } else if (action.equalsIgnoreCase("give")) {
            target.getInventory().addItem(item);
            target.sendMessage(ChatColor.GREEN + sender.getName() + " gave you " + amount + " " + item.getItemMeta().getDisplayName());
            sender.sendMessage(ChatColor.GREEN + "You gave " + amount + " " + item.getItemMeta().getDisplayName() + " to " + target.getName());
        } else {
            sender.sendMessage(ChatColor.RED + "Invalid action. Use 'get' or 'give'.");
            return false;
        }

        return true;
    }
}
