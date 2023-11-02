package io.github.epats.rottentothestore.common.item;

import io.github.epats.rottentothestore.client.WearableStorageLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.function.Function;

public class ItemBagWearable extends ItemBag implements DyeableLeatherItem {

    private final EquipmentSlot[] EQUIPMENT_SLOTS;
    private final Function<EquipmentSlot, WearableStorageLayer.BagParts[]> DYEABLE_BAG_PARTS_FN;
    private final Function<EquipmentSlot, WearableStorageLayer.BagParts[]> SOLID_COLOUR_BAG_PARTS_FN;
    private final ResourceLocation dyeableColourTexture;
    private final ResourceLocation solidColourTexture;
    private final boolean canDye;
    public static final WearableStorageLayer.BagParts[] EMPTY_BAG_PARTS_ARRAY = new WearableStorageLayer.BagParts[0];
    public static final Function<EquipmentSlot, WearableStorageLayer.BagParts[]> EMPTY_BAG_ARRAY_FN = (equipmentSlot -> EMPTY_BAG_PARTS_ARRAY);

    public ItemBagWearable(Item.Properties properties, EquipmentSlot[] equipmentSlots,
                           Function<EquipmentSlot, WearableStorageLayer.BagParts[]> solidColourBagPartsFn,
                           ResourceLocation solidColourTexture, ItemBag bagBase) {
        this(properties, equipmentSlots, EMPTY_BAG_ARRAY_FN, solidColourBagPartsFn, solidColourTexture,
                solidColourTexture, false, bagBase);
    }

    public ItemBagWearable(Item.Properties properties, EquipmentSlot[] equipmentSlots,
                           Function<EquipmentSlot, WearableStorageLayer.BagParts[]> dyeableBagPartsFn,
                           Function<EquipmentSlot, WearableStorageLayer.BagParts[]> solidColourBagPartsFn,
                           ResourceLocation dyeableColourTexture, ResourceLocation solidColourTexture, boolean canDye,
                           ItemBag bagBase) {
        super(properties, bagBase.getNUMBER_OF_SLOTS(), bagBase.getMAX_WEIGHT(), bagBase.CAN_ACCEPT_ITEM_FN);
        this.EQUIPMENT_SLOTS = equipmentSlots;
        this.DYEABLE_BAG_PARTS_FN = dyeableBagPartsFn;
        this.SOLID_COLOUR_BAG_PARTS_FN = solidColourBagPartsFn;
        this.dyeableColourTexture = dyeableColourTexture;
        this.solidColourTexture = solidColourTexture;
        this.canDye = canDye;
    }


    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot armorType, Entity entity) {
        return Arrays.stream(this.EQUIPMENT_SLOTS).anyMatch(equipmentSlot -> equipmentSlot == armorType);
    }

    public WearableStorageLayer.BagParts[] getDyeableBagPartsForRender(EquipmentSlot equipmentSlot) {
        return this.DYEABLE_BAG_PARTS_FN.apply(equipmentSlot);
    }

    public WearableStorageLayer.BagParts[] getSolidColourBagPartsForRender(EquipmentSlot equipmentSlot) {
        return this.SOLID_COLOUR_BAG_PARTS_FN.apply(equipmentSlot);
    }

    public ResourceLocation getDyeableTexture() {
        return this.dyeableColourTexture;
    }

    public ResourceLocation getSolidColourTexture() {
        return this.solidColourTexture;
    }

    public boolean canDye() {
        return this.canDye;
    }

}
