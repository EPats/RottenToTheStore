package io.github.epats.rottentothestore.mixin;

import io.github.epats.rottentothestore.common.ItemRegistry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public class MixinSlot {

    @Inject(at = @At("HEAD"), method = "mayPlace(Lnet/minecraft/world/item/ItemStack;)Z", cancellable = true)
    private void mayPlace(ItemStack pStack, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (!pStack.isEmpty() && pStack.getItem() == ItemRegistry.BLANK_SLOT.get()) {
            callbackInfoReturnable.setReturnValue(false);
        }
    }

    @Inject(at = @At("HEAD"), method = "mayPickup(Lnet/minecraft/world/entity/player/Player;)Z", cancellable = true)
    private void mayPickup(Player pPlayer, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        ItemStack stack = this.getItem();
        if (!stack.isEmpty() && stack.getItem() == ItemRegistry.BLANK_SLOT.get()) {
            callbackInfoReturnable.setReturnValue(false);
        }
    }

    @Shadow
    public ItemStack getItem() {
        throw new IllegalStateException("Mixin failed to shadow getItem()");
    }
}
