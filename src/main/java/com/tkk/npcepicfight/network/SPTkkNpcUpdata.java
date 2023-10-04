package com.tkk.npcepicfight.network;

import com.tkk.npcepicfight.entitypatch.CustomNpcPatch;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import noppes.npcs.entity.EntityCustomNpc;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class SPTkkNpcUpdata {
    private int entityId;
    private String modelPath;
    private String modelNamespace;
    private float scaleX;
    private float scaleY;
    private float scaleZ;
    private float attackSpeed;
    private int animSize;
    private String[] keyAndValue;
    public SPTkkNpcUpdata() {

    }

    public SPTkkNpcUpdata(EntityCustomNpc npc) {
        this.entityId=npc.getId();
        CustomNpcPatch patch = (CustomNpcPatch) npc.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
        this.modelNamespace=patch.model.getNamespace();
        this.modelPath=patch.model.getPath();
        this.scaleX = patch.scaleX;
        this.scaleY = patch.scaleY;
        this.scaleZ = patch.scaleZ;
        this.attackSpeed=patch.attackSpeed;
        this.animSize=patch.animatorIdList.size()*2;
        keyAndValue=new String[this.animSize];
        Set<Map.Entry<String,String>> set=patch.animatorIdList.entrySet();
        int i=0;
        for (Map.Entry<String,String> entry : set) {
            keyAndValue[i]=entry.getKey();
            i+=1;
            keyAndValue[i]= entry.getValue();
            i+=1;
        }

    }

    public static SPTkkNpcUpdata fromBytes(PacketBuffer buf) {
        SPTkkNpcUpdata msg = new SPTkkNpcUpdata();
        msg.entityId=buf.readInt();
        msg.modelNamespace=buf.readUtf();
        msg.modelPath=buf.readUtf();
        msg.animSize=buf.readInt();
        msg.scaleX=buf.readFloat();
        msg.scaleY=buf.readFloat();
        msg.scaleZ=buf.readFloat();
        msg.attackSpeed=buf.readFloat();
        msg.keyAndValue=new String[msg.animSize];
        for(int i=0;i<msg.animSize;i++){
            msg.keyAndValue[i]=buf.readUtf();
        }
        return msg;
    }

    public static void toBytes(SPTkkNpcUpdata msg, PacketBuffer buf) {
        buf.writeInt(msg.entityId);
        buf.writeUtf(msg.modelNamespace);
        buf.writeUtf(msg.modelPath);
        buf.writeInt(msg.animSize);
        buf.writeFloat(msg.scaleX);
        buf.writeFloat(msg.scaleY);
        buf.writeFloat(msg.scaleZ);
        buf.writeFloat(msg.attackSpeed);
        for(String str: msg.keyAndValue){
            buf.writeUtf(str);
        }
    }

    public static void handle(SPTkkNpcUpdata msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            Entity entity = mc.player.level.getEntity(msg.entityId);
            CustomNpcPatch patch = (CustomNpcPatch) entity.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
            patch.model= new ResourceLocation(msg.modelNamespace,msg.modelPath);
            patch.scaleX = msg.scaleX;
            patch.scaleY = msg.scaleY;
            patch.scaleZ = msg.scaleZ;
            patch.attackSpeed = msg.attackSpeed;
            patch.animatorIdList.clear();
            int i=0;
            while(i<msg.animSize){
                patch.animatorIdList.put(msg.keyAndValue[i],msg.keyAndValue[i+1]);
                i+=2;
            }
            patch.syncAnimator();
            patch.initAnimator(patch.getClientAnimator());
        });
        ctx.get().setPacketHandled(true);
    }
}
