package io.github.epats.rottentothestore.common;


import io.github.epats.rottentothestore.common.inventory.tooltip.CustomBundleTooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class InventoryHelper {

    private static final int BAR_COLOR = Mth.color(0.4F, 0.4F, 1.0F);
    private static final String TAG_ITEMS = "Items";
    private static final String TAG_WEARABLE = "Wearable";

    public static int getWeightOfSingleItemFromItemStack(ItemStack itemStack) {
        if(!itemStack.getItem().canFitInsideContainerItems())
            return 65;
        if ((itemStack.is(Items.BEEHIVE) || itemStack.is(Items.BEE_NEST)) && itemStack.hasTag()) {
            CompoundTag compoundtag = BlockItem.getBlockEntityData(itemStack);
            if (compoundtag != null && !compoundtag.getList("Bees", 10).isEmpty()) {
                return 64;
            }
        }

        return 64 / itemStack.getMaxStackSize();
    }

    public static int getWeightOfWholeItemStack(ItemStack itemStack) {
        return getWeightOfSingleItemFromItemStack(itemStack) * itemStack.getCount();
    }

    public static int getWeightOfBagContents(ItemStack bagItemStack) {
        return getContentsFromBag(bagItemStack).mapToInt((itemStack) ->
                getWeightOfWholeItemStack(itemStack))
                .sum();
    }

    public static int addItemStackToBag(ItemStack bagItemStack, ItemStack insertedItemStack, int bagMaxWeight) {
        if (insertedItemStack.isEmpty() || !insertedItemStack.getItem().canFitInsideContainerItems())
            return 0;

        CompoundTag bagTag = bagItemStack.getOrCreateTag();
        ListTag itemListTag = bagTag.getList(TAG_ITEMS, 10);

        int currentWeight = getWeightOfBagContents(bagItemStack);
        int weightOfInsertedItem = getWeightOfSingleItemFromItemStack(insertedItemStack);
        int maxInsertableCount = Math.min(insertedItemStack.getCount(), (bagMaxWeight - currentWeight) / weightOfInsertedItem);

        if (maxInsertableCount == 0)
            return 0;

        Optional<CompoundTag> matchingItemTag = getMatchingItemWithSpace(insertedItemStack, itemListTag);

        if (matchingItemTag.isPresent()) {
            CompoundTag existingItemTag = matchingItemTag.get();
            ItemStack existingItemStack  = ItemStack.of(existingItemTag);
            existingItemStack.grow(maxInsertableCount);
            existingItemStack.save(existingItemTag);
            itemListTag.remove(existingItemTag);
            itemListTag.add(0, existingItemTag);
        } else {
            ItemStack newItemStack  = insertedItemStack.copy();
            newItemStack.setCount(maxInsertableCount);
            CompoundTag newItemTag  = new CompoundTag();
            newItemStack.save(newItemTag );
            itemListTag.add(0, newItemTag );
        }

        return maxInsertableCount;
    }

    public static Optional<CompoundTag> getMatchingItemWithSpace(ItemStack itemStackToMatch, ListTag itemListTag) {
        return itemListTag.stream().filter(CompoundTag.class::isInstance).map(CompoundTag.class::cast)
                .filter((compoundTag) ->
                    ItemStack.isSameItemSameTags(ItemStack.of(compoundTag), itemStackToMatch)
                    && ItemStack.of(compoundTag).getCount() < ItemStack.of(compoundTag).getMaxStackSize()
                ).findFirst();
    }

    private static Optional<ItemStack> getMatchingItemStack(ItemStack itemStackToMatch, ItemStack bagItemStack) {
        return bagItemStack.getOrCreateTag().getList(TAG_ITEMS, 10)
                .stream().filter(CompoundTag.class::isInstance).map(CompoundTag.class::cast)
                .map(compoundTag -> ItemStack.of(compoundTag))
                .filter((itemStack) ->
                        ItemStack.isSameItemSameTags(itemStack, itemStackToMatch)
                ).findFirst();
    }

    public static Optional<ItemStack> removeLastInsertedItemStack(ItemStack bagItemStack) {
        CompoundTag compoundtag = bagItemStack.getOrCreateTag();

        if (!compoundtag.contains(TAG_ITEMS))
            return Optional.empty();

        ListTag listtag = compoundtag.getList(TAG_ITEMS, 10);
        if (listtag.isEmpty())
            return Optional.empty();

        CompoundTag lastItemTag = listtag.getCompound(0);
        ItemStack lastItemStack = ItemStack.of(lastItemTag);
        listtag.remove(0);

        if (listtag.isEmpty()) {
            bagItemStack.removeTagKey(TAG_ITEMS);
        }

        return Optional.of(lastItemStack);
    }

    public static boolean dropContents(ItemStack bagItemStack, Player player) {
        CompoundTag bagTag = bagItemStack.getOrCreateTag();
        if (!bagTag.contains(TAG_ITEMS))
            return false;

        if(!player.level().isClientSide())
            dropContentsInWorld(bagItemStack, player, bagTag);

        bagItemStack.removeTagKey(TAG_ITEMS);
        return true;
    }

    public static void dropContentsInWorld(ItemStack bagItemStack, Player player, CompoundTag bagTag) {
        ListTag listtag = bagTag.getList(TAG_ITEMS, 10);

        for (int i = 0; i < listtag.size(); ++i) {
            CompoundTag itemStackTag = listtag.getCompound(i);
            ItemStack itemstack = ItemStack.of(itemStackTag);
            player.drop(itemstack, true);
        }
    }

    public static Stream<ItemStack> getContentsFromBag(ItemStack bagItemStack) {
        CompoundTag bagTag = bagItemStack.getTag();
        if (bagTag == null || !bagTag.contains(TAG_ITEMS))
            return Stream.empty();

        ListTag listtag = bagTag.getList(TAG_ITEMS, 10);
        return listtag.stream().map(CompoundTag.class::cast).map(ItemStack::of);
    }



    public static boolean bagItemStackedOnSlot(ItemStack bagItemStack, Slot slot, ClickAction action, Player player, int maxWeight, int numberOfSlots) {
        if (action != ClickAction.SECONDARY)
            return false;

        ItemStack itemStackedOn = slot.getItem();

        if (!itemStackedOn.isEmpty() ||
                !itemStackedOn.getItem().canFitInsideContainerItems())
            return false;

        boolean isSpace = hasSpaceInBag(bagItemStack, itemStackedOn, numberOfSlots);
        boolean canBeStackedOn = canBagStackOnItemStack(bagItemStack, itemStackedOn);

        if(!isSpace && !canBeStackedOn)
            return false;

        if (itemStackedOn.isEmpty()) {
            Optional<ItemStack> optional = removeLastInsertedItemStack(bagItemStack);

            optional.ifPresent(itemStack -> {
                ItemStack leftoverItemStack = slot.safeInsert(itemStack);
                addItemStackToBag(bagItemStack, leftoverItemStack, maxWeight);
            });
        } else if (!isSpace && canBeStackedOn) {
            Optional<ItemStack> optional = getMatchingItemStack(itemStackedOn, bagItemStack);

            optional.ifPresent(itemStack -> {
                ItemStack leftoverItemStack = slot.safeInsert(itemStack);
                addItemStackToBag(bagItemStack, leftoverItemStack, maxWeight);
            });
        } else {
            boolean insertItemStackIntoBag = insertItemStackIntoBag(bagItemStack, itemStackedOn, maxWeight);
            if (insertItemStackIntoBag) {
                playInsertSound(player);
            }
        }
        return true;
    }

    private static boolean insertItemStackIntoBag(ItemStack bagItemStack, ItemStack itemStackedOn, int maxWeight) {
        int availableWeight = maxWeight - getWeightOfBagContents(bagItemStack);
        int maxInsertCount = availableWeight / getWeightOfSingleItemFromItemStack(itemStackedOn);
        int actualInsertCount = addItemStackToBag(bagItemStack, itemStackedOn, maxWeight);

        return Math.min(actualInsertCount, maxInsertCount) > 0;
    }

    private static boolean hasSpaceInBag(ItemStack bagItemStack, ItemStack itemStackedOn, int numberOfSlots) {
        return getContentsFromBag(bagItemStack).anyMatch(itemStack ->
                stackCanStackWith(itemStack, itemStackedOn)
                || (numberOfSlots - getContentsFromBag(bagItemStack).count()) > 0);
    }

    private static boolean canBagStackOnItemStack(ItemStack bagItemStack, ItemStack itemStackedOn) {
        return getContentsFromBag(bagItemStack).anyMatch(itemStack ->
                stackCanStackWith(itemStack, itemStackedOn));
    }

    private static boolean stackCanStackWith(ItemStack stack1, ItemStack stack2) {
        return ItemStack.isSameItemSameTags(stack1, stack2) && stack1.getCount() < stack1.getMaxStackSize();
    }



    public static boolean stackOnMe(ItemStack bundle, ItemStack itemIn, Slot slot, ClickAction action, Player player,
                                    SlotAccess slotAccess, int maxWeight) {
        boolean ret = false;
        if (action == ClickAction.SECONDARY && slot.allowModification(player)) {
            boolean isSpace = getContentsFromBag(bundle).anyMatch(stack -> stack.getItem() == itemIn.getItem())
                    || 2 - getContentsFromBag(bundle).mapToInt(stack -> {
                return 1;
            }).sum() > 0;
            if (itemIn.isEmpty()) {
                Optional<ItemStack> optional = removeLastInsertedItemStack(bundle);
                optional.ifPresent((p_186347_) -> {
                    playRemoveOneSound(player);
                    slotAccess.set(p_186347_);
                });
                ret = optional.isPresent();
            } else if(isSpace) {
                int i = addItemStackToBag(bundle, itemIn, maxWeight);
                if (i > 0) {
                    playInsertSound(player);
                    itemIn.shrink(i);
                    ret = true;
                }
            }
        }
        return ret;
    }

    public static void onDestroyed(ItemEntity pItemEntity) {
        ItemUtils.onContainerDestroyed(pItemEntity, getContentsFromBag(pItemEntity.getItem()));
    }

    public static void playRemoveOneSound(Entity pEntity) {
        pEntity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F,
                0.8F + pEntity.level().getRandom().nextFloat() * 0.4F);
    }

    public static void playInsertSound(Entity pEntity) {
        pEntity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + pEntity.level().getRandom().nextFloat() * 0.4F);
    }

    public static void playDropContentsSound(Entity pEntity) {
        pEntity.playSound(SoundEvents.BUNDLE_DROP_CONTENTS, 0.8F,
                0.8F + pEntity.level().getRandom().nextFloat() * 0.4F);
    }

    public static Optional<TooltipComponent> getToolTipImage(ItemStack stack, int size) {
        NonNullList<ItemStack> nonnulllist = NonNullList.create();
        getContentsFromBag(stack).forEach(nonnulllist::add);
        int maxWeight = 64;
        if(stack.getItem() instanceof BundleItem bundle) {
            maxWeight = bundle.MAX_WEIGHT;
        }
        return Optional.of(new CustomBundleTooltip(nonnulllist, getWeightOfBagContents(stack), maxWeight, size));
    }

    public static void addHoverText(ItemStack pStack, List<Component> pTooltipComponents, int size) {
        pTooltipComponents.add(
                Component.translatable("item.minecraft.bundle.fullness", InventoryHelper.getWeightOfBagContents(pStack), size)
                        .withStyle(ChatFormatting.GRAY));
    }

    public static boolean isFullnessBarVisible(ItemStack pStack) {
        return getWeightOfBagContents(pStack) > 0;
    }

    public static int getFullnessBarWidth(ItemStack pStack, int maxWeight) {
        return Math.min(1 + 12 * getWeightOfBagContents(pStack) / maxWeight, 13);
    }

    public static int getFullnessBarColor(ItemStack pStack) {
        return BAR_COLOR;
    }

    // From old code?
