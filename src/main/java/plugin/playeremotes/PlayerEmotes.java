package plugin.playeremotes;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public final class PlayerEmotes extends JavaPlugin {

    private final HashMap<UUID, Long> slapCooldown = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("PlayerEmotes has been enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("PlayerEmotes has been disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String commandName = command.getName().toLowerCase();
            Player target = args.length > 0 ? Bukkit.getPlayer(args[0]) : null;

            switch (commandName) {
                case "slap":
                    if (target != null) {
                        long currentTime = System.currentTimeMillis();
                        UUID playerUUID = player.getUniqueId();
                        if (!slapCooldown.containsKey(playerUUID) || currentTime - slapCooldown.get(playerUUID) >= 10000) {
                            target.setVelocity(new Vector(0, 0.2, 0));
                            Bukkit.broadcastMessage(ChatColor.AQUA + player.getName() + " has slapped " + target.getName() + "!");
                            slapCooldown.put(playerUUID, currentTime);
                        } else {
                            player.sendMessage(ChatColor.RED + "You must wait before using /slap again.");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Player Not Found.");
                    }
                    break;

                case "lol":
                    if (target != null) {
                        Bukkit.broadcastMessage(ChatColor.AQUA + player.getName() + " laughs out loud at " + target.getName() + "!");
                    } else {
                        player.sendMessage(ChatColor.RED + "Player not found.");
                    }
                    break;

                case "rofl":
                    if (target != null) {
                        Bukkit.broadcastMessage(ChatColor.AQUA + player.getName() + " rolls on the floor laughing at " + target.getName() + "!");
                    } else {
                        player.sendMessage(ChatColor.RED + "Player not found.");
                    }
                    break;

                case "love":
                    if (target != null) {
                        Bukkit.broadcastMessage(ChatColor.AQUA + player.getName() + " loves " + target.getName() + ChatColor.LIGHT_PURPLE + " <3 ");
                    } else {
                        player.sendMessage(ChatColor.RED + "Player not found.");
                    }
                    break;

                case "boop":
                    if (target != null) {
                        Bukkit.broadcastMessage(ChatColor.AQUA + player.getName() + " has booped " + target.getName() + " :o");
                    } else {
                        player.sendMessage(ChatColor.RED + "Player not found.");
                    }
                    break;

                case "spank":
                    if (target != null) {
                        Bukkit.broadcastMessage(ChatColor.AQUA + player.getName() + " has spanked " + target.getName() + " on da butt :3");
                    } else {
                        player.sendMessage(ChatColor.RED + "Player not found.");
                    }
                    break;

                case "cry":
                    Bukkit.broadcastMessage(ChatColor.AQUA + player.getName() + " is crying :( ");
                    break;

                case "kiss":
                    if (target != null) {
                        Bukkit.broadcastMessage(ChatColor.AQUA + player.getName() + " has kissed " + target.getName() + " :O");
                    } else {
                        player.sendMessage(ChatColor.RED + "Player not found.");
                    }
                    break;

                case "lick":
                    if (target != null) {
                        Bukkit.broadcastMessage(ChatColor.AQUA + player.getName() + " has licked " + target.getName() + " O.o");
                    } else {
                        player.sendMessage(ChatColor.RED + "Player not found.");
                    }
                    break;

                case "emote":
                    player.sendMessage(ChatColor.GREEN + "Available emotes: /slap, /lol, /rofl, /love, /boop, /spank, /cry, /kiss, /lick");
                    break;

                default:
                    return false;
            }
            return true;
        }
        return false;
    }
}
