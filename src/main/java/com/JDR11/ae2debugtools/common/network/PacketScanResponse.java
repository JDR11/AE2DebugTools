package com.JDR11.ae2debugtools.common.network;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;

import com.JDR11.ae2debugtools.Ae2DebugTools;
import com.JDR11.ae2debugtools.Config;
import com.JDR11.ae2debugtools.client.render.CubeRenderer;
import com.JDR11.ae2debugtools.client.render.CubeRendererTarget;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketScanResponse implements IMessage {

    private String filter;
    private int[] coordinates;

    public PacketScanResponse() {}

    public PacketScanResponse(String filter, List<ChunkCoordinates> results) {
        this.filter = filter;
        this.coordinates = new int[results.size() * 3];
        for (int i = 0; i < results.size(); i++) {
            ChunkCoordinates coord = results.get(i);
            coordinates[i * 3] = coord.posX;
            coordinates[i * 3 + 1] = coord.posY;
            coordinates[i * 3 + 2] = coord.posZ;
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, filter);
        buf.writeInt(coordinates.length);
        for (int c : coordinates) buf.writeInt(c);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        filter = ByteBufUtils.readUTF8String(buf);
        int size = buf.readInt();
        coordinates = new int[size];
        for (int i = 0; i < size; i++) coordinates[i] = buf.readInt();
    }

    public static class Handler implements IMessageHandler<PacketScanResponse, IMessage> {

        @Override
        public IMessage onMessage(PacketScanResponse message, MessageContext ctx) {
            MainThreadScheduler.scheduleClientTask(new Runnable() {

                @Override
                public void run() {
                    int matching = message.coordinates.length / 3;
                    Ae2DebugTools.LOG.info("[client] Scan response: {} matches for '{}'", matching, message.filter);

                    List<CubeRendererTarget> targets = new ArrayList<>();
                    for (int i = 0; i < matching; i++) {
                        BlockPos pos = new BlockPos(
                            message.coordinates[i * 3],
                            message.coordinates[i * 3 + 1],
                            message.coordinates[i * 3 + 2]);
                        targets.add(new CubeRendererTarget(pos, new Color(Config.cubeHighlightColour)));
                    }
                    CubeRenderer.INSTANCE.draw(targets);

                    Minecraft.getMinecraft().thePlayer.addChatComponentMessage(
                        new ChatComponentText(
                            "Scan found " + matching + " block(s) matching \"" + message.filter + "\""));
                }
            });
            return null;
        }
    }
}
