package io.github.epats.rottentothestore.client;

import io.github.epats.rottentothestore.client.WearableStorageLayer.BackpackParts;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

public class WearableStorageModel<EntityT extends LivingEntity> extends HumanoidModel<EntityT> {
    private static final String BACKPACK_MAIN = "backpack_main";
    private static final String BACKPACK_FRONT = "backpack_front";
    private static final String BACKPACK_TOP = "backpack_top";
    private static final String BACKPACK_STRAP_1 = "backpack_strap_1";
    private static final String BACKPACK_STRAP_2 = "backpack_strap_2";
    private static final String BACKPACK_STRAP_1_TOP = "backpack_strap_1_top";
    private static final String BACKPACK_STRAP_2_TOP = "backpack_strap_2_top";
    private static final String BACKPACK_BUTTON_MAIN_1 = "backpack_button_main_1";
    private static final String BACKPACK_BUTTON_MAIN_2 = "backpack_button_main_2";
    private static final String BACKPACK_BUTTON_FRONT = "backpack_button_front";
    private static final String BACKPACK_BUTTON_TOP = "backpack_button_top";

    private static final String BUNDLE_BACK = "bundle_back";
    private static final String BUNDLE_BACK_STRAP = "bundle_back_strap";
    private static final String BUNDLE_SIDE = "bundle_side";
    private static final String BUNDLE_SIDE_STRAP = "bundle_side_strap";

    public final ModelPart head;
    public final ModelPart hat;
    public final ModelPart body;
    public final ModelPart rightArm;
    public final ModelPart leftArm;
    public final ModelPart rightLeg;
    public final ModelPart leftLeg;

    public final Map<BackpackParts, ModelPart> MODEL_PART_MAP;

    public WearableStorageModel(ModelPart part) {
        super(part);
        this.head = part.getChild("head");
        this.hat = part.getChild("hat");
        this.body = part.getChild("body");
        this.rightArm = part.getChild("right_arm");
        this.leftArm = part.getChild("left_arm");
        this.rightLeg = part.getChild("right_leg");
        this.leftLeg = part.getChild("left_leg");
        this.MODEL_PART_MAP = this.setModelParts(part);
    }

    public Map<BackpackParts, ModelPart> setModelParts(ModelPart part) {

        ImmutableMap.Builder<BackpackParts, ModelPart> builder = ImmutableMap.builder();
        builder.put(BackpackParts.BUNDLE_BACK, part.getChild("body").getChild(BUNDLE_BACK));
        builder.put(BackpackParts.BUNDLE_BACK_STRAP, part.getChild("body").getChild(BUNDLE_BACK_STRAP));
        builder.put(BackpackParts.BACKPACK_MAIN, part.getChild("body").getChild(BACKPACK_MAIN));
        builder.put(BackpackParts.BUNDLE_SIDE, part.getChild("left_leg").getChild(BUNDLE_SIDE));
        builder.put(BackpackParts.BUNDLE_SIDE_STRAP, part.getChild("left_leg").getChild(BUNDLE_SIDE_STRAP));
        return builder.build();
    }

