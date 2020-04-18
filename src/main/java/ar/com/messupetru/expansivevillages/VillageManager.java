package ar.com.messupetru.expansivevillages;

import net.minecraft.entity.ai.brain.task.CreateBabyVillagerTask;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused") //called from ASM
public final class VillageManager {

    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Called in {@link CreateBabyVillagerTask#func_220477_a(ServerWorld, VillagerEntity, BlockPos)}
     */
    public static void fireCreateVillagerBabyEvent(ServerWorld serverWorld, VillagerEntity entity, GlobalPos globalpos) {
        LOGGER.debug("ASM successfully called fireCreateVillagerBabyEvent()");
        MinecraftForge.EVENT_BUS.post(new CreateBabyVillagerEvent(entity, serverWorld, globalpos.getPos().getX(),
                globalpos.getPos().getY(), globalpos.getPos().getZ()));
    }

    /**
     * Mock of callsite for fireCreateVillagerBabyEvent
     */
    public static void test(ServerWorld p_220477_1_, VillagerEntity p_220477_2_, BlockPos p_220477_3_) {
        GlobalPos globalpos = GlobalPos.of(p_220477_1_.getDimension().getType(), p_220477_3_);
        fireCreateVillagerBabyEvent(p_220477_1_, p_220477_2_, globalpos);
    }
}
