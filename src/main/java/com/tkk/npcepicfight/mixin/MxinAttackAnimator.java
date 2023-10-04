package com.tkk.npcepicfight.mixin;

import com.tkk.npcepicfight.entitypatch.CustomNpcPatch;
import net.minecraft.entity.Entity;
import noppes.npcs.entity.EntityCustomNpc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.client.events.engine.RenderEngine;
import yesman.epicfight.client.renderer.patched.entity.PatchedEntityRenderer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.Optional;

@Mixin(value = AttackAnimation.class,remap = false)
public abstract class MxinAttackAnimator extends ActionAnimation {

    public MxinAttackAnimator(float convertTime, String path, Model model) {
        super(convertTime, path, model);
    }

    @Inject(at = @At(value = "HEAD"),method = "Lyesman/epicfight/api/animation/types/AttackAnimation;getPlaySpeed(Lyesman/epicfight/world/capabilities/entitypatch/LivingEntityPatch;)F", cancellable = true)
    public void tkk_getPlaySpeed(LivingEntityPatch<?> entitypatch, CallbackInfoReturnable<Float> cir) {
        if(entitypatch instanceof CustomNpcPatch){
            float speedFactor = this.getProperty(AnimationProperty.AttackAnimationProperty.ATTACK_SPEED_FACTOR).orElse(1.0F);
            Optional<Float> property = this.getProperty(AnimationProperty.AttackAnimationProperty.BASIS_ATTACK_SPEED);
            float correctedSpeed = property.map((value) -> ((CustomNpcPatch) entitypatch).attackSpeed / value)
                    .orElse(this.totalTime * ((CustomNpcPatch) entitypatch).attackSpeed);

            correctedSpeed = Math.round(correctedSpeed * 1000.0F) / 1000.0F;
            cir.setReturnValue(1.0F + (correctedSpeed - 1.0F) * speedFactor);
        }
    }

}
