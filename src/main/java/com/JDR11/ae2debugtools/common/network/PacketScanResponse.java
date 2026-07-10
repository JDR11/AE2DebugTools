package com.JDR11.ae2debugtools.common.network;

import java.awt.Color;
import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.util.ChatComponentText;

import com.JDR11.ae2debugtools.Ae2DebugTools;
import com.JDR11.ae2debugtools.Config;
import com.JDR11.ae2debugtools.client.render.CubeRenderer;
import com.JDR11.ae2debugtools.client.render.CubeRendererTarget;
import com.JDR11.ae2debugtools.common.NetworkScanner.ScanMatch;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketScanResponse implements IMessage {

    private String filter;
    private List<ScanMatch> matches;

    public PacketScanResponse() {}

    public PacketScanResponse(String filter, List<ScanMatch> matches) {
        this.filter = filter;
        this.matches = matches;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, filter);
        buf.writeInt(matches.size());
        for (ScanMatch match : matches) {
            buf.writeInt(match.x);
            buf.writeInt(match.y);
            buf.writeInt(match.z);
            buf.writeInt(match.dimension);
            ByteBufUtils.writeUTF8String(buf, match.label);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        filter = ByteBufUtils.readUTF8String(buf);
        int size = buf.readInt();
        matches = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            int x = buf.readInt();
            int y = buf.readInt();
            int z = buf.readInt();
            int dimension = buf.readInt();
            String label = ByteBufUtils.readUTF8String(buf);
            matches.add(new ScanMatch(x, y, z, dimension, label));
        }
    }

    public static class Handler implements IMessageHandler<PacketScanResponse, IMessage> {

        @Override
        public IMessage onMessage(PacketScanResponse message, MessageContext ctx) {
            MainThreadScheduler.scheduleClientTask(new Runnable() {

                @Override
                public void run() {
                    EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
                    int currentDimension = player.dimension;

                    List<ScanMatch> sameDimension = new ArrayList<>();
                    Map<Integer, Integer> otherDimensionCounts = new HashMap<>();

                    for (ScanMatch match : message.matches) {
                        if (match.dimension == currentDimension) {
                            sameDimension.add(match);
                        } else {
                            otherDimensionCounts.merge(match.dimension, 1, Integer::sum);
                        }
                    }

                    sameDimension
                        .sort(Comparator.comparingDouble(m -> distanceSq(m, player.posX, player.posY, player.posZ)));

                    int renderCount = Math.min(Config.maxResults, sameDimension.size());
                    List<CubeRendererTarget> targets = new ArrayList<>();
                    for (int i = 0; i < renderCount; i++) {
                        ScanMatch match = sameDimension.get(i);
                        targets.add(
                            new CubeRendererTarget(
                                new BlockPos(match.x, match.y, match.z),
                                new Color(Config.cubeHighlightColour)));
                    }
                    CubeRenderer.INSTANCE.draw(targets);

                    StringBuilder chatMessage = new StringBuilder(
                        "Scan found " + message.matches.size()
                            + " total match(es) for \""
                            + message.filter
                            + "\" "
                            + "Rendering nearest "
                            + renderCount
                            + " in this dimension.");
                    if (!otherDimensionCounts.isEmpty()) {
                        chatMessage.append(" Also found: ");
                        boolean first = true;
                        for (Map.Entry<Integer, Integer> entry : otherDimensionCounts.entrySet()) {
                            if (!first) chatMessage.append(", ");
                            chatMessage.append(entry.getValue())
                                .append(" in dimension ")
                                .append(entry.getKey());
                            first = false;
                        }
                    }
                    player.addChatComponentMessage(new ChatComponentText(chatMessage.toString()));

                    if (Config.writeReportFile) {
                        writeReport(message.filter, message.matches);
                    }
                }
            });
            return null;
        }

        private static double distanceSq(ScanMatch match, double playerX, double playerY, double playerZ) {
            double dx = (match.x + 0.5) - playerX;
            double dy = (match.y + 0.5) - playerY;
            double dz = (match.z + 0.5) - playerZ;
            return dx * dx + dy * dy + dz * dz;
        }

        private static void writeReport(String filter, List<ScanMatch> matches) {
            File dir = new File(Minecraft.getMinecraft().mcDataDir, "ae2debugtools/scan_reports");
            if (!dir.exists() && !dir.mkdirs()) {
                Ae2DebugTools.LOG.warn("[client] Failed to create scan report directory: {}", dir.getAbsolutePath());
                return;
            }

            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            File reportFile = new File(dir, "scan_" + timestamp + ".txt");

            EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
            double playerX = player.posX;
            double playerY = player.posY;
            double playerZ = player.posZ;

            Map<Integer, List<ScanMatch>> byDimension = new TreeMap<>();
            for (ScanMatch match : matches) {
                byDimension.computeIfAbsent(match.dimension, k -> new ArrayList<>())
                    .add(match);
            }

            for (List<ScanMatch> dimensionMatches : byDimension.values()) {
                dimensionMatches.sort(Comparator.comparingDouble(m -> distanceSq(m, playerX, playerY, playerZ)));
            }

            try (PrintWriter writer = new PrintWriter(reportFile, "UTF-8")) {
                writer.println("AE2DebugTools scan report");
                writer.println("Filter: \"" + filter + "\"");
                writer.println("Matches: " + matches.size());
                writer.println();

                for (Map.Entry<Integer, List<ScanMatch>> entry : byDimension.entrySet()) {
                    int dimension = entry.getKey();
                    List<ScanMatch> dimensionMatches = entry.getValue();

                    writer.println("== Dimension " + dimension + " (" + dimensionMatches.size() + " match(es)) ==");
                    for (ScanMatch match : dimensionMatches) {
                        writer.printf("  (%d, %d, %d) - %s%n", match.x, match.y, match.z, match.label);
                    }
                    writer.println();
                }

                Minecraft.getMinecraft().thePlayer.addChatComponentMessage(
                    new ChatComponentText("Full scan report saved to: " + reportFile.getAbsolutePath()));
            } catch (Exception e) {
                Ae2DebugTools.LOG.warn("[client] Failed to write scan report", e);
            }
        }
    }
}
