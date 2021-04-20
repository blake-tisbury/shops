package shops.Cmds;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import shops.Gui.WarpsGui;
import shops.Shops;
import shops.Utils.ShopManager;
import shops.Utils.Utils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import sun.security.util.ArrayUtil;

import java.util.Arrays;
import java.util.List;

public class ShopCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player p = (Player) commandSender;
        Shops shops = Shops.getInstance();
        ShopManager sm = new ShopManager(shops);
        FileConfiguration config = shops.getConfig();
        if(args.length == 0) {
            WarpsGui warpsGui = new WarpsGui();
            warpsGui.gui().show(p);
            return false;
        } else if(args[0].equalsIgnoreCase("create")) {
            if(!(p.hasPermission("shops.region.create"))) return false;
            if(args.length <= 2) {
                  p.sendMessage(Utils.chat(config.getString("messages.createInvalid")));
                  return false;
              } else {
                  com.sk89q.worldedit.entity.Player actor = BukkitAdapter.adapt(p);
                  SessionManager manager = WorldEdit.getInstance().getSessionManager();
                  LocalSession ls = manager.get(actor);
                  Region region = null;
                  World selectionWorld = ls.getSelectionWorld();
                  try {
                      if(selectionWorld == null) throw new IncompleteRegionException();
                      region = ls.getSelection(selectionWorld);
                  } catch (IncompleteRegionException e) {
                      actor.print(TextComponent.of(Utils.chat(config.getString("messages.noRegion"))));
                  }

                  RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                  RegionManager rm = container.get(BukkitAdapter.adapt(p.getWorld()));
                  ProtectedCuboidRegion r = new ProtectedCuboidRegion(args[1], region.getMaximumPoint(), region.getMinimumPoint());
                  r.setFlag(Flags.BLOCK_PLACE, StateFlag.State.DENY);
                  r.setFlag(Flags.BLOCK_BREAK, StateFlag.State.DENY);
                  r.setFlag(Flags.PVP, StateFlag.State.DENY);
                  rm.addRegion(r);
                  Location centerFloor = new Location(p.getWorld(), region.getCenter().getX(), region.getMinimumPoint().getY() + 1, region.getCenter().getZ());
                  Location centerHolo = new Location(p.getWorld(), region.getCenter().getX(), region.getMinimumPoint().getY() + 3, region.getCenter().getZ());
                  sm.CreateShop(args[1], centerFloor, centerHolo, Integer.valueOf(args[2]), shops);
                  p.sendMessage(Utils.chat(config.getString("messages.shopCreated")));
              }
        } else if(args[0].equalsIgnoreCase("setname")) {
            if(args.length <= 2) {
                p.sendMessage(Utils.chat(config.getString("messages.nameInvalid")));
            } else if(sm.getId(p) == null) {
                p.sendMessage(Utils.chat(config.getString("messages.noShop")));
            } else {
                String id = sm.getId(p);
                sm.setName(args[2], id);
            }
        } else if(args[0].equalsIgnoreCase("setdescription")) {
            if(args.length <= 2) {
                p.sendMessage(Utils.chat(config.getString("messages.descriptionInvalid")));
            } else if(sm.getId(p) == null) {
                p.sendMessage(Utils.chat(config.getString("messages.noShop")));
            } else {
                String id = sm.getId(p);
                sm.setDescription(String.join(" ",  Arrays.copyOfRange(args, 2, args.length)), args[1]);
            }
        } else if(args[0].equalsIgnoreCase("import")) {
            if(p.hasPermission("Shops.import")) {
                for(int i = 1; i < 352; i++) {
                    RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                    RegionManager rm = container.get(BukkitAdapter.adapt(p.getWorld()));
                    ProtectedRegion region = rm.getRegion(String.valueOf(i));

                    if(region == null) {
                        p.sendMessage(Utils.chat("&c&lShops &7| Shops imported"));
                        return false;
                    }

                    double X = (region.getMinimumPoint().getX() + region.getMaximumPoint().getX())/2;
                    double Y = region.getMinimumPoint().getY() + 1;
                    double Z = (region.getMinimumPoint().getZ() + region.getMaximumPoint().getZ())/2;
                    Location centerHolo = new Location(p.getWorld(), X , Y, Z);
                    Location centerFloor = new Location(p.getWorld(), X , Y, Z);
                    sm.CreateShop(String.valueOf(i), centerFloor, centerHolo, i, shops);
                }
                p.sendMessage(Utils.chat("&c&lShops &7| Shops imported"));
            } else {
                return false;
            }
        }

      return false;
  }
}
