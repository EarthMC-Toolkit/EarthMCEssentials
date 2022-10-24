package net.emc.emce.objects;

import com.google.gson.JsonObject;

public class Resident {
    private final String name;
    private final String town;
    private final String nation;
    private final String rank;

    public Resident(JsonObject object) {
        this.name = object.get("name").getAsString();
        this.town = object.get("town").getAsString();
        this.nation = object.get("nation").getAsString();
        this.rank = object.get("rank").getAsString();
    }

    public Resident(String name) {
        this.name = name;
        this.town = "";
        this.nation = "";
        this.rank = "";
    }

    public String getName() {
        return name;
    }

    public String getTown() {
        return town;
    }

    public String getNation() {
        return nation;
    }

    public String getRank() {
        return rank;
    }
}
