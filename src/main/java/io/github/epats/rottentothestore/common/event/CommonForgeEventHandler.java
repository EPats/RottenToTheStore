package io.github.epats.rottentothestore.common.event;

import io.github.epats.rottentothestore.RottenToTheStore;
import io.github.epats.rottentothestore.common.ItemRegistry;
import io.github.epats.rottentothestore.common.item.ItemBlankSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;

@Mod.EventBusSubscriber(modid = RottenToTheStore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonForgeEventHandler {

    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public static void onEntityJoinEvent(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        ItemBlankSlot.fillOrRemoveBlankSlots(player);
    }

    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public static void onEvent(PlayerEvent.PlayerChangeGameModeEvent event) {
        RottenToTheStore.LOGGER.info("hi");
        Player player = event.getEntity();
        ItemBlankSlot.fillOrRemoveBlankSlots(player,
                event.getNewGameMode() == GameType.CREATIVE || event.getNewGameMode() == GameType.SPECTATOR);
    }

    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public static void onEvent(LivingDropsEvent event) {
        Collection<ItemEntity> c = event.getDrops();
        c.removeIf(i -> i.getItem().getItem() == ItemRegistry.BLANK_SLOT.get());
    }
}
