package net.emc.emce.objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import lombok.Getter;

@Getter
public class QuarterCuboid {
    private final int[] cornerOne;
    private final int[] cornerTwo;
    
    public QuarterCuboid(JsonElement cornerOne, JsonElement cornerTwo) {
        this.cornerOne = jsonToArray(cornerOne);
        this.cornerTwo = jsonToArray(cornerTwo);
    }
    
    private int[] jsonToArray(JsonElement el) {
        JsonArray jsonArray = el.getAsJsonArray();
        return new int[]{
            jsonArray.get(0).getAsInt(),
            jsonArray.get(1).getAsInt(),
            jsonArray.get(2).getAsInt()
        };
    }
}