package shops.managers;

import net.tnemc.core.common.api.TNEAPI;

import java.math.BigDecimal;
import java.util.List;

public class RentManager {

    private final TNEAPI ECON;
    private final int RENT_AMOUNT;

    public RentManager(TNEAPI econ) {
        this.ECON = econ;

        // TODO: Change this to be set in the config.
        // TODO: After launch add this to the Shop object.
        this.RENT_AMOUNT = 100;
    }

    /**
     * Charge RENT_AMOUNT to the given list of owners.
     * @param owners
     */
    public void chargeRent(List<String> owners) {
        for (String name : owners) {
            this.ECON.removeHoldings(name, BigDecimal.valueOf(RENT_AMOUNT));
        }
    }
}
