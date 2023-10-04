package com.tkk.npcepicfight.mixin;

import com.tkk.npcepicfight.handler.HandlerEntityNPCInterface;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;

@Mixin(value = EntityNPCInterface.class)
public abstract class MixinEntityNPCInterface extends CreatureEntity implements IEntityAdditionalSpawnData, IRangedAttackMob {

    protected MixinEntityNPCInterface(EntityType<? extends CreatureEntity> p_i48575_1_, World p_i48575_2_) {
        super(p_i48575_1_, p_i48575_2_);
    }

    /**
     * @author
     * @reason Fixed Battle status block changing to interactive
     * if (this.field_70170_p.field_72995_K) {
     *             return this.isAttacking() ? ActionResultType.SUCCESS : ActionResultType.FAIL;
     */
    @Overwrite(remap = false)
    protected ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
        return HandlerEntityNPCInterface.func_230254_b_(this,player,hand);
    }
}
