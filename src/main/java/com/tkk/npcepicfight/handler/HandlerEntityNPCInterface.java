package com.tkk.npcepicfight.handler;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.IntNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import noppes.npcs.CustomItems;
import noppes.npcs.EventHooks;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.DialogOption;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.QuestData;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketQuestCompletion;

import java.util.Iterator;

public class HandlerEntityNPCInterface {
    public static ActionResultType func_230254_b_(Object target,PlayerEntity player, Hand hand){
        if(!(target instanceof EntityCustomNpc)){return ActionResultType.FAIL;}
        EntityCustomNpc t=(EntityCustomNpc) target;
        if (t.level.isClientSide) {
            //return this.isAttacking() ? ActionResultType.SUCCESS : ActionResultType.FAIL;
            return t.isAttacking() ? ActionResultType.FAIL : ActionResultType.FAIL;
        } else if (hand != Hand.MAIN_HAND) {
            return ActionResultType.PASS;
        } else {
            ItemStack stack = player.getItemInHand(hand);
            if (stack != null) {
                Item item = stack.getItem();
                if (item == CustomItems.cloner || item == CustomItems.wand || item == CustomItems.mount || item == CustomItems.scripter) {
                    t.setTarget((LivingEntity)null);
                    t.setLastHurtByMob((LivingEntity)null);
                    return ActionResultType.SUCCESS;
                }

                if (item == CustomItems.moving) {
                    t.setTarget((LivingEntity)null);
                    stack.addTagElement("NPCID", IntNBT.valueOf(t.getId()));
                    player.sendMessage(new TranslationTextComponent("message.pather.register", new Object[]{t.getName()}), t.getUUID());
                    return ActionResultType.SUCCESS;
                }
            }

            if (EventHooks.onNPCInteract(t, player)) {
                return ActionResultType.FAIL;
            } else if (t.getFaction().isAggressiveToPlayer(player)) {
                return ActionResultType.FAIL;
            } else {
                t.addInteract(player);
                Dialog dialog = getDialog(t,player);

                QuestData data = PlayerData.get(player).questData.getQuestCompletion(player, t);
                if (data != null) {
                    Packets.send((ServerPlayerEntity)player, new PacketQuestCompletion(data.quest.id));
                } else if (dialog != null) {
                    NoppesUtilServer.openDialog(player, t, dialog);
                } else if (t.role.getType() != 0) {
                    t.role.interact(player);
                } else {
                    t.say(player, t.advanced.getInteractLine());
                }

                return ActionResultType.PASS;
            }
        }
    }
    private static Dialog getDialog(EntityCustomNpc t,PlayerEntity player) {
        Iterator var2 = t.dialogs.values().iterator();

        while(var2.hasNext()) {
            DialogOption option = (DialogOption)var2.next();
            if (option != null && option.hasDialog()) {
                Dialog dialog = option.getDialog();
                if (dialog.availability.isAvailable(player)) {
                    return dialog;
                }
            }
        }

        return null;
    }
}
