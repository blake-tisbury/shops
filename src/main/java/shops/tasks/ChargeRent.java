package shops.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import shops.Shops;
import shops.managers.RentManager;
import shops.managers.ShopManager;

import java.util.Calendar;
import java.util.List;

public class ChargeRent extends BukkitRunnable {

    private final ShopManager SHOP_MANAGER;
    private final RentManager RENT_MANAGER;
    private final int rentDay;

    public ChargeRent() {
        this.SHOP_MANAGER = Shops.getShopManager();
        this.RENT_MANAGER = Shops.getRentManager();
        this.rentDay = 14;
    }

    /**
     * Bukkit runnabled to charge rent on the day specified by rentDay.
     */
    @Override
    public void run() {
        int date = Calendar.getInstance().get(Calendar.DATE);
        if (date == this.rentDay) {
            List<String> owners = SHOP_MANAGER.getOwners();
            RENT_MANAGER.chargeRent(owners);
        }
    }
}
