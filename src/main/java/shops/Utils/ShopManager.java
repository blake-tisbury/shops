package shops.Utils;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import shops.Shops;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.gmail.filoghost.holographicdisplays.api.line.HologramLine;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

public class ShopManager{

    Statement statement;
    Shops shops;

    public ShopManager(Shops plugin) {
        this.shops = plugin;
        try {
            statement = shops.connection.createStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void createShop(String id, Location warpLoc, Location holoLoc, int price, Player p) {
        double warpX, warpY, warpZ, holoX, holoY, holoZ;
        Region region = createShopRegion(p, id);

        if (region == null) {
            return;
        }

        if (warpLoc == null) {
            warpX = region.getCenter().getX();
            warpY = region.getMinimumPoint().getY() + 1;
            warpZ = region.getCenter().getZ();
        }
        else {
            warpX = warpLoc.getX();
            warpY = warpLoc.getY();
            warpZ = warpLoc.getZ();
        }
        warpLoc = new Location(p.getWorld(), warpX, warpY, warpZ);

        if (holoLoc == null)  {
            holoX = region.getCenter().getX();
            holoY = region.getMinimumPoint().getY() + 1;
            holoZ = region.getCenter().getZ();
        }
        else {
            holoX = holoLoc.getX();
            holoY = holoLoc.getY();
            holoZ = holoLoc.getZ();
        }
        holoLoc = new Location(p.getWorld(), holoX, holoY, holoZ);


        String warpCoords = warpLoc.getWorld().getName() + ";" + warpX + ";" + warpY + ";" + warpZ;
        String holoCoords = holoLoc.getWorld().getName() + ";" + holoX + ";" + (holoY + 3) + ";" + holoZ;

        try {
            statement.executeUpdate("INSERT INTO shops (id, owner, Warp, price) VALUES ('" + id  + "', 'null', '" + warpCoords + "', '" + price +"')");
            final Hologram hologram = HologramsAPI.createHologram(shops, holoLoc);
            hologram.appendTextLine(ChatColor.YELLOW + "" + ChatColor.BOLD + "For rent! " + ChatColor.GREEN + "Use /shop buy to rent!" );
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        p.sendMessage(Utils.chat(Shops.getInstance().getConfig().getString("messages.shopCreated")));
    }

    public Region createShopRegion(Player p, String name) {
        com.sk89q.worldedit.entity.Player actor = BukkitAdapter.adapt(p);
        SessionManager manager = WorldEdit.getInstance().getSessionManager();
        LocalSession ls = manager.get(actor);
        Region region = null;
        World selectionWorld = ls.getSelectionWorld();

        // Create Region
        try {
            if(selectionWorld == null) throw new IncompleteRegionException();
            region = ls.getSelection(selectionWorld);
            if (region == null) {
                throw new IncompleteRegionException();
            }
        } catch (IncompleteRegionException e) {
            actor.print(TextComponent.of(Utils.chat(Shops.getInstance().getConfig().getString("messages.noRegion"))));
            return null;
        }

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager rm = container.get(BukkitAdapter.adapt(p.getWorld()));

        ProtectedCuboidRegion r = new ProtectedCuboidRegion(name, region.getMaximumPoint(), region.getMinimumPoint());

        // Set WorldGuard Flags
        r.setFlag(Flags.BLOCK_PLACE, StateFlag.State.DENY);
        r.setFlag(Flags.BLOCK_BREAK, StateFlag.State.DENY);
        r.setFlag(Flags.PVP, StateFlag.State.DENY);
        rm.addRegion(r);

        // Set Warp/Holos
//        Location warpLoc = new Location(p.getWorld(), warpX, region.getMinimumPoint().getY() + 1, warpZ);
//        Location holoLoc = new Location(p.getWorld(), holoX, region.getMinimumPoint().getY() + 3, holoZ);

        return region;
    }

    public void setOwner(Player p, String id) {
        try {
            statement.executeUpdate("UPDATE shops SET owner= '"+ p.getUniqueId().toString() +"' WHERE id='"+id+"'");
            ProtectedRegion pr =  WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(p.getWorld())).getRegion(id);
            pr.getOwners().addPlayer(p.getUniqueId());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void setPrice(int i, String id) {
        try {
            statement.executeUpdate("UPDATE shops SET price= '"+ i +"' WHERE id='"+id+"'");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public boolean isOwner(Player p, String id) {
        ResultSet rs = null;
        try {
            rs = statement.executeQuery("SELECT * FROM shops WHERE id='"+id+"'");
            if (rs.next()) {
                if (rs.getString("owner").equals(p.getUniqueId().toString())) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<String> getIds() {
        List<String> ids = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = statement.executeQuery("SELECT id FROM shops;");
            while(rs.next()) {
                ids.add(rs.getString("id"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return ids;
    }

    public void setWarp(Location l, String id) {
        String s = l.getWorld().getName() + ";" + l.getX() + ";" + l.getY() + ";" + l.getZ();
        try {
            statement.executeUpdate("UPDATE shops SET warp= '"+ l +"' WHERE id='"+id+"'");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public Location getWarp(String id) {
        Location loc = null;
        ResultSet rs = null;
        try {
            rs = statement.executeQuery("SELECT * FROM shops WHERE id='"+id+"'");
            if(rs.next()) {
                String locString = rs.getString("warp");
                String[] split = locString.split(";");
                loc = new Location(Bukkit.getWorld(split[0]), Double.valueOf(split[1]), Double.valueOf(split[2]), Double.valueOf(split[3]));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return loc;
    }

    public Location getHolo(String id) {
        Location loc = null;
        ResultSet rs = null;
        try {
            rs = statement.executeQuery("SELECT * FROM shops WHERE id='"+id+"'");
            if(rs.next()) {
                String locString = rs.getString("holo");
                String[] split = locString.split(";");
                loc = new Location(Bukkit.getWorld(split[0]), Double.valueOf(split[1]), Double.valueOf(split[2]), Double.valueOf(split[3]));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return loc;
    }
    public String getOwner(String id) {
        ResultSet rs = null;
        try {
            rs = statement.executeQuery("SELECT * FROM shops WHERE id='"+id+"'");
            if(rs.next()) {
                return rs.getString("owner");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public String getPrice(String id) {
        ResultSet rs = null;
        try {
            rs = statement.executeQuery("SELECT * FROM shops WHERE id='"+id+"'");
            if(rs.next()) {
                return rs.getString("price");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public void setName(String name, String id) {
        try {
            statement.executeUpdate("UPDATE shops SET name= '"+ name +"' WHERE id='"+id+"'");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public String getName(String id) {
        ResultSet rs = null;
        try {
            rs = statement.executeQuery("SELECT * FROM shops WHERE id='"+id+"'");
            if(rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public void setDescription(String description, String id) {
        try {
            statement.executeUpdate("UPDATE shops SET description= '"+ description +"' WHERE id='"+id+"'");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public String getDescription(String id) {
        ResultSet rs = null;
        try {
            rs = statement.executeQuery("SELECT * FROM shops WHERE id='"+id+"'");
            if(rs.next()) {
                return rs.getString("description");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public String getId(String name) {
        ResultSet rs = null;
        try {
            rs = statement.executeQuery("SELECT * FROM shops WHERE name='"+ name +"'");
            if(rs.next()) {
                return rs.getString("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getId(Player p) {
        ResultSet rs = null;
        try {
            rs = statement.executeQuery("SELECT * FROM shops WHERE owner='"+ p.getUniqueId().toString() +"'");
            if(rs.next()) {
                return rs.getString("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


}
