package io.github.epats.rottentothestore.client;

import io.github.epats.rottentothestore.client.WearableStorageLayer.BagParts;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.LivingEntity;

import java.util.Map;

public class WearableStorageModel<EntityT extends LivingEntity> extends HumanoidModel<EntityT> {
    private static final String BUNDLE_BACK = "bundle_back";
    private static final String BUNDLE_SIDE = "bundle_side";
    private static final String BUNDLE_SIDE_STRAP = "bundle_side_strap";
    private static final String BACKPACK_MAIN = "backpack_main";
    private static final String BACKPACK_FRONT = "backpack_front";
    private static final String BACKPACK_TOP = "backpack_top";
    private static final String BACKPACK_MAIN_BUTTONS = "backpack_main_buttons";
    private static final String BACKPACK_FRONT_BUTTONS = "backpack_front_button";
    private static final String BACKPACK_TOP_BUTTONS = "backpack_top_button";


    private final ModelPart head;
    private final ModelPart hat;
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public final Map<BagParts, ModelPart> MODEL_PART_MAP;

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

    public Map<BagParts, ModelPart> setModelParts(ModelPart part) {

        ImmutableMap.Builder<BagParts, ModelPart> builder = ImmutableMap.builder();
        builder.put(BagParts.BUNDLE_BACK, part.getChild("body").getChild(BUNDLE_BACK));
        builder.put(BagParts.BUNDLE_SIDE, part.getChild("left_leg").getChild(BUNDLE_SIDE));
        builder.put(BagParts.BUNDLE_SIDE_STRAP, part.getChild("body").getChild(BUNDLE_SIDE_STRAP));
        builder.put(BagParts.BACKPACK_MAIN, part.getChild("body").getChild(BACKPACK_MAIN));
        builder.put(BagParts.BACKPACK_MAIN_BUTTONS, part.getChild("body").getChild(BACKPACK_MAIN_BUTTONS));
        builder.put(BagParts.BACKPACK_TOP, part.getChild("body").getChild(BACKPACK_TOP));
        builder.put(BagParts.BACKPACK_TOP_BUTTONS, part.getChild("body").getChild(BACKPACK_TOP_BUTTONS));
        builder.put(BagParts.BACKPACK_FRONT, part.getChild("body").getChild(BACKPACK_FRONT));
        builder.put(BagParts.BACKPACK_FRONT_BUTTONS, part.getChild("body").getChild(BACKPACK_FRONT_BUTTONS));
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

        renderSideBundle(parentDefinition);
        renderBackBundle(parentDefinition);
        renderBackpack(parentDefinition.getChild("body"));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    private static void renderSideBundle(PartDefinition parentDefinition) {

        PartDefinition leftLeg = parentDefinition.getChild("left_leg");

        leftLeg.addOrReplaceChild(BUNDLE_SIDE, CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(4F, 11F, -1F, 4F, 4F, 4F, CubeDeformation.NONE)
                        .texOffs(16, 4)
                        .addBox(4F, 9F, 0F, 2F, 2F, 3F, CubeDeformation.NONE),
                PartPose.offset(1.9F, 0.0F, 0.0F));


        PartDefinition body = parentDefinition.getChild("body");

        body.addOrReplaceChild(BUNDLE_SIDE_STRAP, CubeListBuilder.create()
                        .texOffs(0, 8)
                        .addBox(-4.5F, 8.5F, -2.5F, 9F, 1F, 5F, CubeDeformation.NONE),
                PartPose.offset(-1.0F, 0.0F, 0.0F));

    }

    private static void renderBackBundle(PartDefinition parentDefinition) {

        PartDefinition body = parentDefinition.getChild("body");

        body.addOrReplaceChild(BUNDLE_BACK, CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-5F, 2F, 1.75F, 5.0F, 5.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(0, 0)
                .addBox(-4F, 0F, 2.75F, 3.0F, 2.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(0, 8)
                .addBox(-3F, -2.5F, -2.5F, 1F, 12F, 5F, CubeDeformation.NONE),
                PartPose.offset(0.0F, 64.0F, 0.0F));
    }

    private static void renderBackpack(PartDefinition body) {

        body.addOrReplaceChild(BACKPACK_MAIN,
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-5.0F, 2.0F, 2.0F, 10.0F, 10.0F, 4.0F, true)
                        .texOffs(36, 0)
                        .addBox(3.0F, 0.0F, -3F, 2.0F, 9.0F, 1.0F, true)
                        .texOffs(42, 0)
                        .addBox(-5.0F, 0.0F, -3F, 2.0F, 9.0F, 1.0F, true)
                        .texOffs(48, 0)
                        .addBox(3.0F, -1.0F, -3.0F, 2F, 1F, 5F, true)
                        .texOffs(48, 6)
                        .addBox(-5.0F, -1.0F, -3.0F, 2F, 1F, 5F, true)
                        .texOffs(36, 0)
                        .addBox(3.0F, -1.0F, 2F, 2.0F, 5.0F, 1.0F, true)
                        .texOffs(42, 0)
                        .addBox(-5.0F, -1.0F, 2F, 2.0F, 5.0F, 1.0F, true),
                PartPose.offset(0.0F, 64.0F, 0.0F));

        body.addOrReplaceChild(BACKPACK_MAIN_BUTTONS,
                CubeListBuilder.create()
                        .texOffs(30, 0)
                        .addBox(-3.5F, 3.5F, 5.5F, 1.0F, 2.0F, 1.0F, true)
                        .texOffs(30, 3)
                        .addBox(2.5F, 3.5F, 5.5F, 1.0F, 2.0F, 1.0F, true),
                PartPose.offset(0.0F, 64.0F, 0.0F));


        body.addOrReplaceChild(BACKPACK_FRONT,
                CubeListBuilder.create()
                        .texOffs(0, 17)
                        .addBox(-4F, 6.5F, 6.0F, 8.0F, 5.0F, 2.0F, true),
                PartPose.offset(0.0F, 64.0F, 0.0F));

        body.addOrReplaceChild(BACKPACK_FRONT_BUTTONS,
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-1.0F, 7.5F, 7.5F, 2.0F, 2.0F, 1.0F, true),
                PartPose.offset(0.0F, 0.0F, 0.0F));


        body.addOrReplaceChild(BACKPACK_TOP,
                CubeListBuilder.create()
                        .texOffs(26, 17)
                        .addBox(-3.5F, -1.0F, 2.0F, 7.0F, 3.0F, 3.0F, true),
                PartPose.offset(0.0F, 64.0F, 0.0F));

        body.addOrReplaceChild(BACKPACK_TOP_BUTTONS,
                CubeListBuilder.create()
                        .texOffs(0, 3)
                        .addBox(-1.0F, 0.5F, 4.5F, 2.0F, 1.0F, 1.0F, true),
                PartPose.offset(0.0F, 0.0F, 0.0F));

    }

