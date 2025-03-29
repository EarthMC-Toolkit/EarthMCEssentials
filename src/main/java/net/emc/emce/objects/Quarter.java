package net.emc.emce.objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.emcw.common.Entity;
import io.github.emcw.utils.GsonUtil;

import java.util.ArrayList;
import java.util.List;

// Reference: https://earthmc.net/docs/api#quarters
public class Quarter {
    public final String name;
    public final String uuid;
    public final QuarterType type;
    public final String creator;
    public final Entity owner, town, nation;
    
    // Timestamps
    public final long registered;
    public final Long claimedAt;
    
    // Status
    public final boolean isEmbassy, isForSale;
    
    // Stats
    public final Double price;
    public final int volume;
    public final Float particleSize;
    
    public final int[] colours;
    public final String[] trusted;
    public final List<QuarterCuboid> cuboids;
    
    public Quarter(JsonObject rawObj) {
        this.name = rawObj.get("name").getAsString();
        this.uuid = rawObj.get("uuid").getAsString();
        this.type = QuarterType.valueOf(rawObj.get("type").getAsString());
        this.creator = rawObj.get("creator").getAsString();
        
        this.owner = objToEntity(rawObj.get("owner"));
        this.town = objToEntity(rawObj.get("town"));
        this.nation = objToEntity(rawObj.get("nation")); // TODO: This may not exist, make it possible to be null?
        
        JsonObject timestamps = rawObj.get("timestamps").getAsJsonObject();
        this.registered = timestamps.get("registered").getAsLong();
        
        JsonElement claimedAt = timestamps.get("claimedAt");
        this.claimedAt = claimedAt != null ? claimedAt.getAsLong() : null;
        
        JsonObject status = rawObj.get("status").getAsJsonObject();
        this.isEmbassy = status.get("isEmbassy").getAsBoolean();
        this.isForSale = status.get("isForSale").getAsBoolean();
        
        JsonObject stats = rawObj.get("stats").getAsJsonObject();
        JsonElement price = stats.get("price");
        this.price = price != null ? price.getAsDouble() : null;
        this.volume = stats.get("volume").getAsInt();
        this.particleSize = stats.get("particleSize").getAsFloat();
        
        this.colours = GsonUtil.deserialize(rawObj.get("colour").getAsJsonArray(), int[].class);
        this.trusted = GsonUtil.deserialize(rawObj.get("trusted").getAsJsonArray(), String[].class);
        
        List<QuarterCuboid> quarterCuboids = new ArrayList<>();
        
        var cuboidsArr = rawObj.get("cuboids").getAsJsonArray();
        for (JsonElement cuboid : cuboidsArr) {
            JsonElement cornerOne = cuboid.getAsJsonObject().get("cornerOne");
            JsonElement cornerTwo = cuboid.getAsJsonObject().get("cornerTwo");
            
            quarterCuboids.add(new QuarterCuboid(cornerOne, cornerTwo));
        }
        
        this.cuboids = quarterCuboids;
    }
    
    Entity objToEntity(JsonElement el) {
        JsonObject obj = el.getAsJsonObject();
        return new Entity(obj.get("uuid").getAsString(), obj.get("name").getAsString());
    }
}