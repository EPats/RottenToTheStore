package io.github.epats.rottentothestore.client;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.github.epats.rottentothestore.RottenToTheStore;
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
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

public class WearableStorageLayer<EntityT extends LivingEntity, ModelT extends HumanoidModel<EntityT>>
        extends RenderLayer<EntityT, ModelT> {

    public static final ResourceLocation WEARABLE_STORAGE_LAYER =
            new ResourceLocation(RottenToTheStore.MOD_ID, "wearable_storage");

    public static final ModelLayerLocation WEARABLE_STORAGE_MODEL_LAYER =
            new ModelLayerLocation(WEARABLE_STORAGE_LAYER, "wearable_storage");

    public static final  ResourceLocation BACKPACK_TEXTURE = new ResourceLocation(RottenToTheStore.MOD_ID,
            "textures/models/armor/backpack.png");

    private WearableStorageModel<EntityT> model;

    public WearableStorageLayer(RenderLayerParent<EntityT, ModelT> pRenderer) {
        super(pRenderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, EntityT livingEntity,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        this.model = new WearableStorageModel<EntityT>(WearableStorageModel.createBodyLayer(true).bakeRoot());
        this.model.setupAnim(livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.renderWearables(poseStack, bufferSource, packedLight, livingEntity);
    }

    private void renderWearables(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, LivingEntity livingEntity) {

        renderWearablesChest(poseStack, bufferSource, packedLight, livingEntity, livingEntity.getItemBySlot(EquipmentSlot.CHEST));
        renderWearablesLegs(poseStack, bufferSource, packedLight, livingEntity, livingEntity.getItemBySlot(EquipmentSlot.LEGS));

    }

    private void renderWearablesChest(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
                                      LivingEntity livingEntity, ItemStack chestItem) {
        if (chestItem.getItem() instanceof ArmorItem armorItem) {
            if (armorItem.getEquipmentSlot() == EquipmentSlot.CHEST) {

                this.getParentModel().copyPropertiesTo(model);
                this.model.copyPropertiesThrough(livingEntity.isCrouching());

                poseStack.translate(0D, 0.2D, 0.22D);
//                poseStack.mulPose(Axis.ZN.rotationDegrees(40));
                boolean flag1 = chestItem.hasFoil();
                if (armorItem instanceof net.minecraft.world.item.DyeableLeatherItem) {
                    int i = ((net.minecraft.world.item.DyeableLeatherItem) armorItem).getColor(chestItem);
                    float f = (float) (i >> 16 & 255) / 255.0F;
                    float f1 = (float) (i >> 8 & 255) / 255.0F;
                    float f2 = (float) (i & 255) / 255.0F;
                    this.renderModel(poseStack, bufferSource, packedLight, flag1, f, f1, f2, livingEntity, true);
                    this.renderModel(poseStack, bufferSource, packedLight, flag1, 1.0F, 1.0F, 1.0F, livingEntity, false);
                } else {
                    this.renderModel(poseStack, bufferSource, packedLight, flag1, 1.0F, 1.0F, 1.0F, livingEntity, false);
                }
            }
        }
    }

    private void renderWearablesLegs(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
                                      LivingEntity livingEntity, ItemStack legsItem) {
        if (legsItem.getItem() instanceof ArmorItem armorItem) {
            if (armorItem.getEquipmentSlot() == EquipmentSlot.LEGS) {

                this.getParentModel().copyPropertiesTo(model);
                this.model.copyPropertiesThrough(livingEntity.isCrouching());

                poseStack.translate(0D, 0.2D, 0.22D);
                poseStack.mulPose(Axis.ZN.rotationDegrees(40));
                boolean flag1 = legsItem.hasFoil();
                if (armorItem instanceof net.minecraft.world.item.DyeableLeatherItem) {
                    int i = ((net.minecraft.world.item.DyeableLeatherItem) armorItem).getColor(legsItem);
                    float f = (float) (i >> 16 & 255) / 255.0F;
                    float f1 = (float) (i >> 8 & 255) / 255.0F;
                    float f2 = (float) (i & 255) / 255.0F;
                    this.renderModel(poseStack, bufferSource, packedLight, flag1, f, f1, f2, livingEntity, true);
                    this.renderModel(poseStack, bufferSource, packedLight, flag1, 1.0F, 1.0F, 1.0F, livingEntity, false);
                } else {
                    this.renderModel(poseStack, bufferSource, packedLight, flag1, 1.0F, 1.0F, 1.0F, livingEntity, false);
                }
            }
        }
    }



    private void renderModel(PoseStack pPoseStack, MultiBufferSource pBuffer, int packedLight, boolean foil, float r,
                             float g, float b, LivingEntity livingEntity, Boolean customColour) {
        VertexConsumer vertexConsumer = ItemRenderer.getArmorFoilBuffer(pBuffer,
                RenderType.armorCutoutNoCull(BACKPACK_TEXTURE), false, foil);
        BackpackParts[] parts = {
//                BackpackParts.BUNDLE_BACK, BackpackParts.BUNDLE_BACK_STRAP,
                BackpackParts.BUNDLE_SIDE, BackpackParts.BUNDLE_SIDE_STRAP
//                BackpackParts.BACKPACK_MAIN
        };
        this.renderModel(pPoseStack, vertexConsumer, pBuffer, packedLight, r, g, b, customColour, parts);
    }

    private void renderModel(PoseStack pPoseStack, VertexConsumer vertexBuilder, MultiBufferSource pBuffer,
                       int packedLight, float r, float g, float b, Boolean customColour, BackpackParts[] parts) {
        ImmutableList.Builder<ModelPart> modelPartsBuilder = ImmutableList.builder();
        for(BackpackParts part : parts) {
            modelPartsBuilder.add(this.model.MODEL_PART_MAP.get(part));
        }
        ImmutableList<ModelPart> modelParts = modelPartsBuilder.build();
        modelParts.forEach((part) -> {
            part.render(pPoseStack, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY, r, g, b, 1.0F);
        });
    }

    public static LayerDefinition createBodyLayer() {
        return WearableStorageModel.createBodyLayer(true);
    }

    public enum BackpackParts {
        BUNDLE_BACK,
        BUNDLE_BACK_STRAP,
        BUNDLE_SIDE,
        BUNDLE_SIDE_STRAP,
        BACKPACK_MAIN,
        BACKPACK_TOP,
        BACKPACK_FRONT,
        BACKPACK_STRAPS
    }
}
