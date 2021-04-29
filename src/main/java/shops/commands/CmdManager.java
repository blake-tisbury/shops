package shops.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import shops.Shops;
import shops.Utils.Utils;

public class CmdManager implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        BaseCmd cmd = null;
        Player p = (Player) sender;

        if (args.length < 1) {
            cmd = new ShopsMenuCmd(sender, command, label, args);
            return cmd.runCommand();
        }
        else {
            switch (args[0]) {
                case "menu":
                    cmd = new ShopsMenuCmd(sender, command, label, args);
                    break;
                case "create":
                    if (args.length <= 2) {
                        p.sendMessage(Utils.chat(Shops.getInstance().getConfig().getString("messages.createInvalid")));
                        return true;
                    }
                    cmd = new CreateShopCmd(sender, command, label, args);
                    break;
                case "setname":
                    cmd = new SetNameCmd(sender, command, label, args);
                    break;
                case "setdescription":
                    cmd = new SetDescCmd(sender, command, label,args);
                    break;
                default:
                    sender.sendMessage("[Shops] \"" + args[0] + "\" is not a valid command.");
                    return true;
            }
        }
        return cmd.runCommand();
    }
}
