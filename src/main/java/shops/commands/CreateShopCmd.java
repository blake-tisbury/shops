package shops.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import shops.Shops;

public class CreateShopCmd extends BaseCmd {

    public CreateShopCmd(CommandSender sender, Command command, String label, String[] args) {
        super(sender, command, label, args);
    }

    @Override
    public boolean runCommand() {
        String[] args = getArgs();
        Shops.getShopManager().createShop(args[1], null, null, Integer.parseInt(args[2]), (Player) getSender());
        return true;
    }
}
