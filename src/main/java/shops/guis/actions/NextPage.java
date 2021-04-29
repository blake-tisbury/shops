package shops.guis.actions;

import org.bukkit.entity.Player;
import shops.guis.BaseGui;
import shops.guis.ShopsGui;

import java.util.UUID;

public class NextPage implements GuiAction {

    @Override
    public void click(Player p) {
        UUID playerUUID = p.getUniqueId();

        UUID guiUUID = BaseGui.openGuis.get(playerUUID);
        if (guiUUID != null) {
            ShopsGui gui = (ShopsGui) BaseGui.getGuisByUuid().get(guiUUID);
            gui.nextPage();
        }
    }
}
