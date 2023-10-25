package io.github.epats.rottentothestore.common.item;

import io.github.epats.rottentothestore.client.WearableStorageLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.function.Function;

public class WearableTest extends Item {

    private final EquipmentSlot[] EQUIPMENT_SLOTS;
    private final Function<EquipmentSlot, WearableStorageLayer.BagParts[]> BAG_PARTS_FN;

    public WearableTest(Item.Properties properties, EquipmentSlot[] equipmentSlots, Function<EquipmentSlot, WearableStorageLayer.BagParts[]> bagPartsFunction) {
        super(properties);
        this.EQUIPMENT_SLOTS = equipmentSlots;
        this.BAG_PARTS_FN = bagPartsFunction;
    }

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot armorType, Entity entity) {
        return Arrays.stream(this.EQUIPMENT_SLOTS).anyMatch(equipmentSlot -> equipmentSlot == armorType);
    }

    public WearableStorageLayer.BagParts[] getBagPartsFromSlot(EquipmentSlot equipmentSlot) {
        return this.BAG_PARTS_FN.apply(equipmentSlot);
    }
}
