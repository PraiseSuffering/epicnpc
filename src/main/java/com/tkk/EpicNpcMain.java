package com.tkk;

import com.tkk.npcepicfight.entitypatch.CustomNpcPatch;
import com.tkk.npcepicfight.network.EpicNpcNetworkManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import noppes.npcs.CustomEntities;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yesman.epicfight.api.forgeevent.EntityPatchRegistryEvent;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("epicnpc")
public class EpicNpcMain
{
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public EpicNpcMain() {
        // Register ourselves for server and other game events we are interested in
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
        bus.addListener(this::doCommonStuff);
    }
    private void doCommonStuff(FMLCommonSetupEvent event) {
        event.enqueueWork(EpicNpcNetworkManager::registerPackets);
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void entityPatchRegistryEvent(EntityPatchRegistryEvent event){
            Map<EntityType<?>, Function<Entity, Supplier<EntityPatch<?>>>> map=event.getTypeEntry();
            map.put(CustomEntities.entityCustomNpc, (entityIn) -> CustomNpcPatch::new);
        }


    }
}


