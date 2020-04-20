package ar.com.messupetru.expansivevillages;

import ar.com.messupetru.expansivevillages.events.CreateBabyVillagerEvent;
import ar.com.messupetru.expansivevillages.events.VillageStructureStartEvent;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.state.properties.BedPart;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Random;
import java.util.stream.Stream;

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
        private static final int BEDS_ARE_MISSING = 80;

        private final Random rand = new Random();
        private int spawnProbability = 0;
        private HashSet<VillageHousing> housings = new HashSet<>();
        private HashSet<VillageHousing> notCached = new HashSet<>();

        public HouseCreatorManager() {

        }

        public void worldTick(World world) {
            addToCache(world);

            housings.removeIf((villageHousing) -> {
                if(villageHousing.getVillagers() <= 1) {
                    LOGGER.info("Village is dead! Villagers: " villageHousing.getVillagers());
                    //return true;
                    return false;
                }

                float ratio = villageHousing.getBeds()/(float)villageHousing.getVillagers();

                if(ratio >= 1.5) spawnProbability = 0;
                else if(ratio >= 1.0) spawnProbability = ALL_VILLAGERS_HAVE_BEDS_PROBABILITY;
                else spawnProbability = BEDS_ARE_MISSING;

                LOGGER.info("Tick, Probability is " + spawnProbability);

                if(rand.nextInt(100) <= spawnProbability) {
                    BlockPos block1 = new BlockPos(0, 3, 0);
                    BlockPos block2 = new BlockPos(0, 3, 1);
                    BlockPos block3 = new BlockPos(0, 4, 0);
                    BlockPos block4 = new BlockPos(0, 4, 1);
                    world.setBlockState(block1, Blocks.STONE.getDefaultState());
                    world.setBlockState(block2, Blocks.STONE.getDefaultState());
                    world.setBlockState(block3, Blocks.RED_BED.getDefaultState().with(BedBlock.PART, BedPart.HEAD));
                    world.setBlockState(block4, Blocks.RED_BED.getDefaultState());
                    LOGGER.info("Creating home!");
                    LOGGER.info(Blocks.RED_BED.getDefaultState().toString());
                }

                return false;
            });
        }

        private void addToCache(World world) {
            notCached.forEach(villageHousing -> {
                MutableBoundingBox boundingBox = villageHousing.start.getBoundingBox();

                long amountBeds = BlockPos.getAllInBox(boundingBox)
                        .filter((blockPos) -> world.getBlockState(blockPos).getBlock() instanceof BedBlock)
                        .count();
                villageHousing.setBeds((int) amountBeds);

                long amountOfVillagers = world.getEntitiesWithinAABB(EntityType.VILLAGER, AxisAlignedBB.toImmutable(boundingBox), x -> true)
                        .size();
                villageHousing.setVillagers((int) amountOfVillagers);
            });
            notCached.clear();
        }

        public void addVillage(VillageHousing village) {
            housings.add(village);
            notCached.add(village);
        }
    }

    @Mod.EventBusSubscriber(modid = ExpansiveVillagesMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onNewbornVillager(final CreateBabyVillagerEvent event) {
            /*
            VillagerEntity child = event.child;

            java.util.function.Predicate<PointOfInterestType> type = PointOfInterestType.HOME.func_221045_c();
            BlockPos villagerPosition = new BlockPos(child);
            int distance = 48;
            Stream<PointOfInterest> pointOfInterestStream = event.serverWorld.getPointOfInterestManager()
                    .func_219146_b(type, villagerPosition, distance, PointOfInterestManager.Status.HAS_SPACE)
                    .filter((pointOfInterest) -> pathfindToBed(child, pointOfInterest.getPos()));

            HOUSE_CREATOR_MANAGER.putIfAbsent(child.getUniqueID(), new HouseCreatorManager(child));
            HOUSE_CREATOR_MANAGER.get(child.getUniqueID()).setAmountOfBeds(pointOfInterestStream.count());

            LOGGER.info("Newborn " + child.getPosition().toString() + ": ");
            */
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

        private static boolean pathfindToBed(VillagerEntity villager, BlockPos blockPos) {
            Path path = villager.getNavigator().getPathToPos(blockPos, PointOfInterestType.HOME.func_225478_d());
            return path != null && path.func_224771_h();
        }
    }
}
