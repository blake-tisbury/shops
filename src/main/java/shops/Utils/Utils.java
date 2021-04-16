package shops.Utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.meta.SkullMeta;
import shops.Gui.Gui;
import shops.Shops;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Utils {

    public static String chat(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static List<String> chat(List<String> list) {
        List<String> colored = new ArrayList<>();
        for(String s : list) {
            colored.add(chat(s));
        }
        return colored;
    }

    public static Boolean needPage(Gui gui) {
        ItemStack[] cont = gui.getContents();
        for(ItemStack i : cont) {
            if(cont == null) {
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

    public static YamlConfiguration loadConfig(String s) {
        File file = new File(Shops.getInstance().getDataFolder().getAbsolutePath() + s);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        return config;
    }

    public static void makeFormat(FileConfiguration config, Gui gui, List<String> toFormat, String keyForItems) {

        int size = gui.getInventory().getSize();

        if(toFormat.size() == size / 9) {

            for(int i = 0; i < (size / 9); i++) {
                String s = toFormat.get(i);
                for(int z = 0; z < 9; z++) {
                    String removeSpaces = s.replaceAll(" ", "");
                    char individual = removeSpaces.charAt(z);
                    if(i > 0) {
                        if(config.get(keyForItems + "." + individual) == null) {
                            continue;
                        } else {
                            ItemStack stack = new ItemStack(Material.matchMaterial(config.getString(keyForItems + "." + individual + ".material")));
                            ItemMeta im = stack.getItemMeta();
                            im.setDisplayName(Utils.chat(config.getString(keyForItems + "." + individual + ".name")));
                            im.setLore(chat(config.getStringList(keyForItems + "." + individual + ".lore")));
                            stack.setItemMeta(im);
                            gui.i((9 * i) + z, stack);
                        }
                    } else {
                        if(config.get(keyForItems + "." + individual) == null) {
                            continue;
                        } else {
                            ItemStack stack = new ItemStack(Material.matchMaterial(config.getString(keyForItems + "." + individual + ".material")));
                            ItemMeta im = stack.getItemMeta();
                            im.setDisplayName(config.getString(keyForItems + "." + individual + ".name"));
                            im.setLore(chat(config.getStringList(keyForItems + "." + individual + ".lore")));
                            stack.setItemMeta(im);
                            gui.i(z, stack);
                        }
                    }
                }

            }

        }
    }

    public static List<String> split(String s) {
        String[] split = s.split(" ");
        List<String> finalStringList = new ArrayList<>();
        String finalString = "";
        for(int i = 0; i < split.length; i++) {
            finalString+=split[i] + " ";
            if(i >0 && i % 5 == 0) {
                finalStringList.add(finalString);
                finalString = "";
            }
        }
        return finalStringList;
    }

}
