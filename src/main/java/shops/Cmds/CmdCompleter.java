package shops.Cmds;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CmdCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete (CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equals("shops")) {
            List<String> list = new ArrayList<>();
            if (args.length < 1) {
                list.add("create");
                list.add("menu");
                list.add("remove");
                return list;
            }

            switch (args[0]) {
                case "create":
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        list.add(p.getName());
                    }
                    break;
                default:
                    break;
            }

            return list;
        }

        return null;
    }
}
