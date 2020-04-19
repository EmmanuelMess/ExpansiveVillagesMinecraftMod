package ar.com.messupetru.expansivevillages.events;

import net.minecraft.world.gen.feature.structure.VillageStructure;
import net.minecraftforge.eventbus.api.Event;

public class VillageStructureStartEvent extends Event {
    public final VillageStructure.Start start;

    public VillageStructureStartEvent(VillageStructure.Start start) {
        this.start = start;
    }
}
