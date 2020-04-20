package ar.com.messupetru.expansivevillages;

import net.minecraft.world.gen.feature.structure.VillageStructure;

public class VillageHousing {
    public final VillageStructure.Start start;
    private int beds;
    private int villagers;

    public VillageHousing(VillageStructure.Start start) {
        this.start = start;
    }

    public int getBeds() {
        return beds;
    }

    public void setBeds(int beds) {
        this.beds = beds;
    }

    public int getVillagers() {
        return villagers;
    }

    public void setVillagers(int villagers) {
        this.villagers = villagers;
    }
}
