package shops.Cmds;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public abstract class BaseCmd {

    private final CommandSender sender;
    private final Command command;
    private final String label;
    private final String[] args;

    public BaseCmd(CommandSender sender, Command command, String label, String[] args) {
        this.sender = sender;
        this.command = command;
        this.label = label;
        this.args = args;
    }

    public abstract boolean runCommand();

    public CommandSender getSender() {
        return this.sender;
    }

    public Command getCommand() {
        return this.command;
    }

    public String getLabel() {
        return this.label;
    }

    public String[] getArgs() {
        return this.args;
    }
}