//    public static Optional<ItemStack> getCarriedBagFromCompoundWearable(ItemStack wearableBagItemStack) {
//        CompoundTag bagTag = wearableBagItemStack.getTag();
//        if (bagTag == null)
//            return Optional.empty();
//
//        ListTag listtag = bagTag.getList(TAG_WEARABLE, 10);
//        return listtag.stream().findFirst().map(CompoundTag.class::cast).map(ItemStack::of);
//    }
//
//    public static ItemStack getCarriedBagFromCompoundWearableSafe(ItemStack wearableBagItemStack) {
//        Optional<ItemStack> optional = getCarriedBagFromCompoundWearable(wearableBagItemStack);
//        if (optional.isPresent()) {
//            return optional.get();
//        } else {
//            ItemStack newBundle = new ItemStack(Items.ACACIA_BOAT);//ItemRegistry.GRASS_BUNDLE.get());
//            setCarriedBundle(wearableBagItemStack, newBundle);
//            return newBundle;
//        }
//    }
//
//    public static void setCarriedBundle(ItemStack wearableBundle, ItemStack carriedBundle) {
//        CompoundTag compoundtag = wearableBundle.getOrCreateTag();
//        if (!compoundtag.contains(TAG_WEARABLE)) {
//            compoundtag.put(TAG_WEARABLE, new ListTag());
//        }
//        ListTag listtag = compoundtag.getList(TAG_WEARABLE, 10);
//        ItemStack itemstack1 = carriedBundle.copy();
//        CompoundTag compoundtag2 = new CompoundTag();
//        itemstack1.save(compoundtag2);
//        listtag.add(0, (Tag) compoundtag2);
//    }

}
