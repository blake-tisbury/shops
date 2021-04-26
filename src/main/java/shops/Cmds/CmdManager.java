package shops.Cmds;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CmdManager implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        BaseCmd cmd;

        if (args.length < 1) {
            cmd = new ShopsMenuCmd(sender, command, label, args);
        }
        else {
            switch (args[0]) {
                case "menu":
                    cmd = new ShopsMenuCmd(sender, command, label, args);
                    break;
                default:
                    sender.sendMessage("[Shops] \"" + args[0] + "\" is not a valid command.");
                    return true;
            }
        }

        return cmd.runCommand();
    }
}
