package io.github.epats.rottentothestore.client.event;

import io.github.epats.rottentothestore.RottenToTheStore;
import io.github.epats.rottentothestore.client.WearableStorageLayer;
import io.github.epats.rottentothestore.client.WearableStorageModel;
import io.github.epats.rottentothestore.client.screen.ClientCustomBundleTooltip;
import io.github.epats.rottentothestore.common.inventory.tooltip.CustomBundleTooltip;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;

@Mod.EventBusSubscriber(modid = RottenToTheStore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEventHandler {

    @SubscribeEvent
    public static void registerTooltip(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(CustomBundleTooltip.class, ClientCustomBundleTooltip::new);
    }

        @SubscribeEvent
    public static void registerEntityLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(WearableStorageLayer.WEARABLE_STORAGE_MODEL_LAYER, WearableStorageLayer::createBodyLayer);
    }

    @SubscribeEvent
    public static void addLayersToEntities(final EntityRenderersEvent.AddLayers event) {

        Set<String> s = event.getSkins();
        for (String p : s) {
            PlayerRenderer player = event.getSkin(p);
            if (player != null) {
                player.addLayer(new WearableStorageLayer<>(player));
            }
        }

        ZombieRenderer zombie = event.getRenderer(EntityType.ZOMBIE);
        if (zombie != null) {
            zombie.addLayer(new WearableStorageLayer<>(zombie));
        }
    }
}
