package com.JDR11.ae2debugtools.client.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;

import com.JDR11.ae2debugtools.Ae2DebugTools;
import com.JDR11.ae2debugtools.client.NetworkScanner;
import com.JDR11.ae2debugtools.client.render.CubeRendererTarget;
import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.GuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.item.IItemHandlerModifiable;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.cleanroommc.modularui.value.StringValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.PhantomItemSlot;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

public class ScannerGui implements IGuiHolder<GuiData> {

    Minecraft mc = Minecraft.getMinecraft();

    private final IItemHandlerModifiable itemHandler = new ItemStackHandler(1);
    private String scanTarget = "";

    public ModularPanel buildUI(GuiData guiData, PanelSyncManager syncManager, UISettings uiSettings) {

        syncManager.registerSlotGroup("item_inv", 1);

        ModularPanel panel = ModularPanel.defaultPanel("ae2debugtools:scanner")
            .sizeRel(0.25f, 0.35f);
        panel.bindPlayerInventory()
            .child(
                IKey.str("Network Scanner")
                    .asWidget()
                    .top(7)
                    .left(7))
            .child(
                Flow.row()
                    .top(20)
                    .bottom(88)
                    .left(15)
                    .right(0)
                    .mainAxisAlignment(Alignment.MainAxis.START)
                    .crossAxisAlignment(Alignment.CrossAxis.CENTER)
                    .childPadding(6)
                    .child(new PhantomItemSlot().slot(new ModularSlot(itemHandler, 0).slotGroup("item_inv")))
                    .child(
                        new TextFieldWidget()
                            .value(new StringValue.Dynamic(() -> this.scanTarget, val -> this.scanTarget = val))
                            .widthRel(0.4f))
                    .child(
                        new ButtonWidget<>().size(60, 16)
                            .child(
                                IKey.str("Scan")
                                    .asWidget()
                                    .center())
                            .onMouseTapped(mouseButton -> {
                                Ae2DebugTools.LOG.info("Scan button clicked, target filter: '{}'", scanTarget);

                                List<ChunkCoordinates> results = NetworkScanner.scan(scanTarget);
                                Ae2DebugTools.LOG.info("Scan found {} matching block(s)", results.size());

                                mc.thePlayer.addChatComponentMessage(
                                    new ChatComponentText(
                                        "Scan found " + results.size() + " block(s) matching \"" + scanTarget + "\""));

                                List<CubeRendererTarget> targets = new ArrayList<>();
                                for (ChunkCoordinates coords : results) {
                                    targets.add(
                                        new CubeRendererTarget(
                                            new BlockPos(coords.posX, coords.posY, coords.posZ),
                                            Color.CYAN));
                                }

                                return true;
                            })));

        return panel;
    }

}
