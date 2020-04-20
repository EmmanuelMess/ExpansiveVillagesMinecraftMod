package ar.com.messupetru.expansivevillages;

import ar.com.messupetru.expansivevillages.events.CreateBabyVillagerEvent;
import ar.com.messupetru.expansivevillages.events.VillageStructureStartEvent;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.state.properties.BedPart;
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

import java.util.HashSet;
import java.util.Random;

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
        private HashSet<VillageHousing> villages = new HashSet<>();
        private HashSet<VillageHousing> dirtyVillages = new HashSet<>();
        
        public HouseCreatorManager() {

        }

        public void worldTick(World world) {
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

                    BlockPos block1 = new BlockPos(center);
                    BlockPos block2 = new BlockPos(center.add(0, 0, 1));
                    BlockPos block3 = new BlockPos(center.add(0, 1, 0));
                    BlockPos block4 = new BlockPos(center.add(0, 1, 1));
                    world.setBlockState(block1, Blocks.STONE.getDefaultState());
                    world.setBlockState(block2, Blocks.STONE.getDefaultState());
                    world.setBlockState(block3, Blocks.RED_BED.getDefaultState().with(BedBlock.PART, BedPart.HEAD));
                    world.setBlockState(block4, Blocks.RED_BED.getDefaultState());

                    ratio = villageHousing.getBeds()/(float)villageHousing.getVillagers();

                    LOGGER.info("Creating home! New bed/villager: " + ratio + " Location: " + center);

                    dirtyVillages.add(villageHousing);
                    return true;
                }

                return false;
            });
        }

        private void processDirty(World world) {
            dirtyVillages.forEach(villageHousing -> {
                MutableBoundingBox boundingBox = villageHousing.start.getBoundingBox();

                long amountBeds = BlockPos.getAllInBox(boundingBox)
                        .filter((blockPos) -> world.getBlockState(blockPos).getBlock() instanceof BedBlock)
                        .count();
                villageHousing.setBeds((int) amountBeds);

                long amountOfVillagers = world.getEntitiesWithinAABB(EntityType.VILLAGER, AxisAlignedBB.toImmutable(boundingBox), x -> true)
                        .size();
                villageHousing.setVillagers((int) amountOfVillagers);
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
