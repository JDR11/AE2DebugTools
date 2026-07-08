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

    public static String greeting = "Hello World";

    public static void synchronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);

        greeting = configuration.getString("greeting", Configuration.CATEGORY_GENERAL, greeting, "How shall I greet?");

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
