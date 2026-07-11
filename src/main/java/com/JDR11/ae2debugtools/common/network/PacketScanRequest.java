package com.JDR11.ae2debugtools.common.network;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;

import com.JDR11.ae2debugtools.Config;
import com.JDR11.ae2debugtools.common.NetworkScanner;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketScanRequest implements IMessage {

    private String filter;
    private int itemId;
    private int itemDamage;

    public PacketScanRequest() {}

    public PacketScanRequest(String filter, ItemStack itemFilterStack) {
        this.filter = filter;
        if (itemFilterStack != null) {
            this.itemId = Item.getIdFromItem(itemFilterStack.getItem());
            this.itemDamage = itemFilterStack.getItemDamage();
        } else {
            this.itemId = -1;
            this.itemDamage = 0;
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, filter);
        buf.writeInt(itemId);
        buf.writeInt(itemDamage);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        filter = ByteBufUtils.readUTF8String(buf);
        itemId = buf.readInt();
        itemDamage = buf.readInt();
    }

    public static class Handler implements IMessageHandler<PacketScanRequest, IMessage> {

        @Override
        public IMessage onMessage(final PacketScanRequest message, final MessageContext ctx) {
            final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            final WorldServer world = (WorldServer) player.worldObj;

            MainThreadScheduler.scheduleServerTask(new Runnable() {

                @Override
                public void run() {
                    ItemStack itemFilter = null;
                    if (message.itemId != -1) {
                        Item item = Item.getItemById(message.itemId);
                        if (item != null) {
                            itemFilter = new ItemStack(item, 1, message.itemDamage);
                        }
                    }

                    List<NetworkScanner.ScanMatch> matches = NetworkScanner
                        .scan(player, message.filter, itemFilter, Config.scanRadius);

                    PacketHandler.INSTANCE.sendTo(new PacketScanResponse(message.filter, matches), player);
                    /**
                     * Ae2DebugTools.LOG
                     * .info("[server] Scan request from {} for filter {}", player.getDisplayName(), message.filter);
                     *
                     * NetworkScanner.ScanResult scanResult = NetworkScanner
                     * .scan(world, player, message.filter, itemFilter, Config.scanRadius);
                     *
                     * if (!scanResult.otherDimensionCounts.isEmpty()) {
                     * StringBuilder stringBuilder = new StringBuilder("Also found matching parts in: ");
                     * boolean first = true;
                     * for (Map.Entry<Integer, Integer> entry : scanResult.otherDimensionCounts.entrySet()) {
                     * if (!first) stringBuilder.append(", ");
                     * stringBuilder.append(entry.getValue())
                     * .append(" in dimension ")
                     * .append(entry.getKey());
                     * first = false;
                     * }
                     * player.addChatMessage(new ChatComponentText(stringBuilder.toString()));
                     * player.addChatComponentMessage(
                     * new ChatComponentText(
                     * DimensionManager.getWorld(player.dimension)
                     * .getProviderName()
                     * .toString()));
                     * }
                     **/
                }
            });

            return null;
        }
    }
}
