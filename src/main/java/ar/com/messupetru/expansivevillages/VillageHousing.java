package ar.com.messupetru.expansivevillages;

import net.minecraft.world.gen.feature.structure.VillageStructure;

import java.util.Objects;

public class VillageHousing {
    public final VillageStructure.Start start;
    private int beds;
    private int foods;
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

    public int getFoods() {
        return foods;
    }

    public void setFoods(int foods) {
        this.foods = foods;
    }

    public int getVillagers() {
        return villagers;
    }

    public void setVillagers(int villagers) {
        this.villagers = villagers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VillageHousing that = (VillageHousing) o;
        return beds == that.beds &&
                foods == that.foods &&
                villagers == that.villagers &&
                start.equals(that.start);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, beds, villagers);
    }
}
