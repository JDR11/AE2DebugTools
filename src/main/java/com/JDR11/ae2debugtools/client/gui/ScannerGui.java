package com.JDR11.ae2debugtools.client.gui;

import com.JDR11.ae2debugtools.Ae2DebugTools;
import com.JDR11.ae2debugtools.common.network.PacketHandler;
import com.JDR11.ae2debugtools.common.network.PacketScanRequest;
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

public class ScannerGui implements IGuiHolder<GuiData> {

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
                    .topRel(0.03f)
                    .leftRel(0.04f))
            .child(
                Flow.row()
                    .topRel(0.12f)
                    .bottomRelOffset(0f, 88)
                    .leftRel(0.11f)
                    .rightRel(0f)
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
                                Ae2DebugTools.LOG
                                    .info("Scan button clicked, sending request for filter '{}'", scanTarget);
                                PacketHandler.INSTANCE.sendToServer(new PacketScanRequest(scanTarget));
                                return true;
                            })));

        return panel;
    }

}
