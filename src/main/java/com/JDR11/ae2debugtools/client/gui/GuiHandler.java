package com.JDR11.ae2debugtools.client.gui;

import net.minecraft.client.Minecraft;

import com.JDR11.ae2debugtools.Ae2DebugTools;
import com.JDR11.ae2debugtools.ClientProxy;
import com.cleanroommc.modularui.factory.GuiFactories;
import com.cleanroommc.modularui.factory.SimpleGuiFactory;
import com.cleanroommc.modularui.utils.item.IItemHandlerModifiable;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;

public class GuiHandler {

    private final IItemHandlerModifiable invHandler = new ItemStackHandler(1);

    private static final SimpleGuiFactory FACTORY = GuiFactories
        .createSimple("ae2debugtools:scanner", new ScannerGui());

    @SubscribeEvent
    public void openGui(InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }

        if (ClientProxy.openInterface.isPressed()) {
            FACTORY.openClient();
            // ClientGUI.open(buildUI());
            Ae2DebugTools.LOG.info("Opening GUI");
        }
    }
}