    public void copyPropertiesThrough(boolean crouching) {
        this.body.getChild(BACKPACK_MAIN).copyFrom(this.body);
        this.body.getChild(BACKPACK_MAIN_BUTTONS).copyFrom(this.body);
        this.body.getChild(BACKPACK_TOP).copyFrom(this.body);
        this.body.getChild(BACKPACK_TOP_BUTTONS).copyFrom(this.body);
        this.body.getChild(BACKPACK_FRONT).copyFrom(this.body);
        this.body.getChild(BACKPACK_FRONT_BUTTONS).copyFrom(this.body);

        this.body.getChild(BUNDLE_BACK).copyFrom(this.body);

        this.leftLeg.getChild(BUNDLE_SIDE).copyFrom(this.body);
        this.body.getChild(BUNDLE_SIDE_STRAP).copyFrom(this.body);

        this.body.getChild(BUNDLE_BACK).zRot -= 0.7F;

        if(crouching) {
            this.body.getChild(BUNDLE_BACK).yRot += 0.4F;
            this.body.getChild(BUNDLE_BACK).xRot -= 0.1F;

            this.body.getChild(BUNDLE_SIDE_STRAP).z -= 0.4F;
            this.leftLeg.getChild(BUNDLE_SIDE).xRot -= 0.3F;
        }

        this.leftLeg.getChild(BUNDLE_SIDE).xRot += this.leftLeg.xRot * 0.15F;
    }

}
