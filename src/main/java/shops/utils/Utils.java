package shops.utils;

import shops.Shops;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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


    public static YamlConfiguration loadConfig(String s) {
        File file = new File(Shops.getInstance().getDataFolder().getAbsolutePath() + s);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        return config;
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
