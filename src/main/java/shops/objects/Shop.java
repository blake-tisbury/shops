package shops.objects;

import com.sk89q.worldedit.regions.Region;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Shop {

    private final String ID;

    private String name;
    private String description;
    private int rating;
    private double price;
    private boolean owned;
    private Player owner;
    private Location warpLoc;
    private Location holoLoc;
    private Region region;

    public Shop(String ID) {
        this.ID = ID;
    }

    public Shop(String ID, Player owner, double price, Location warpLoc, Location holoLoc) {
        this.ID = ID;
        this.owner = owner;
        this.price = price;
        this.warpLoc = warpLoc;
        this.holoLoc = holoLoc;
    }

    public Shop(String ID, String description, Player owner, Location warpLoc, Location holoLoc, Region region) {
        this.ID = ID;
        this.description = description;
        this.owner = owner;
        this.warpLoc = warpLoc;
        this.holoLoc = holoLoc;
        this.region = region;
    }

    /*
     * GETTERS
     */
    public String getID() {
        return this.ID;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public int getRating() {
        return this.rating;
    }

    public Player getOwner() {
        return this.owner;
    }

    public Location getWarpLoc() {
        return this.warpLoc;
    }

    public Location getHoloLoc() {
        return this.holoLoc;
    }

    public double getPrice() {
        return this.price;
    }

    public boolean isOwned() {
        return this.owned;
    }

    /*
     * SETTERS
     */
    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setOwner(Player p) {
        this.owner = p;
        this.owned = true;
    }

    public void setWarpLoc(Location warpLoc) {
        this.warpLoc = warpLoc;
    }

    public void setHoloLoc(Location holoLoc) {
        this.holoLoc = holoLoc;
    }
}
