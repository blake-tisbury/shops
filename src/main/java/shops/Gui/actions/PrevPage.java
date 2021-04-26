package shops.Gui.actions;

import org.bukkit.entity.Player;
import shops.Gui.BaseGui;
import shops.Gui.ShopsGui;

import java.util.UUID;

public class PrevPage implements GuiAction {
    @Override
    public void click(Player p) {
        UUID playerUUID = p.getUniqueId();

        UUID guiUUID = BaseGui.openGuis.get(playerUUID);
        if (guiUUID != null) {
            ShopsGui gui = (ShopsGui) BaseGui.getGuisByUuid().get(guiUUID);
            gui.prevPage();
        }
    }
}
