package io.github.epats.rottentothestore.common.item;

import io.github.epats.rottentothestore.common.InventoryHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class ItemBag extends Item implements StorageItem {
    private final int NUMBER_OF_SLOTS;
    private final int MAX_WEIGHT;
    protected final Predicate<ItemStack> CAN_ACCEPT_ITEM_FN;

    public ItemBag(Properties prop, int numberOfSlots, int maxWeight, Predicate<ItemStack> itemRestrictions) {
        super(prop);
        this.NUMBER_OF_SLOTS = numberOfSlots;
        this.MAX_WEIGHT = maxWeight;
        this.CAN_ACCEPT_ITEM_FN = itemRestrictions;
    }

    @Override
    public int getNUMBER_OF_SLOTS() {
        return this.NUMBER_OF_SLOTS;
    }

    @Override
    public int getMAX_WEIGHT() {
        return this.MAX_WEIGHT;
    }

    @Override
    public float getFullnessDisplay(ItemStack pStack) {
        return (float) InventoryHelper.getWeightOfBagContents(pStack) / ((float) MAX_WEIGHT);
    }

    @Override
    public boolean canBagTakeItem(ItemStack stackToInsert) {
        return this.CAN_ACCEPT_ITEM_FN.test(stackToInsert);
    }

    @Override
    public boolean overrideStackedOnOther(@NotNull ItemStack bundle, Slot slot, @NotNull ClickAction action, @NotNull Player player) {
        if(!this.canBagTakeItem(slot.getItem()))
            return false;
        return InventoryHelper.bagItemStackedOnSlot(bundle, slot, action, player, this.MAX_WEIGHT, this.NUMBER_OF_SLOTS);
    }

    @Override
    public boolean overrideOtherStackedOnMe(@NotNull ItemStack bundle, @NotNull ItemStack itemIn, @NotNull Slot slot, @NotNull ClickAction action,
                                            @NotNull Player player, @NotNull SlotAccess slotAccess) {
        if(!itemIn.isEmpty() && !this.canBagTakeItem(itemIn))
            return false;
        return InventoryHelper.itemStackedOnBag(bundle, itemIn, slot, action, player, slotAccess, this.MAX_WEIGHT, this.NUMBER_OF_SLOTS);
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack stack) {
        return InventoryHelper.getToolTipImage(stack, this.NUMBER_OF_SLOTS, this.MAX_WEIGHT);
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @Override
    public void appendHoverText(@NotNull ItemStack pStack, Level pLevel, @NotNull List<Component> pTooltipComponents,
                                @NotNull TooltipFlag pIsAdvanced) {
        InventoryHelper.addHoverText(pStack, pTooltipComponents, this.MAX_WEIGHT);
    }

    /**
     * Called to trigger the item's "innate" right click behavior.
     */
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);
        if (InventoryHelper.dropContents(itemstack, pPlayer)) {
            InventoryHelper.playDropContentsSound(pPlayer);
            pPlayer.awardStat(Stats.ITEM_USED.get(this));
            return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
        } else {
            return InteractionResultHolder.fail(itemstack);
        }
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack pStack) {
        return InventoryHelper.isFullnessBarVisible(pStack);
    }

    @Override
    public int getBarWidth(@NotNull ItemStack pStack) {
        return InventoryHelper.getFullnessBarWidth(pStack, this.MAX_WEIGHT);
    }

    @Override
    public int getBarColor(@NotNull ItemStack pStack) {
        return InventoryHelper.getFullnessBarColor(pStack);
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }
}
