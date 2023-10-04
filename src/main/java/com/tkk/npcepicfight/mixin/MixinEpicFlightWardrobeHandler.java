package com.tkk.npcepicfight.mixin;


import com.mojang.blaze3d.matrix.MatrixStack;
import extensions.com.mojang.blaze3d.vertex.PoseStack.ABI;
import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.armature.JointTransformModifier;
import moe.plushie.armourers_workshop.core.armature.thirdparty.EpicFlightContext;
import moe.plushie.armourers_workshop.core.armature.thirdparty.EpicFlightTransformProvider;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.init.client.EpicFlightWardrobeHandler;
import moe.plushie.armourers_workshop.utils.ModelHolder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.entity.LivingEntity;
import noppes.npcs.client.renderer.RenderCustomNpc;
import noppes.npcs.entity.EntityCustomNpc;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import yesman.epicfight.main.EpicFightMod;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;

@Pseudo
@Mixin(value = EpicFlightWardrobeHandler.class,priority=800,remap = false)
public abstract class MixinEpicFlightWardrobeHandler {
    private static Field NPC_RENDERENTITY;
    @Shadow
    private static EpicFlightContext context;
    static {
        try {
            NPC_RENDERENTITY= RenderCustomNpc.class.getDeclaredField("renderEntity");
            NPC_RENDERENTITY.setAccessible(true);
        }catch (Exception e){

        }
    }
    /**
     * @author
     * @reason 我是懒逼，应该用替换参数的
     */
    @Overwrite
    public static void onRenderLivingPre(LivingEntity entity, float partialTicks, int packedLight, MatrixStack poseStack, IRenderTypeBuffer buffers, LivingRenderer<?, ?> entityRenderer, boolean isFirstPersonRenderer, EpicFlightTransformProvider transformProvider){
        if(entity instanceof EntityCustomNpc){
            EntityCustomNpc npc=(EntityCustomNpc) entity;
            if(npc.modelData != null && npc.modelData.getEntity(npc) != null) {
                entity = npc.modelData.getEntity(npc);
            } else {
                entity = entity;
            }
        }
        if(entityRenderer instanceof RenderCustomNpc){
            try {
                entityRenderer=(LivingRenderer) NPC_RENDERENTITY.get(entityRenderer);
            }catch (Exception e){
                EpicFightMod.LOGGER.log(Level.ERROR,"[2kk2] mixin error : noppes.npcs.client.renderer.RenderCustomNpc.renderLayer error "+e);
            }
        }
        IModel model = ModelHolder.ofNullable(entityRenderer.getModel());
        SkinRenderData renderData = SkinRenderData.of(entity);
        if (renderData != null) {
            Collection<ISkinPartType> overrideParts = null;
            if (isFirstPersonRenderer) {
                overrideParts = Collections.singleton(SkinPartTypes.BIPPED_HEAD);
            }

            model.setAssociatedObject(transformProvider, EpicFlightTransformProvider.KEY);
            context.overrideParts = overrideParts;
            context.overridePostStack = ABI.copy(poseStack);
            context.overrideTransformModifier = (JointTransformModifier)model.getAssociatedObject(JointTransformModifier.EPICFIGHT);
            context.isLimitLimbs = false;
            renderData.epicFlightContext = context;
            SkinRendererManager.getInstance().willRender(entity, entityRenderer.getModel(), entityRenderer, renderData, () -> {
                return SkinRenderContext.alloc(renderData, packedLight, partialTicks, poseStack, buffers);
            });
        }

    }
    /**
     * @author
     * @reason 我是懒逼，应该用替换参数的
     */
    @Overwrite
    public static void onRenderLivingPost(LivingEntity entity, float partialTicks, int packedLight, MatrixStack poseStack, IRenderTypeBuffer buffers, LivingRenderer<?, ?> entityRenderer){

        if(entity instanceof EntityCustomNpc){
            EntityCustomNpc npc=(EntityCustomNpc) entity;
            if(npc.modelData != null && npc.modelData.getEntity(npc) != null) {
                entity = npc.modelData.getEntity(npc);
            } else {
                entity = entity;
            }
        }
        if(entityRenderer instanceof RenderCustomNpc){
            try {
                entityRenderer=(LivingRenderer) NPC_RENDERENTITY.get(entityRenderer);
            }catch (Exception e){
                EpicFightMod.LOGGER.log(Level.ERROR,"[2kk2] mixin error : noppes.npcs.client.renderer.RenderCustomNpc.renderLayer error "+e);
            }

        }
        IModel model = ModelHolder.ofNullable(entityRenderer.getModel());
        SkinRenderData renderData = SkinRenderData.of(entity);
        if (renderData != null) {
            model.setAssociatedObject(null, EpicFlightTransformProvider.KEY);
            context.overrideParts = null;
            context.overridePostStack = null;
            context.overrideTransformModifier = null;
            context.isLimitLimbs = true;
            renderData.epicFlightContext = null;
            SkinRendererManager.getInstance().didRender(entity, entityRenderer.getModel(), entityRenderer, renderData, () -> {
                return SkinRenderContext.alloc(renderData, packedLight, partialTicks, poseStack, buffers);
            });
        }
    }

}

