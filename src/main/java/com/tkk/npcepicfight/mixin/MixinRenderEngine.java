package com.tkk.npcepicfight.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import noppes.npcs.entity.EntityCustomNpc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.client.events.engine.RenderEngine;
import yesman.epicfight.client.renderer.patched.entity.PatchedEntityRenderer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.Map;
import java.util.function.Supplier;

@Mixin(value = RenderEngine.class,remap = false)
public abstract class MixinRenderEngine {

    @Shadow private Map<EntityType<?>, Supplier<PatchedEntityRenderer>> entityRendererProvider;
    @Shadow private Map<EntityType<?>, PatchedEntityRenderer> entityRendererCache;

    @Shadow public abstract PatchedEntityRenderer getEntityRenderer(Entity entity);

    @Shadow public abstract void registerRenderer();

    @Inject(at = @At(value = "HEAD"),method = "getEntityRenderer(Lnet/minecraft/entity/Entity;)Lyesman/epicfight/client/renderer/patched/entity/PatchedEntityRenderer;", cancellable = true)
    public void tkk_getEntityRenderer(Entity entity, CallbackInfoReturnable<PatchedEntityRenderer> cir) {
        if(entity instanceof EntityCustomNpc){
            if(((EntityCustomNpc)entity).modelData.getEntity((EntityCustomNpc) entity)!=null){
                cir.setReturnValue(this.entityRendererCache.get(((EntityCustomNpc)entity).modelData.getEntity((EntityCustomNpc) entity).getType()));
            }
        }
    }

    @Inject(at = @At(value = "HEAD"),method = "hasRendererFor(Lnet/minecraft/entity/Entity;)Z", cancellable = true)
    public void tkk_hasRendererFor(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if(entity instanceof EntityCustomNpc){
            if(((EntityCustomNpc)entity).modelData.getEntity((EntityCustomNpc) entity)!=null){
                Entity temp=((EntityCustomNpc)entity).modelData.getEntity((EntityCustomNpc) entity);
                cir.setReturnValue(this.entityRendererCache.computeIfAbsent(temp.getType(), (key) -> this.entityRendererProvider.containsKey(key) ? this.entityRendererProvider.get(temp.getType()).get() : null) != null);
            }
        }
    }

    @Inject(at = @At(value = "HEAD"),method = "renderEntityArmatureModel(Lnet/minecraft/entity/LivingEntity;Lyesman/epicfight/world/capabilities/entitypatch/LivingEntityPatch;Lnet/minecraft/client/renderer/entity/LivingRenderer;Lnet/minecraft/client/renderer/IRenderTypeBuffer;Lcom/mojang/blaze3d/matrix/MatrixStack;IF)V", cancellable = true)
    public void tkk_renderEntityArmatureModel(LivingEntity livingEntity, LivingEntityPatch<?> entitypatch, LivingRenderer<? extends Entity, ?> renderer, IRenderTypeBuffer buffer, MatrixStack matStack, int packedLightIn, float partialTicks, CallbackInfo ci) {
        if(livingEntity instanceof EntityCustomNpc){
            if(((EntityCustomNpc)livingEntity).modelData.getEntity((EntityCustomNpc) livingEntity)!=null){
                LivingEntity temp=((EntityCustomNpc)livingEntity).modelData.getEntity((EntityCustomNpc) livingEntity);
                this.getEntityRenderer(temp).render(livingEntity, entitypatch, renderer, buffer, matStack, packedLightIn, partialTicks);
                ci.cancel();
            }
        }
    }

}
