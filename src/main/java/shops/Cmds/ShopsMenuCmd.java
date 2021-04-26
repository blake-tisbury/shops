package shops.Cmds;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import shops.Gui.BaseGui;
import shops.Gui.ShopsGui;

public class ShopsMenuCmd extends BaseCmd {

    public ShopsMenuCmd(CommandSender sender, Command command, String label, String[] args) {
        super(sender, command, label, args);
    }

    @Override
    public boolean runCommand() {
        Player p = (Player) getSender();
        ShopsGui gui = new ShopsGui();
        gui.show(p);
        return true;
    }
}
