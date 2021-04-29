package shops.guis.actions;

import org.bukkit.entity.Player;
import shops.guis.BaseGui;

import java.util.UUID;

public class Exit implements GuiAction {
    @Override
    public void click(Player p) {
        UUID playerUUID = p.getUniqueId();

        UUID guiUUID = BaseGui.openGuis.get(playerUUID);
        if (guiUUID != null) {
            BaseGui gui = BaseGui.getGuisByUuid().get(guiUUID);
            gui.close(p);
        }
    }
}
