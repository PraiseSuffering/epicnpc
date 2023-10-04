package com.tkk.npcepicfight.entitypatch;

import com.tkk.EpicNpcMain;
import com.tkk.npcepicfight.network.EpicNpcNetworkManager;
import com.tkk.npcepicfight.network.SPTkkNpcUpdata;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.entity.EntityCustomNpc;
import org.apache.logging.log4j.Level;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Models;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPSpawnData;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class CustomNpcPatch <T extends EntityCustomNpc> extends HumanoidMobPatch<T> {
    public ResourceLocation model;
    public static float NPC_SCALE_OFFSET=0.1825f;
    public float scaleX;
    public float scaleY;
    public float scaleZ;
    public float attackSpeed=1;
    private HashMap<LivingMotions, ResourceLocation> animatorList;
    public HashMap<String,String> animatorIdList;
    public CustomNpcPatch() {
        super(Faction.NEUTRAL);
        model=new ResourceLocation("epicfight", "entity/biped_old_texture");
        animatorIdList=new HashMap<>();
        animatorList=new HashMap();
        animatorIdList.put("IDLE", "epicfight:biped/living/idle");
        animatorIdList.put("WALK", "epicfight:biped/living/walk");
        animatorIdList.put("DEATH","epicfight:biped/living/death");
        scaleX=1.0f;
        scaleY=1.0f;
        scaleZ=1.0f;
        syncAnimator();
    }
    public void tkkNpcUpdata(){
        EpicNpcNetworkManager.sendToAllPlayerTrackingThisEntity(new SPTkkNpcUpdata(this.original), this.original);
    }

    @Override
    protected void initAI() {}
    @Override
    public void onStartTracking(ServerPlayerEntity trackingPlayer) {
        if (!this.getHoldingItemCapability(Hand.MAIN_HAND).isEmpty()) {
            SPSpawnData packet = new SPSpawnData(this.original.getId());
            EpicFightNetworkManager.sendToPlayer(packet, trackingPlayer);
        }
        super.onStartTracking(trackingPlayer);
        //此处同步npc自定义模型数据
        EpicNpcNetworkManager.sendToPlayer(new SPTkkNpcUpdata(this.getOriginal()),trackingPlayer);
    }

    @Override
    public void processSpawnData(ByteBuf buf) {
        AnimationManager animationManager = EpicFightMod.getInstance().animationManager;
        Map<ResourceLocation, StaticAnimation> animationMap=animationManager.getNameMap();
        ClientAnimator animator = this.getClientAnimator();
        for(Map.Entry<LivingMotions,ResourceLocation> entry:animatorList.entrySet()){
            animator.addLivingAnimation(entry.getKey(),animationMap.get(entry.getValue()));
        }
        animator.setCurrentMotionsAsDefault();
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.original.getAttribute(EpicFightAttributes.OFFHAND_IMPACT.get()).setBaseValue(1.0D);
        this.original.getAttribute(EpicFightAttributes.OFFHAND_ATTACK_DAMAGE.get()).setBaseValue(1.0D);
        this.original.getAttribute(EpicFightAttributes.OFFHAND_MAX_STRIKES.get()).setBaseValue(1);
        this.original.getAttribute(EpicFightAttributes.OFFHAND_ARMOR_NEGATION.get()).setBaseValue(0);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initAnimator(ClientAnimator clientAnimator) {
        AnimationManager animationManager = EpicFightMod.getInstance().animationManager;
        Map<ResourceLocation, StaticAnimation> animationMap=animationManager.getNameMap();
        for(Map.Entry<LivingMotions,ResourceLocation> entry:animatorList.entrySet()){
            animator.addLivingAnimation(entry.getKey(),animationMap.get(entry.getValue()));
        }
        clientAnimator.setCurrentMotionsAsDefault();
    }


    public void syncAnimator(){
        animatorList.clear();
        for (Map.Entry<String,String> entry : animatorIdList.entrySet()) {
            String key= entry.getKey();
            String[] value= entry.getValue().split(":",2);
            animatorList.put(LivingMotions.valueOf(key),new ResourceLocation(value[0],value[1]));
        }
    }
    @Override
    public void updateMotion(boolean considerInaction) {
        super.commonAggressiveMobUpdateMotion(considerInaction);
    }

    @Override
    public <M extends Model> M getEntityModel(Models<M> modelDB) {
        M model=modelDB.get(this.model);
        if(model==null){
            EpicNpcMain.LOGGER.log(Level.ERROR,"error model ResourceLocation: " + this.model);
            return modelDB.bipedOldTexture;
        }
        return model;
    }

    @Override
    public OpenMatrix4f getModelMatrix(float partialTicks) {
        float scale = this.getOriginal().display.getSize()*NPC_SCALE_OFFSET;
        return super.getModelMatrix(partialTicks).scale(scale*this.scaleX, scale*this.scaleY, scale*this.scaleZ);
    }

    @Nullable
    public static StaticAnimation loadAnimation(String str){
        int lastIndex = str.lastIndexOf(".");

        if (lastIndex != -1) {
            String identifier = str.substring(0, lastIndex);
            String lastPart = str.substring(lastIndex + 1);

            //System.out.println("Identifier: " + identifier);
            //System.out.println("Last part: " + lastPart);
            try{
                return loadAnimation(identifier,lastPart);
            }catch (Throwable e){
            }
        } else {
            //System.out.println("无法找到标识符");
        }
        EpicNpcMain.LOGGER.log(Level.ERROR,"error animation: " + str);
        return null;

    }
    public static StaticAnimation loadAnimation(String dass,String field) throws Throwable {
        Class c=Class.forName(dass);
        Object animation=c.getField(field).get(c);
        if(animation instanceof StaticAnimation){
            return (StaticAnimation) animation;
        }else{
            throw new Throwable(dass+"."+field+" !instanceof StaticAnimation");
        }
    }

}
