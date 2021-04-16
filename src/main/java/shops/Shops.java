package shops;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;
import shops.Cmds.ShopCmd;
import shops.Utils.Utils;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.List;

public final class Shops extends JavaPlugin implements Listener {

    private static Shops instance;
    private Statement statement;
    String host = this.getConfig().getString("host"), port = this.getConfig().getString("port"), database = this.getConfig().getString("database"), username = this.getConfig().getString("username"), password = this.getConfig().getString("password");
    public Connection connection;

    private static Economy econ = null;
    private static Permission perms = null;
    private static Chat chat = null;


    @Override
    public void onEnable() {
        instance = this;

        if (!setupEconomy() ) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupPermissions();
        setupChat();

        try {
            openConnection();
            this.statement = connection.createStatement();
            PreparedStatement ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS shops (" +
                    "id VARCHAR(32) PRIMARY KEY," +
                    "owner VARCHAR(55) NOT NULL," +
                    "warp VARCHAR(55)," +
                    "name VARCHAR(32)," +
                    "price INT," +
                    "description VARCHAR(64)" +
                    ");");
            ps.executeUpdate();
        } catch (ClassNotFoundException | SQLException e) {
            Bukkit.getServer().getConsoleSender().sendMessage(Utils.chat("&c&LCould not enable due to invalid database"));
            e.printStackTrace();
        }

        getCommand("shops").setExecutor(new ShopCmd());

        saveDefaultConfig();

        saveResource("warpsGui.yml", false);

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    CoreProtectAPI CoreProtect = getCoreProtect();
    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        Block b = e.getBlock();

        CoreProtectAPI CoreProtect = getCoreProtect();
        if (CoreProtect!=null){ //Ensure we have access to the API
            List<String[]> lookup = CoreProtect.blockLookup(b, 0);
            if (lookup!=null){
                for (String[] value : lookup){
                    CoreProtectAPI.ParseResult result = CoreProtect.parseResult(value);
                    List<String> blocked = this.getConfig().getStringList("blockedBlocks");
                    if(blocked.contains(result.getPlayer()) && !p.isOp()) {
                        e.setCancelled(true);
                    } else {
                        return;
                    }
                }
            }
        }

    }

    public static Shops getInstance() {
        return instance;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public void openConnection() throws SQLException, ClassNotFoundException {
        if(connection != null && !connection.isClosed()) {
            return;
        }
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://"
                        + this.host + ":" + this.port + "/" + this.database,
                this.username, this.password);
    }

    public static Economy getEconomy() {
        return econ;
    }

    public static Permission getPermissions() {
        return perms;
    }

    public static Chat getChat() {
        return chat;
    }

    private CoreProtectAPI getCoreProtect() {
        Plugin plugin = getServer().getPluginManager().getPlugin("CoreProtect");

        // Check that CoreProtect is loaded
        if (plugin == null || !(plugin instanceof CoreProtect)) {
            return null;
        }

        // Check that the API is enabled
        CoreProtectAPI CoreProtect = ((net.coreprotect.CoreProtect) plugin).getAPI();
        if (CoreProtect.isEnabled() == false) {
            return null;
        }

        // Check that a compatible version of the API is loaded
        if (CoreProtect.APIVersion() < 6) {
            return null;
        }

        return CoreProtect;
    }

}
