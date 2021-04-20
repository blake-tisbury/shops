package shops.Gui;


import com.google.gson.internal.$Gson$Preconditions;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import scala.Int;
import shops.Shops;
import shops.Utils.ShopManager;
import shops.Utils.Utils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import javax.rmi.CORBA.Util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import com.gmail.filoghost.holographicdisplays.api.line.HologramLine;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;




public class WarpsGui {


    Shops shops = Shops.getInstance();
    private YamlConfiguration config = Utils.loadConfig("/warpsGui.yml");
    private Gui warpsGui;
    private HashMap<Integer, String> slot = new HashMap<>();
    private HashMap<Integer, String> idSlot = new HashMap<>();
    private Economy econ = Shops.getEconomy();
    ShopManager sm = new ShopManager(shops);

    public Gui gui() {
        warpsGui = new Gui(Utils.chat(config.getString("title")), config.getInt("size"))
                .c();

        List<String> format = config.getStringList("format");
        Utils.makeFormat(config, warpsGui, format, "items");
        List<String> ids = sm.getIds();
        int key = 0;
        for(String s : ids) {
            if(!sm.getOwner(s).equals("null")) {
                OfflinePlayer op = (Bukkit.getOfflinePlayer(UUID.fromString(sm.getOwner(s))));
                ItemStack i = new ItemStack(Material.PLAYER_HEAD, 1, (short)3);
                SkullMeta skm = (SkullMeta) i.getItemMeta();
                skm.setOwningPlayer(op);
                skm.setDisplayName(Utils.chat(shops.getConfig().getString("claimedName").replace("{name}", sm.getName(s))));
                List<String> lore = new ArrayList<>();
                for(String ls : shops.getConfig().getStringList("claimedLore")) {
                    lore.add(Utils.chat(ls.replace("{id}", s).replace("{player}", op.getName()).replace("{description}", "")));
                    if(ls.contains("{description}")) {
                        for(String s2 : Utils.split(sm.getDescription(s))) {
                            lore.add(Utils.chat("&7" + s2));
                        }
                    }
                }
                skm.setLore(lore);
                i.setItemMeta(skm);
                warpsGui.i(i);
                slot.put(key, "claimed");
                idSlot.put(key, s);
                key++;
            } else {
                ItemStack i = new ItemStack(Material.PLAYER_HEAD, 1, (short)3);
                SkullMeta skm = (SkullMeta) i.getItemMeta();
                skm.setOwningPlayer(Bukkit.getOfflinePlayer("ElMarcosFTW"));
                skm.setDisplayName(Utils.chat(shops.getConfig().getString("unclaimedName")));
                List<String> lore = new ArrayList<>();
                for(String ls : shops.getConfig().getStringList("unclaimedLore")) {
                    lore.add(Utils.chat(ls.replace("{id}", s)).replace("{price}", sm.getPrice(s)));
                }
                skm.setLore(lore);
                i.setItemMeta(skm);
                warpsGui.i(i);
                slot.put(key, "unclaimed");
                idSlot.put(key, s);
                key++;
            }

        }

        warpsGui.onClick(e -> {
            Player p = (Player) e.getWhoClicked();
            int s = e.getRawSlot();
            int prev = config.getInt("previous"), next = config.getInt("nextPage"), close = config.getInt("close");;
            if(s == prev) {
                warpsGui.prevPage();
            } else if(s == next) {
                warpsGui.nextPage();
            } else if(s == close) {
                p.closeInventory();
            } else if(slot.get(s).equals("unclaimed")) {
                String id = idSlot.get(s);
                Location l = sm.getHolo(id);
                sm.setOwner(p, id);
                sm.setName(p.getName(), id);
                sm.setDescription("", id);

                // Hologram hologram = HologramsAPI.createHologram(HolographicDisplays, l);
                // textLine = hologram.appendTextLine("A hologram line");

                if(econ.getBalance(Bukkit.getOfflinePlayer(p.getUniqueId())) < Integer.valueOf(sm.getPrice(id))) {
                    p.sendMessage(Utils.chat(shops.getConfig().getString("messages.noFunds")));
                    return;
                }
                econ.withdrawPlayer(Bukkit.getOfflinePlayer(p.getUniqueId()), Integer.valueOf(sm.getPrice(id)));
                p.sendMessage(Utils.chat(shops.getConfig().getString("messages.shopPurchased")));
                p.closeInventory();
            } else if(slot.get(s).equals("claimed")) {
                String id = idSlot.get(s);
                Location l = sm.getWarp(id);
                p.closeInventory();
                p.teleport(l);
            }

        });


        return warpsGui;
    }

}
