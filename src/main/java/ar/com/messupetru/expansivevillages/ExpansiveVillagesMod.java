package ar.com.messupetru.expansivevillages;

import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ExpansiveVillagesMod.MOD_ID)
public class ExpansiveVillagesMod {
    public static final String MOD_ID = "expansivevillages";

    private static final Logger LOGGER = LogManager.getLogger();

    public ExpansiveVillagesMod() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventBusSubscriber(modid = ExpansiveVillagesMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onNewbornVillager(final CreateBabyVillagerEvent event) {
            VillagerEntity villager = (VillagerEntity) event.getEntityLiving();
            if(villager.isChild()) {
                LOGGER.info("Newborn!");

            }
            event.setResult(Event.Result.DEFAULT);
        }
    }
}
