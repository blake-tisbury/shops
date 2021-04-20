package shops.Cmds;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import shops.Shops;
import shops.menus.MenuManager;

public class ShopsMenuCmd extends BaseCmd {

    public ShopsMenuCmd(CommandSender sender, Command command, String label, String[] args) {
        super(sender, command, label, args);
    }

    @Override
    public boolean runCommand() {
        MenuManager menuManager = Shops.getMenuManager();
        menuManager.showMenu((Player) getSender());
        return true;
    }
}
