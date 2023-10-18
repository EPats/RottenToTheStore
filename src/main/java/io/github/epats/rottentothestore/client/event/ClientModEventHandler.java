package io.github.epats.rottentothestore.client.event;

import io.github.epats.rottentothestore.RottenToTheStore;
import io.github.epats.rottentothestore.client.screen.ClientCustomBundleTooltip;
import io.github.epats.rottentothestore.common.inventory.tooltip.CustomBundleTooltip;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RottenToTheStore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEventHandler {

    @SubscribeEvent
    public static void registerTooltip(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(CustomBundleTooltip.class, ClientCustomBundleTooltip::new);
    }
}
