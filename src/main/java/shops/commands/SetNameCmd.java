package shops.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import shops.Shops;
import shops.managers.ShopManager;
import shops.utils.Utils;

public class SetNameCmd extends BaseCmd {

    public SetNameCmd(CommandSender sender, Command command, String label, String[] args) {
        super(sender, command, label, args);
    }

    @Override
    public boolean runCommand() {
        Player p = (Player) getSender();
        String[] args = getArgs();
        FileConfiguration config = Shops.getInstance().getConfig();
        ShopManager sm = Shops.getShopManager();

        if (args.length <= 2) {
            p.sendMessage(Utils.chat(config.getString("messages.nameInvalid")));
        }
        else if (sm.getId(p) == null) {
            p.sendMessage(Utils.chat(config.getString("messages.noShop")));
        }
        else if (!sm.isOwner(p, args[1])) {
            p.sendMessage(Utils.chat(config.getString("messages.noShop")));
        }
        else {
            sm.setName(args[2], args[1]);
            p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Shops |" + ChatColor.GRAY + " Successfully set shop name.");
        }

        return true;
    }
}
