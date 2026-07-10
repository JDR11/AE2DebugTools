package com.JDR11.ae2debugtools.common.network;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.WorldServer;

import com.JDR11.ae2debugtools.Ae2DebugTools;
import com.JDR11.ae2debugtools.common.NetworkScanner;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketScanRequest implements IMessage {

    private String filter;

    // public PacketScanRequest() {}

    public PacketScanRequest(String filter) {
        this.filter = filter;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, filter);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        filter = ByteBufUtils.readUTF8String(buf);
    }

    public static class Handler implements IMessageHandler<PacketScanRequest, IMessage> {

        @Override
        public IMessage onMessage(final PacketScanRequest message, final MessageContext ctx) {
            final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            final WorldServer world = (WorldServer) player.worldObj;

            MainThreadScheduler.scheduleServerTask(new Runnable() {

                @Override
                public void run() {
                    Ae2DebugTools.LOG
                        .info("[server] Scan request from {} for filter {}", player.getDisplayName(), message.filter);

                    List<ChunkCoordinates> results = NetworkScanner.scan(world, player, message.filter, 32);

                    PacketHandler.INSTANCE.sendTo(new PacketScanResponse(message.filter, results), player);
                }
            });

            return null;
        }
    }
}
