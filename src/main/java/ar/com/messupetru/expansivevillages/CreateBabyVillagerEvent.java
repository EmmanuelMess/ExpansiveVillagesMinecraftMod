package ar.com.messupetru.expansivevillages;

import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.world.IWorld;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CreateBabyVillagerEvent extends LivingSpawnEvent {

    private static final Logger LOGGER = LogManager.getLogger();

    public CreateBabyVillagerEvent(VillagerEntity entity, IWorld world, double x, double y, double z) {
        super(entity, world, x, y, z);
    }

}
