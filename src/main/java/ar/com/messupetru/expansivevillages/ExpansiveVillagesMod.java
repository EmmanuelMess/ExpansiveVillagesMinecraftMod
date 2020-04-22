package ar.com.messupetru.expansivevillages;

import ar.com.messupetru.expansivevillages.events.CreateBabyVillagerEvent;
import ar.com.messupetru.expansivevillages.events.VillageStructureStartEvent;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StructureBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.network.play.client.CUpdateStructureBlockPacket;
import net.minecraft.state.properties.BedPart;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ExpansiveVillagesMod.MOD_ID)
public class ExpansiveVillagesMod {
    public static final String MOD_ID = "expansivevillages";

    public static final HouseCreatorManager HOUSE_CREATOR_MANAGER = new HouseCreatorManager();

    private static final Logger LOGGER = LogManager.getLogger();

    public ExpansiveVillagesMod() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static class HouseCreatorManager {
        private static final int ALL_VILLAGERS_HAVE_BEDS_PROBABILITY = 20;
        private static final int BEDS_ARE_MISSING_PROBABILITY = 80;

        private final Random rand = new Random();
        private int spawnProbability = 0;
        private Set<VillageHousing> villages = Collections.synchronizedSet(new HashSet<>());
        private Set<VillageHousing> dirtyVillages = Collections.synchronizedSet(new HashSet<>());
        private int counter = 0;

        public HouseCreatorManager() {

        }

        public void worldTick(World world) {
            if(counter < 20) {
                counter++;
                return;
            } else {
                counter = 0;
            }

            processDirty(world);

            villages.removeIf((villageHousing) -> {
                if(villageHousing.getVillagers() <= 1) {
                    LOGGER.info("Village is dead! Villagers: " + villageHousing.getVillagers());
                    dirtyVillages.add(villageHousing);
                    return true;
                }

                float ratio = villageHousing.getBeds()/(float)villageHousing.getVillagers();

                if(ratio >= 1.5) spawnProbability = 0;
                else if(ratio >= 1.0) spawnProbability = ALL_VILLAGERS_HAVE_BEDS_PROBABILITY;
                else spawnProbability = BEDS_ARE_MISSING_PROBABILITY;

                LOGGER.info("Tick for " + villageHousing.start.getPos() + ", ratio is " + ratio);

                if(rand.nextInt(100) < spawnProbability) {
                    MutableBoundingBox box = villageHousing.start.getBoundingBox();
                    int x = rand.ints(1, box.minX, box.maxX).sum();
                    int z = rand.ints(1, box.minZ, box.maxZ).sum();
                    BlockPos center = new BlockPos(x, 4, z);
                    world.setBlockState(center, Blocks.STRUCTURE_BLOCK.getDefaultState()
                            .with(StructureBlock.MODE, StructureMode.LOAD));

                    StructureBlockTileEntity entity  = (StructureBlockTileEntity) world.getTileEntity(center);
                    entity.setMode(StructureMode.LOAD);
                    entity.setName("village/plains/houses/plains_medium_house_1");
                    entity.setPosition(new BlockPos(5, 0, 5));
                    entity.load();

                    ratio = villageHousing.getBeds() / (float) villageHousing.getVillagers();

                    LOGGER.info("Creating home! New bed/villager: " + ratio + " Location: " + center);
                }

                dirtyVillages.add(villageHousing);
                return true;
            });
        }

        private void processDirty(World world) {
            dirtyVillages.forEach(village -> {
                MutableBoundingBox boundingBox = village.start.getBoundingBox();

                long amountBeds = BlockPos.getAllInBox(boundingBox)
                        .filter((blockPos) -> world.getBlockState(blockPos).getBlock() instanceof BedBlock)
                        .count();
                village.setBeds((int) amountBeds);

                long amountOfVillagers = world.getEntitiesWithinAABB(EntityType.VILLAGER, AxisAlignedBB.toImmutable(boundingBox), x -> true)
                        .size();
                village.setVillagers((int) amountOfVillagers);

                villages.add(village);
            });
            dirtyVillages.clear();
        }

        public void addVillage(VillageHousing village) {
            dirtyVillages.add(village);
        }
    }

    @Mod.EventBusSubscriber(modid = ExpansiveVillagesMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onNewbornVillager(final CreateBabyVillagerEvent event) {
            VillagerEntity child = event.child;
            BlockPos position = child.getPosition();

            HOUSE_CREATOR_MANAGER.villages
                    .removeIf(villageHousing -> {
                        if(villageHousing.start.getBoundingBox().isVecInside(position)) {
                            HOUSE_CREATOR_MANAGER.dirtyVillages.add(villageHousing);
                            return true;
                        }

                        return false;
                    });

            LOGGER.info("Newborn " + child.getPosition().toString() );
        }

        @SubscribeEvent
        public static void onWorldTick(TickEvent.WorldTickEvent tickEvent) {
            HOUSE_CREATOR_MANAGER.worldTick(tickEvent.world);
        }

        @SubscribeEvent
        public static void onVillageGenerated(VillageStructureStartEvent structureStartEvent) {
            LOGGER.debug("New village!");
            MutableBoundingBox components = structureStartEvent.start.getBoundingBox();
            LOGGER.debug("Structure: " + components);

            HOUSE_CREATOR_MANAGER.addVillage(new VillageHousing(structureStartEvent.start));
        }
    }
}
