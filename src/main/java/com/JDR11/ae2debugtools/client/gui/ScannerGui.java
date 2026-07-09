package com.JDR11.ae2debugtools.client.gui;

import com.JDR11.ae2debugtools.client.NetworkScanner;
import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.GuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.item.IItemHandlerModifiable;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.cleanroommc.modularui.value.StringValue;
import com.cleanroommc.modularui.value.sync.InteractionSyncHandler;
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

        InteractionSyncHandler scanButtonHandler = new InteractionSyncHandler()
            .setOnMouseTapped(mouseData -> { NetworkScanner.scan(scanTarget); });

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
                            .syncHandler(scanButtonHandler)));

        return panel;
    }

}
