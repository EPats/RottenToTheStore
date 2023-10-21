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

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class ItemBundle extends Item implements StorageItem {
    private int numberOfSlots;
    private int maxWeight;
    private Predicate<ItemStack> canAcceptItem;

    public ItemBundle(Properties prop, int numberOfSlots, int maxWeight, Predicate<ItemStack> itemRestrictions) {
        super(prop);
        this.numberOfSlots = numberOfSlots;
        this.maxWeight = maxWeight;
        this.canAcceptItem = itemRestrictions;
    }

    @Override
    public int getNumberOfSlots() {
        return this.numberOfSlots;
    }

    @Override
    public int getMaxWeight() {
        return this.maxWeight;
    }

    @Override
    public float getFullnessDisplay(ItemStack pStack) {
        return (float) InventoryHelper.getWeightOfBagContents(pStack) / ((float) maxWeight);
    }

    @Override
    public boolean canBagTakeItem(ItemStack stackToInsert) {
        return this.canAcceptItem.test(stackToInsert);
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack bundle, Slot slot, ClickAction action, Player player) {
        if(!this.canBagTakeItem(slot.getItem()))
            return false;
        return InventoryHelper.bagItemStackedOnSlot(bundle, slot, action, player, this.maxWeight, this.numberOfSlots);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack bundle, ItemStack itemIn, Slot slot, ClickAction action,
                                            Player player, SlotAccess slotAccess) {
        if(!itemIn.isEmpty() && !this.canBagTakeItem(itemIn))
            return false;
        return InventoryHelper.itemStackedOnBag(bundle, itemIn, slot, action, player, slotAccess, this.maxWeight, this.numberOfSlots);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return InventoryHelper.getToolTipImage(stack, this.numberOfSlots, this.maxWeight);
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @Override
    public void appendHoverText(ItemStack pStack, Level pLevel, List<Component> pTooltipComponents,
                                TooltipFlag pIsAdvanced) {
        InventoryHelper.addHoverText(pStack, pTooltipComponents, this.maxWeight);
    }

    /**
     * Called to trigger the item's "innate" right click behavior.
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
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
    public boolean isBarVisible(ItemStack pStack) {
        return InventoryHelper.isFullnessBarVisible(pStack);
    }

    @Override
    public int getBarWidth(ItemStack pStack) {
        return InventoryHelper.getFullnessBarWidth(pStack, this.maxWeight);
    }

    @Override
    public int getBarColor(ItemStack pStack) {
        return InventoryHelper.getFullnessBarColor(pStack);
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }

//    /**
//     * Returns true if the item can be used on the given entity, e.g. shears on sheep.
//     */
//    public InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {
//        if(!pPlayer.getLevel().isClientSide) {
//            pPlayer.getLevel().getCapability(LevelChestProvider.levelChestCap).ifPresent(levelChest -> {
//                levelChest.addEntity(pStack, pPlayer, pInteractionTarget);
//            });
//        }
//        return InteractionResult.SUCCESS;
//    }
}
