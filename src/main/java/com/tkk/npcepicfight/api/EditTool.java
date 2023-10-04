package com.tkk.npcepicfight.api;

import com.tkk.npcepicfight.entitypatch.CustomNpcPatch;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.entity.EntityCustomNpc;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import javax.annotation.Nullable;
import javax.swing.text.html.parser.Entity;
import java.util.HashMap;

public enum EditTool {
    AVOID_ONLY_CLIENT;




    public boolean playAnimations(LivingEntity entity, ResourceLocation animations,float convertTimeModifier){
        LivingEntityPatch patch=(LivingEntityPatch)entity.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
        if (patch==null){return false;}
        StaticAnimation a = EpicFightMod.getInstance().animationManager.getNameMap().get(animations);
        if(a==null){return false;}
        patch.playAnimationSynchronized(a,convertTimeModifier);
        return true;
    }

    public int getPhaseLevel(LivingEntity entity){
        LivingEntityPatch patch=(LivingEntityPatch)entity.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
        if (patch==null){return -999;}
        return patch.getEntityState().getLevel();
    }

    public void setNPCModel(EntityCustomNpc npc,ResourceLocation model){
        CustomNpcPatch patch=(CustomNpcPatch)npc.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
        patch.model=model;
        patch.tkkNpcUpdata();
    }
    public HashMap<String,String> getNPCAnimatorIdList(EntityCustomNpc npc){
        CustomNpcPatch patch=(CustomNpcPatch)npc.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
        return patch.animatorIdList;
    }
    public void NPCSyncAnimator(EntityCustomNpc npc){
        CustomNpcPatch patch=(CustomNpcPatch)npc.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
        patch.syncAnimator();
        patch.tkkNpcUpdata();
    }

    public float getNPCAttackSpeed(EntityCustomNpc npc){
        CustomNpcPatch patch=(CustomNpcPatch)npc.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
        return patch.attackSpeed;
    }

    public void setNPCAttackSpeed(EntityCustomNpc npc,float speed){
        CustomNpcPatch patch=(CustomNpcPatch)npc.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
        patch.attackSpeed=speed;
        patch.tkkNpcUpdata();
    }










}
