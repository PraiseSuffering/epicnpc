package com.tkk.npcepicfight.mixin;

import com.tkk.npcepicfight.entitypatch.CustomNpcPatch;
import com.tkk.npcepicfight.handler.HandlerEntityNPCInterface;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.BasicAttackAnimation;
import yesman.epicfight.api.animation.types.LinkAnimation;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

import javax.annotation.Nullable;
import java.util.Optional;

@Mixin(value = BasicAttackAnimation.class,remap = false)
public abstract class MixinBasicAttackAnimation extends AttackAnimation{


    public MixinBasicAttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, @Nullable Collider collider, String index, String path, Model model) {
        super(convertTime, antic, preDelay, contact, recovery, collider, index, path, model);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void setLinkAnimation(Pose pose1, float timeModifier, LivingEntityPatch<?> entitypatch, LinkAnimation dest) {
        float extTime = Math.max(this.convertTime + timeModifier, 0.0F);
        if(entitypatch instanceof CustomNpcPatch){
            Phase phase = this.getPhaseByTime(entitypatch.getAnimator().getPlayerFor(this).getElapsedTime());
            extTime *= (float)(this.totalTime * ((CustomNpcPatch) entitypatch).attackSpeed);
        }
        if (entitypatch instanceof PlayerPatch) {
            PlayerPatch<?> playerpatch = (PlayerPatch)entitypatch;
            Phase phase = this.getPhaseByTime(playerpatch.getAnimator().getPlayerFor(this).getElapsedTime());
            extTime *= this.totalTime * playerpatch.getAttackSpeed(phase.getHand());
        }

        extTime = Math.max(extTime - this.convertTime, 0.0F);
        super.setLinkAnimation(pose1, extTime, entitypatch, dest);
    }

}
