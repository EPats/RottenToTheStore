package io.github.epats.rottentothestore.client.event;

import io.github.epats.rottentothestore.RottenToTheStore;
import io.github.epats.rottentothestore.common.ItemRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RottenToTheStore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeEventHandler {

    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public static void onTooltipEvent(ItemTooltipEvent event) {
        if(!event.getItemStack().isEmpty() && event.getItemStack().getItem() == ItemRegistry.BLANK_SLOT.get())
            event.getToolTip().clear();
    }

}
