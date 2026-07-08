package com.JDR11.ae2debugtools.client.gui;

import static com.JDR11.ae2debugtools.Config.openInterface;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

import com.JDR11.ae2debugtools.Ae2DebugTools;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.ClientGUI;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;

public class GuiHandler {

    @SubscribeEvent
    public void openGui(InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }

        if (openInterface.isPressed()) {
            ClientGUI.open(createGui());
            Ae2DebugTools.LOG.info("Opening GUI");
            System.out.println("Opening GUI");
            mc.thePlayer.addChatMessage(new ChatComponentText("Opening GUI"));
        }
    }

    public static ModularScreen createGui() {
        ModularPanel panel = ModularPanel.defaultPanel("tutorial");
        panel.child(
            IKey.str("My first screen")
                .asWidget()
                .top(7)
                .left(7));

        return new ModularScreen(panel);
    }
}
