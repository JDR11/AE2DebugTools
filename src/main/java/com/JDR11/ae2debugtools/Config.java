package com.JDR11.ae2debugtools;

import java.io.File;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.config.Configuration;

import org.lwjgl.input.Keyboard;

public class Config {

    public static final KeyBinding openInterface = new KeyBinding(
        "key.ae2debugtools.open_gui",
        Keyboard.KEY_NONE,
        "key.categories.ae2debugtools");

    public static void synchronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
