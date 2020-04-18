package ar.com.messupetru.expansivevillages;

import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ExpansiveVillagesMod.MOD_ID)
public class ExpansiveVillagesMod {
    public static final String MOD_ID = "expansivevillages";

    public static final HashMap<UUID, HouseCreatorManager> HOUSE_CREATOR_MANAGER = new HashMap<>();

    private static final Logger LOGGER = LogManager.getLogger();

    public ExpansiveVillagesMod() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static class HouseCreatorManager {
        private static final int MAX_SPAWN_PROBABILITY = 80;
        private int spawnProbability = 0;
        
        public void setAmountOfBeds(long amountFromChild) {
            spawnProbability = (int) (MAX_SPAWN_PROBABILITY - amountFromChild * 10);
        }
    }

    @Mod.EventBusSubscriber(modid = ExpansiveVillagesMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onNewbornVillager(final CreateBabyVillagerEvent event) {
            VillagerEntity child = event.child;

            java.util.function.Predicate<PointOfInterestType> type = PointOfInterestType.HOME.func_221045_c();
            BlockPos villagerPosition = new BlockPos(child);
            int distance = 48;
            Stream<PointOfInterest> pointOfInterestStream = event.serverWorld.getPointOfInterestManager()
                    .func_219146_b(type, villagerPosition, distance, PointOfInterestManager.Status.HAS_SPACE)
                    .filter((pointOfInterest) -> pathfindToBed(child, pointOfInterest.getPos()));

            HOUSE_CREATOR_MANAGER.putIfAbsent(child.getUniqueID(), new HouseCreatorManager());
            HOUSE_CREATOR_MANAGER.get(child.getUniqueID()).setAmountOfBeds(pointOfInterestStream.count());

            LOGGER.info("Newborn " + child.getPosition().toString() + ": ");
        }

        private static boolean pathfindToBed(VillagerEntity villager, BlockPos blockPos) {
            Path path = villager.getNavigator().getPathToPos(blockPos, PointOfInterestType.HOME.func_225478_d());
            return path != null && path.func_224771_h();
        }
    }
}
