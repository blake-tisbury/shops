package shops.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CmdCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete (CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        if (args.length == 1) {
            commands.add("create");
            commands.add("menu");
            commands.add("setdescription");
            commands.add("setname");
            StringUtil.copyPartialMatches(args[0], commands, completions);
        }
        else if (args.length == 2) {
            switch (args[1]) {
                case "create":
                    commands.add("<Shop ID>");
                    commands.add("<Price>");
                    break;
                case "menu":
                    break;
                case "setdescription":
                    commands.add("<Shop ID>");
                    commands.add("<Description>");
                    break;
                case "setname":
                    commands.add("<Shop ID>");
                    commands.add("<Name>");
                    break;
            }
            StringUtil.copyPartialMatches(args[0], commands, completions);
        }

        Collections.sort(completions);
        return completions;
    }
}
