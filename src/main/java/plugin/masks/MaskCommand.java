package plugin.masks;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MaskCommand implements CommandExecutor {

    private Masks mask;

    public MaskCommand(Masks mask) {
        this.mask = mask;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            GUIManager.openMaskMenu(player);
        }
        return true;
    }
}
