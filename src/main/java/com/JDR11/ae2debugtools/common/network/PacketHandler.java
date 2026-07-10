package com.JDR11.ae2debugtools.common.network;

import com.JDR11.ae2debugtools.Ae2DebugTools;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler {

    public static SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Ae2DebugTools.MODID);

    public static void init() {
        int id = 0;
        INSTANCE.registerMessage(PacketScanRequest.Handler.class, PacketScanRequest.class, id++, Side.SERVER);
        INSTANCE.registerMessage(PacketScanResponse.Handler.class, PacketScanResponse.class, id++, Side.CLIENT);
    }
}
