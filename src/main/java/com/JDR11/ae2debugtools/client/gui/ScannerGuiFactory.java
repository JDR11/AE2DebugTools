package com.JDR11.ae2debugtools.client.gui;

import com.cleanroommc.modularui.factory.GuiFactories;
import com.cleanroommc.modularui.factory.SimpleGuiFactory;

public class ScannerGuiFactory {

    public static final SimpleGuiFactory FACTORY = GuiFactories.createSimple("ae2debugtools:scanner", new ScannerGui());
}
