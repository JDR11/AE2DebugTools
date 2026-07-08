package com.JDR11.ae2debugtools.client.gui;

import static com.JDR11.ae2debugtools.Config.openInterface;

import net.minecraft.client.Minecraft;

import com.JDR11.ae2debugtools.Ae2DebugTools;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.ClientGUI;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.utils.item.IItemHandler;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.cleanroommc.modularui.widgets.slot.PhantomItemSlot;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;

public class GuiHandler {

    private final IItemHandler invHandler = new ItemStackHandler(1);

    @SubscribeEvent
    public void openGui(InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }

        if (openInterface.isPressed()) {
            ClientGUI.open(createGui());
            Ae2DebugTools.LOG.info("Opening GUI");
        }
    }

    public static ModularScreen createGui() {

        ModularPanel panel = ModularPanel.defaultPanel("tutorial");
        panel.child(
            IKey.str("My first screen")
                .asWidget()
                .top(7)
                .left(7))
            .child(new PhantomItemSlot().slot(invHandler, 1));

        return new ModularScreen(panel);
    }
}
