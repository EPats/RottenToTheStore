package io.github.epats.rottentothestore.common;


import io.github.epats.rottentothestore.common.inventory.tooltip.CustomBundleTooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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

    public static int getWeight(ItemStack stack) {
        if (stack.is(Items.BUNDLE)) {
            return 4 + getContentWeight(stack);
        } else {
            if ((stack.is(Items.BEEHIVE) || stack.is(Items.BEE_NEST)) && stack.hasTag()) {
                CompoundTag compoundtag = BlockItem.getBlockEntityData(stack);
                if (compoundtag != null && !compoundtag.getList("Bees", 10).isEmpty()) {
                    return 64;
                }
            }

            return 64 / stack.getMaxStackSize();
        }
    }

    public static int getContentWeight(ItemStack bundle) {
        return getContents(bundle).mapToInt((p_186356_) -> {
            return getWeight(p_186356_) * p_186356_.getCount();
        }).sum();
    }

    public static int add(ItemStack pBundleStack, ItemStack pInsertedStack, int maxWeight) {
        if (!pInsertedStack.isEmpty() && pInsertedStack.getItem().canFitInsideContainerItems()) {
            CompoundTag compoundtag = pBundleStack.getOrCreateTag();
            if (!compoundtag.contains(TAG_ITEMS)) {
                compoundtag.put(TAG_ITEMS, new ListTag());
            }

            int i = getContentWeight(pBundleStack);
            int j = getWeight(pInsertedStack);
            int k = Math.min(pInsertedStack.getCount(), (maxWeight - i) / j);
            if (k == 0) {
                return 0;
            } else {
                ListTag listtag = compoundtag.getList(TAG_ITEMS, 10);
                Optional<CompoundTag> optional = getMatchingItem(pInsertedStack, listtag);
                if (optional.isPresent()) {
                    CompoundTag compoundtag1 = optional.get();
                    ItemStack itemstack = ItemStack.of(compoundtag1);
                    itemstack.grow(k);
                    itemstack.save(compoundtag1);
                    listtag.remove(compoundtag1);
                    listtag.add(0, (Tag) compoundtag1);
                } else {
                    ItemStack itemstack1 = pInsertedStack.copy();
                    itemstack1.setCount(k);
                    CompoundTag compoundtag2 = new CompoundTag();
                    itemstack1.save(compoundtag2);
                    listtag.add(0, (Tag) compoundtag2);
                }

                return k;
            }
        } else {
            return 0;
        }
    }

    public static Optional<CompoundTag> getMatchingItem(ItemStack pStack, ListTag pList) {
        return pStack.is(Items.BUNDLE) ? Optional.empty()
                : pList.stream().filter(CompoundTag.class::isInstance).map(CompoundTag.class::cast)
                .filter((p_186350_) -> {
                    return ItemStack.isSameItemSameTags(ItemStack.of(p_186350_), pStack);
                }).findFirst();
    }

    public static Optional<ItemStack> removeOne(ItemStack pStack) {
        CompoundTag compoundtag = pStack.getOrCreateTag();
        if (!compoundtag.contains(TAG_ITEMS)) {
            return Optional.empty();
        } else {
            ListTag listtag = compoundtag.getList(TAG_ITEMS, 10);
            if (listtag.isEmpty()) {
                return Optional.empty();
            } else {
                CompoundTag compoundtag1 = listtag.getCompound(0);
                ItemStack itemstack = ItemStack.of(compoundtag1);
                listtag.remove(0);
                if (listtag.isEmpty()) {
                    pStack.removeTagKey(TAG_ITEMS);
                }

                return Optional.of(itemstack);
            }
        }
    }

    public static boolean dropContents(ItemStack pStack, Player pPlayer) {
        CompoundTag compoundtag = pStack.getOrCreateTag();
        if (!compoundtag.contains(TAG_ITEMS)) {
            return false;
        } else {
            if (pPlayer instanceof ServerPlayer) {
                ListTag listtag = compoundtag.getList(TAG_ITEMS, 10);

                for (int i = 0; i < listtag.size(); ++i) {
                    CompoundTag compoundtag1 = listtag.getCompound(i);
                    ItemStack itemstack = ItemStack.of(compoundtag1);
                    pPlayer.drop(itemstack, true);
                }
            }

            pStack.removeTagKey(TAG_ITEMS);
            return true;
        }
    }

    public static Stream<ItemStack> getContents(ItemStack pStack) {
        CompoundTag compoundtag = pStack.getTag();
        if (compoundtag == null) {
            return Stream.empty();
        } else {
            ListTag listtag = compoundtag.getList(TAG_ITEMS, 10);
            return listtag.stream().map(CompoundTag.class::cast).map(ItemStack::of);
        }
    }

    public static Optional<ItemStack> getCarriedBundle(ItemStack wearableBundle) {
        CompoundTag compoundtag = wearableBundle.getTag();
        if (compoundtag == null) {
            return Optional.empty();
        } else {
            ListTag listtag = compoundtag.getList("Wearable", 10);
            return listtag.stream().findFirst().map(CompoundTag.class::cast).map(ItemStack::of);
        }
    }

    public static ItemStack getCarriedBundleSafe(ItemStack wearableBundle) {
        Optional<ItemStack> optional = getCarriedBundle(wearableBundle);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            ItemStack newBundle = new ItemStack(Items.ACACIA_BOAT);//ItemRegistry.GRASS_BUNDLE.get());
            setCarriedBundle(wearableBundle, newBundle);
            return newBundle;
        }
    }

    public static void setCarriedBundle(ItemStack wearableBundle, ItemStack carriedBundle) {
        CompoundTag compoundtag = wearableBundle.getOrCreateTag();
        if (!compoundtag.contains("Wearable")) {
            compoundtag.put("Wearable", new ListTag());
        }
        ListTag listtag = compoundtag.getList("Wearable", 10);
        ItemStack itemstack1 = carriedBundle.copy();
        CompoundTag compoundtag2 = new CompoundTag();
        itemstack1.save(compoundtag2);
        listtag.add(0, (Tag) compoundtag2);
    }

    public static boolean stackMeOn(ItemStack bundle, Slot slot, ClickAction action, Player player, int maxWeight) {
        if (action != ClickAction.SECONDARY) {
            return false;
        } else {
            ItemStack itemIn = slot.getItem();
            boolean isSpace = getContents(bundle).anyMatch(stack -> stack.getItem() == itemIn.getItem())
                    || (2 - getContents(bundle).mapToInt(stack -> {
                return 1;
            }).sum()) > 0;
            if (!itemIn.isEmpty() && !isSpace) {
                return false;
            } else {
                boolean ret = false;
                if (itemIn.isEmpty()) {
                    Optional<ItemStack> optional = removeOne(bundle);
                    if(optional.isPresent()) {
                        playRemoveOneSound(player);
                        removeOne(bundle).ifPresent((p_150740_) -> {
                            add(bundle, slot.safeInsert(p_150740_), maxWeight);
                        });
                        ret = true;
                    }
                } else if (itemIn.getItem().canFitInsideContainerItems()) {
                    int i = (maxWeight - getContentWeight(bundle)) / getWeight(itemIn);
                    int j = add(bundle, slot.safeTake(itemIn.getCount(), i, player), maxWeight);
                    if (j > 0) {
                        playInsertSound(player);
                    }
                    ret = true;
                }
                return ret;
            }
        }
    }

    public static boolean stackOnMe(ItemStack bundle, ItemStack itemIn, Slot slot, ClickAction action, Player player,
                                    SlotAccess slotAccess, int maxWeight) {
        boolean ret = false;
        if (action == ClickAction.SECONDARY && slot.allowModification(player)) {
            boolean isSpace = getContents(bundle).anyMatch(stack -> stack.getItem() == itemIn.getItem())
                    || 2 - getContents(bundle).mapToInt(stack -> {
                return 1;
            }).sum() > 0;
            if (itemIn.isEmpty()) {
                Optional<ItemStack> optional = removeOne(bundle);
                optional.ifPresent((p_186347_) -> {
                    playRemoveOneSound(player);
                    slotAccess.set(p_186347_);
                });
                ret = optional.isPresent();
            } else if(isSpace) {
                int i = add(bundle, itemIn, maxWeight);
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
        ItemUtils.onContainerDestroyed(pItemEntity, getContents(pItemEntity.getItem()));
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
        getContents(stack).forEach(nonnulllist::add);
        int maxWeight = 64;
        if(stack.getItem() instanceof BundleItem bundle) {
            maxWeight = bundle.MAX_WEIGHT;
        }
        return Optional.of(new CustomBundleTooltip(nonnulllist, getContentWeight(stack), maxWeight, size));
    }

    public static void addHoverText(ItemStack pStack, List<Component> pTooltipComponents, int size) {
        pTooltipComponents.add(
                Component.translatable("item.minecraft.bundle.fullness", InventoryHelper.getContentWeight(pStack), size)
                        .withStyle(ChatFormatting.GRAY));
    }

    public static boolean isFullnessBarVisible(ItemStack pStack) {
        return getContentWeight(pStack) > 0;
    }

    public static int getFullnessBarWidth(ItemStack pStack, int maxWeight) {
        return Math.min(1 + 12 * getContentWeight(pStack) / maxWeight, 13);
    }

    public static int getFullnessBarColor(ItemStack pStack) {
        return BAR_COLOR;
    }

}