    public static LayerDefinition createBodyLayer(boolean isWorn) {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition parentDefinition = meshdefinition.getRoot();

        parentDefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F + 64, 0.0F));
        parentDefinition.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE.extend(0.5F)), PartPose.offset(0.0F, 0.0F + 64, 0.0F));
        parentDefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));
        parentDefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(-5.0F, 2.0F + 64, 0.0F));
        parentDefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(5.0F, 2.0F + 64, 0.0F));
        parentDefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(-1.9F, 12.0F + 64, 0.0F));
        parentDefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(1.9F, 12.0F + 64, 0.0F));

        PartDefinition leftLeg = parentDefinition.getChild("left_leg");
        leftLeg.addOrReplaceChild(BUNDLE_SIDE, CubeListBuilder.create().texOffs(0, 0).addBox(-5F, 1.0F, -1.5F, 5.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-1.5F, -2.75F, -0.5F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        leftLeg.addOrReplaceChild(BUNDLE_SIDE_STRAP, CubeListBuilder.create().texOffs(0, 0).addBox(-5F, 1.0F, -5F, 12.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-0.5F, -5.0F, -5.6F, 12.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-0.5F, -5.0F, -5.5F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));


        PartDefinition body = parentDefinition.getChild("body");

        body.addOrReplaceChild(BUNDLE_BACK, CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -0.75F, -1.5F, 5.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-1.5F, -2.75F, -0.5F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 64.0F, 0.0F));

        body.addOrReplaceChild(BUNDLE_BACK_STRAP, CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -5.0F, -1.5F, 1.0F, 12.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-0.5F, -5.0F, -5.6F, 1.0F, 12.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-0.5F, -5.0F, -5.5F, 1.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 64.0F, 0.0F));


        body.addOrReplaceChild(BACKPACK_MAIN,
                CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, 13.0F, 2.0F, 12.0F, 11.0F, 6.0F, true),
                PartPose.offset(0.0F, 0.0F, 0.0F));
        body.addOrReplaceChild(BACKPACK_FRONT,
                CubeListBuilder.create().texOffs(0, 17).addBox(-5.0F, 13.0F, 8.0F, 10.0F, 7.0F, 3.0F, true),
                PartPose.offset(0.0F, 0.0F, 0.0F));
        body.addOrReplaceChild(BACKPACK_TOP,
                CubeListBuilder.create().texOffs(26, 17).addBox(-5.0F, 24.0F, 2.0F, 10.0F, 3.0F, 5.0F, true),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        float strapOffset = isWorn ? -3.0F : 1.0F;
        body.addOrReplaceChild(BACKPACK_STRAP_1,
                CubeListBuilder.create().texOffs(36, 0).addBox(3.0F, 15.0F, strapOffset, 2.0F, 9.0F, 1.0F, true),
                PartPose.offset(0.0F, 0.0F, 0.0F));
        body.addOrReplaceChild(BACKPACK_STRAP_2,
                CubeListBuilder.create().texOffs(42, 0).addBox(-5.0F, 15.0F, strapOffset, 2.0F, 9.0F, 1.0F, true),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        float strapTopX = isWorn ? 2.0F : 0.0F;
        float strapTopY = isWorn ? 1.0F : 0.0F;
        float strapTopZ = isWorn ? 5.0F : 0.0F;
        body.addOrReplaceChild(BACKPACK_STRAP_1_TOP, CubeListBuilder.create().texOffs(48, 0).addBox(3.0F, 24.0F, -3.0F,
                strapTopX, strapTopY, strapTopZ, true), PartPose.offset(0.0F, 0.0F, 0.0F));
        body.addOrReplaceChild(BACKPACK_STRAP_2_TOP, CubeListBuilder.create().texOffs(48, 6).addBox(-5.0F, 24.0F, -3.0F,
                strapTopX, strapTopY, strapTopZ, true), PartPose.offset(0.0F, 0.0F, 0.0F));

        body.addOrReplaceChild(BACKPACK_BUTTON_MAIN_1,
                CubeListBuilder.create().texOffs(30, 0).addBox(-4.0F, 20.5F, 7.5F, 1.0F, 2.0F, 1.0F, true),
                PartPose.offset(0.0F, 0.0F, 0.0F));
        body.addOrReplaceChild(BACKPACK_BUTTON_MAIN_2,
                CubeListBuilder.create().texOffs(30, 3).addBox(3.0F, 20.5F, 7.5F, 1.0F, 2.0F, 1.0F, true),
                PartPose.offset(0.0F, 0.0F, 0.0F));
        body.addOrReplaceChild(BACKPACK_BUTTON_FRONT,
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 16.0F, 10.5F, 2.0F, 2.0F, 1.0F, true),
                PartPose.offset(0.0F, 0.0F, 0.0F));
        body.addOrReplaceChild(BACKPACK_BUTTON_TOP,
                CubeListBuilder.create().texOffs(0, 3).addBox(-1.0F, 25.5F, 6.5F, 2.0F, 1.0F, 1.0F, true),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }


    public static void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int color,
                              ImmutableList<ModelPart> backpack, ImmutableList<ModelPart> buttons, BackpackParts parts) {
        VertexConsumer vertexBuilder = buffer
                .getBuffer(RenderType.entityCutoutNoCull(WearableStorageLayer.BACKPACK_TEXTURE));

        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        backpack.forEach((part) -> {
            part.render(poseStack, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY, r, g, b, 1.0F);
        });
        buttons.forEach((part) -> {
            part.render(poseStack, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        });
    }

    public void copyPropertiesThrough() {
        this.body.getChild(BACKPACK_MAIN).copyFrom(this.body);
        this.body.getChild(BUNDLE_BACK).copyFrom(this.body);
        this.body.getChild(BUNDLE_BACK_STRAP).copyFrom(this.body);
        this.leftLeg.getChild(BUNDLE_SIDE).copyFrom(this.leftLeg);
        this.leftLeg.getChild(BUNDLE_SIDE_STRAP).copyFrom(this.leftLeg);
    }
}
