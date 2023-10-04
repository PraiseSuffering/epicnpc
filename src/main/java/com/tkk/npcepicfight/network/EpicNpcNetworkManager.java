package com.tkk.npcepicfight.network;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class EpicNpcNetworkManager {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation("epicnpc", "network_manager"), () -> "1", "1"::equals, "1"::equals);

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToClient(MSG message, PacketDistributor.PacketTarget packetTarget) {
        INSTANCE.send(packetTarget, message);
    }

    public static <MSG> void sendToAll(MSG message) {
        sendToClient(message, PacketDistributor.ALL.noArg());
    }

    public static <MSG> void sendToAllPlayerTrackingThisEntity(MSG message, Entity entity) {
        sendToClient(message, PacketDistributor.TRACKING_ENTITY.with(() -> entity));
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayerEntity player) {
        sendToClient(message, PacketDistributor.PLAYER.with(() -> player));
    }

    public static <MSG> void sendToAllPlayerTrackingThisEntityWithSelf(MSG message, ServerPlayerEntity entity) {
        sendToPlayer(message, entity);
        sendToClient(message, PacketDistributor.TRACKING_ENTITY.with(() -> entity));
    }

    public static void registerPackets() {
        int id = 0;
        INSTANCE.registerMessage(id++, SPTkkNpcUpdata.class, SPTkkNpcUpdata::toBytes, SPTkkNpcUpdata::fromBytes, SPTkkNpcUpdata::handle);
    }
}
