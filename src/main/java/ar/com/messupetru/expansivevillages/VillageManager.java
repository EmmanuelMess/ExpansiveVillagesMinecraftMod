package ar.com.messupetru.expansivevillages;

import ar.com.messupetru.expansivevillages.events.CreateBabyVillagerEvent;
import ar.com.messupetru.expansivevillages.events.VillageStructureStartEvent;
import net.minecraft.entity.ai.brain.task.CreateBabyVillagerTask;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.VillageStructure;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused") //called from ASM
public final class VillageManager {

    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Called in {@link VillageStructure.Start#init(ChunkGenerator, TemplateManager, int, int, Biome)}
     */
    public static void fireVillageStructureStartEvent(VillageStructure.Start structure) {
        LOGGER.debug("ASM successfully called fireVillageStructureStartEvent()");
        MinecraftForge.EVENT_BUS.post(new VillageStructureStartEvent(structure));
    }

    /**
     * Called in {@link CreateBabyVillagerTask#func_220477_a(ServerWorld, VillagerEntity, BlockPos)}
     */
    public static void fireCreateVillagerBabyEvent(ServerWorld serverWorld, VillagerEntity entity, GlobalPos globalpos) {
        LOGGER.debug("ASM successfully called fireCreateVillagerBabyEvent()");
        MinecraftForge.EVENT_BUS.post(new CreateBabyVillagerEvent(entity, serverWorld, globalpos.getPos().getX(),
                globalpos.getPos().getY(), globalpos.getPos().getZ()));
    }

    /**
     * Mock of callsite for fireVillageStructureStartEvent
     */

    private static final class Mocker extends VillageStructure.Start {
        public Mocker(Structure<?> p_i225821_1_, int p_i225821_2_, int p_i225821_3_, MutableBoundingBox p_i225821_4_, int p_i225821_5_, long p_i225821_6_) {
            super(p_i225821_1_, p_i225821_2_, p_i225821_3_, p_i225821_4_, p_i225821_5_, p_i225821_6_);
        }

        public void test(ChunkGenerator<?> generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn) {
            fireVillageStructureStartEvent(this);
        }
    }

    private static final class Mocker2 {
        /**
         * Mock of callsite for fireCreateVillagerBabyEvent
         */
        public void test(ServerWorld p_220477_1_, VillagerEntity p_220477_2_, BlockPos p_220477_3_) {
            GlobalPos globalpos = GlobalPos.of(p_220477_1_.getDimension().getType(), p_220477_3_);
            fireCreateVillagerBabyEvent(p_220477_1_, p_220477_2_, globalpos);
        }
    }
}
