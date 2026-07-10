package com.JDR11.ae2debugtools;

import java.io.File;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.config.Configuration;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Config {

    @SideOnly(Side.CLIENT)
    public static final KeyBinding openInterface = new KeyBinding(
        "key.ae2debugtools.open_gui",
        Keyboard.KEY_NONE,
        "key.categories.ae2debugtools");

    // Scanner
    public static int maxResults;
    public static int scanRadius;
    public static boolean verboseLogging;

    // CubeRenderer
    public static int cubeTotalLifeMillis;
    public static int cubeFadeDurationMillis;
    public static float cubeLineWidth;
    public static int cubeHighlightColour;

    public static void synchronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);

        try {
            configuration.load();

            maxResults = configuration.getInt(
                "maxResults",
                "scanner",
                64,
                1,
                2048,
                "Maximum number of blocks a single scan will return/highlight. Prevents huge networks from generating an excessive number of render targets in one scan.");

            scanRadius = configuration.getInt(
                "scanRadius",
                "scanner",
                32,
                4,
                128,
                "Radius (in blocks) around the player to search for AE2 network nodes when scanning.");

            verboseLogging = configuration.getBoolean(
                "verboseLogging",
                "scanner",
                false,
                "If true, logs detailed per-node scan diagnostics to the console. Useful for debugging, noisy otherwise.");

            cubeTotalLifeMillis = configuration.getInt(
                "totalLifeMillis",
                "renderer",
                3000,
                500,
                60000,
                "How long (in milliseconds) a highlighted cube stays visible before disappearing.");

            cubeFadeDurationMillis = configuration.getInt(
                "fadeDurationMillis",
                "renderer",
                1000,
                0,
                60000,
                "How long (in milliseconds), at the end of a cube's life, it takes to fade out. Set to 0 to disable fading.");

            cubeLineWidth = configuration.getFloat(
                "lineWidth",
                "renderer",
                2.0f,
                0.5f,
                10.0f,
                "Line thickness of the rendered highlight cube outlines.");

            cubeHighlightColour = configuration.getInt(
                "highlightColor",
                "renderer",
                0x00FFFF,
                0x000000,
                0xFFFFFF,
                "RGB hex color (as a decimal integer, e.g. 65535 = 0x00FFFF = cyan) used to highlight scanned blocks.");
        } finally {
            if (configuration.hasChanged()) {
                configuration.save();
            }
        }
    }
}
