package ar.com.messupetru.expansivevillages;

import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.world.IWorld;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CreateBabyVillagerEvent extends LivingSpawnEvent {

    private static final Logger LOGGER = LogManager.getLogger();

    public final ServerWorld serverWorld;
    public final VillagerEntity child;

    public CreateBabyVillagerEvent(VillagerEntity child, ServerWorld world, double x, double y, double z) {
        super(child, world, x, y, z);
        if(!child.isChild()) {
            throw new IllegalArgumentException("Child is not child!");
        }

        serverWorld = world;
        this.child = child;
    }

}
