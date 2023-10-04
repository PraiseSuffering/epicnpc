package com.tkk.npcepicfight.mixin;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.tkk.npcepicfight.handler.HandlerEntityNPCInterface;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import noppes.npcs.client.renderer.RenderCustomNpc;
import noppes.npcs.entity.EntityCustomNpc;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import yesman.epicfight.api.client.model.ClientModels;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.events.engine.RenderEngine;
import yesman.epicfight.client.renderer.patched.entity.PatchedEntityRenderer;
import yesman.epicfight.client.renderer.patched.entity.PatchedLivingEntityRenderer;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;

@Mixin(value = PatchedLivingEntityRenderer.class,remap = false)
public abstract class MixinPatchedLivingEntityRenderer<E extends LivingEntity, T extends LivingEntityPatch<E>, M extends EntityModel<E>> extends PatchedEntityRenderer<E, T, LivingRenderer<E, M>> {

    private static Field NPC_RENDERENTITY;
    static {
        try {
            NPC_RENDERENTITY=RenderCustomNpc.class.getDeclaredField("renderEntity");
            NPC_RENDERENTITY.setAccessible(true);
        }catch (Exception e){

        }
    }


    @ModifyVariable(at=@At("HEAD"),method = "renderLayer(Lnet/minecraft/client/renderer/entity/LivingRenderer;Lyesman/epicfight/world/capabilities/entitypatch/LivingEntityPatch;Lnet/minecraft/entity/LivingEntity;[Lyesman/epicfight/api/utils/math/OpenMatrix4f;Lnet/minecraft/client/renderer/IRenderTypeBuffer;Lcom/mojang/blaze3d/matrix/MatrixStack;IF)V")
    private LivingRenderer getNpcLivingRenderer(LivingRenderer<E, M> renderer){
        LivingRenderer<E,M> temp=renderer;
        if(temp instanceof RenderCustomNpc){
            try {
                temp=(LivingRenderer) NPC_RENDERENTITY.get(temp);

            }catch (Exception e){
                EpicFightMod.LOGGER.log(Level.ERROR,"[2kk2]: noppes.npcs.client.renderer.RenderCustomNpc.renderLayer error "+e);
            }

        }
        return temp;
    }
    @ModifyVariable(at=@At("HEAD"),method = "renderLayer(Lnet/minecraft/client/renderer/entity/LivingRenderer;Lyesman/epicfight/world/capabilities/entitypatch/LivingEntityPatch;Lnet/minecraft/entity/LivingEntity;[Lyesman/epicfight/api/utils/math/OpenMatrix4f;Lnet/minecraft/client/renderer/IRenderTypeBuffer;Lcom/mojang/blaze3d/matrix/MatrixStack;IF)V")
    private E getNpcLivingEntityIn(E entityIn){
        E tempB=entityIn;
        if(tempB instanceof EntityCustomNpc){
            EntityCustomNpc npc=(EntityCustomNpc) tempB;
            if(npc.modelData != null && npc.modelData.getEntity(npc) != null) {
                tempB = (E) npc.modelData.getEntity(npc);
            } else {
                tempB = entityIn;
            }
        }
        return tempB;
    }
}
