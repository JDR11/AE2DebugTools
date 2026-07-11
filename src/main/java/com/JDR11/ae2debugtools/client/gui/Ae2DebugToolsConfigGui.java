package com.JDR11.ae2debugtools.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;

import com.JDR11.ae2debugtools.Ae2DebugTools;
import com.JDR11.ae2debugtools.Config;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;

public class Ae2DebugToolsConfigGui extends GuiConfig {

    public Ae2DebugToolsConfigGui(GuiScreen parentScreen) {
        super(parentScreen, getConfigElements(), Ae2DebugTools.MODID, false, false, "AE2DebugTools Configuration");
    }

    private static List<IConfigElement> getConfigElements() {
        List<IConfigElement> list = new ArrayList<>();
        for (String category : Config.getConfig()
            .getCategoryNames()) {
            list.add(
                new ConfigElement(
                    Config.getConfig()
                        .getCategory(category)));
        }
        return list;
    }
}
