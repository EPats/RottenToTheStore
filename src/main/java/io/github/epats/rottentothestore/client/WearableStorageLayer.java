package io.github.epats.rottentothestore.client;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.epats.rottentothestore.RottenToTheStore;
import io.github.epats.rottentothestore.common.item.ItemBagWearable;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class WearableStorageLayer<EntityT extends LivingEntity, ModelT extends HumanoidModel<EntityT>>
        extends RenderLayer<EntityT, ModelT> {

    public static final ResourceLocation WEARABLE_STORAGE_LAYER =
            new ResourceLocation(RottenToTheStore.MOD_ID, "wearable_storage");

    public static final ModelLayerLocation WEARABLE_STORAGE_MODEL_LAYER =
            new ModelLayerLocation(WEARABLE_STORAGE_LAYER, "wearable_storage");

    public static final ResourceLocation BACKPACK_TEXTURE = new ResourceLocation(RottenToTheStore.MOD_ID,
            "textures/model/armor/backpack.png");

    public static final ResourceLocation BACKPACK_TEXTURE_BUTTONS = new ResourceLocation(RottenToTheStore.MOD_ID,
            "textures/model/armor/buttons.png");

    private final WearableStorageModel<EntityT> model = new WearableStorageModel<>(WearableStorageModel.createBodyLayer(true).bakeRoot());

    public WearableStorageLayer(RenderLayerParent<EntityT, ModelT> pRenderer) {
        super(pRenderer);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight, @NotNull EntityT livingEntity,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        this.getParentModel().copyPropertiesTo(model);
        this.model.setupAnim(livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.model.copyPropertiesThrough(livingEntity.isCrouching());

        this.renderStoragePiece(poseStack, bufferSource, livingEntity, packedLight, EquipmentSlot.HEAD);
        this.renderStoragePiece(poseStack, bufferSource, livingEntity, packedLight, EquipmentSlot.CHEST);
        this.renderStoragePiece(poseStack, bufferSource, livingEntity, packedLight, EquipmentSlot.LEGS);
        this.renderStoragePiece(poseStack, bufferSource, livingEntity, packedLight, EquipmentSlot.FEET);
    }

    private void renderStoragePiece(PoseStack poseStack, MultiBufferSource bufferSource, LivingEntity livingEntity,
                                    int packedLight, EquipmentSlot slot) {
        ItemStack itemStack = livingEntity.getItemBySlot(slot);
        if(!(itemStack.getItem() instanceof ItemBagWearable equippedBag))
            return;

        boolean hasFoil = itemStack.hasFoil();
        if(equippedBag.canDye()) {
            this.renderDyeableModelParts(poseStack, bufferSource, packedLight, itemStack, equippedBag, slot, hasFoil);
        }
        this.renderSolidColourModelParts(poseStack, bufferSource, packedLight, equippedBag, slot, hasFoil);
    }

    private void renderSolidColourModelParts(PoseStack pPoseStack, MultiBufferSource pBuffer, int packedLight,
                                             ItemBagWearable equippedBag, EquipmentSlot equipmentSlot, boolean foil) {
        ResourceLocation backpackTexture = equippedBag.getSolidColourTexture();
        BagParts[] parts = equippedBag.getSolidColourBagPartsForRender(equipmentSlot);
        this.renderModel(pPoseStack, pBuffer, packedLight, 1.0F, 1.0F, 1.0F, parts, foil, backpackTexture);
    }

    private void renderDyeableModelParts(PoseStack pPoseStack, MultiBufferSource pBuffer, int packedLight, ItemStack itemStack,
                                         ItemBagWearable equippedBag, EquipmentSlot equipmentSlot, boolean foil) {
        ResourceLocation backpackTexture = equippedBag.getDyeableTexture();
        BagParts[] parts = equippedBag.getDyeableBagPartsForRender(equipmentSlot);
        this.renderModel(pPoseStack, pBuffer, packedLight, 1.0F, 1.0F, 1.0F, parts, foil, backpackTexture);
        int color = equippedBag.getColor(itemStack);
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        this.renderModel(pPoseStack, pBuffer, packedLight, r, g, b, parts, foil, backpackTexture);
    }

    private void renderModel(PoseStack pPoseStack, MultiBufferSource pBuffer, int packedLight,
                             float r, float g, float b, BagParts[] parts, boolean foil, ResourceLocation texture) {

        VertexConsumer vertexConsumer = ItemRenderer.getArmorFoilBuffer(pBuffer, RenderType.armorCutoutNoCull(texture), false, foil);

        ImmutableList<ModelPart> modelParts = Arrays.stream(parts)
                .map(this.model.MODEL_PART_MAP::get)
                .collect(ImmutableList.toImmutableList());

        modelParts.forEach((part) -> part.render(pPoseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, r, g, b, 1.0F));
    }

    public static LayerDefinition createBodyLayer() {
        return WearableStorageModel.createBodyLayer(true);
    }

    public enum BagParts {
        BUNDLE_BACK,
        BUNDLE_SIDE,
        BUNDLE_SIDE_STRAP,
        BACKPACK_MAIN,
        BACKPACK_MAIN_BUTTONS,
        BACKPACK_TOP,
        BACKPACK_TOP_BUTTONS,
        BACKPACK_FRONT,
        BACKPACK_FRONT_BUTTONS
    }
}
