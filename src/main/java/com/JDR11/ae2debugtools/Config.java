package com.JDR11.ae2debugtools;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class Config {

    private static Configuration config;

    // Scanner
    public static int maxResults;
    public static int scanRadius;
    public static boolean verboseLogging;
    public static boolean writeReportFile;

    // CubeRenderer
    public static int cubeTotalLifeMillis;
    public static int cubeFadeDurationMillis;
    public static float cubeLineWidth;
    public static int cubeHighlightColour;
    public static int cubeBorderWarningColour;

    public static void synchronizeConfiguration(File configFile) {
        config = new Configuration(configFile);
        reloadFromConfig();
    }

    public static void reloadFromConfig() {

        try {
            config.load();

            maxResults = config.getInt(
                "maxResults",
                "scanner",
                64,
                1,
                4096,
                "Maximum number of blocks a single scan will return/highlight. Prevents huge networks from generating an excessive number of render targets in one scan.");

            scanRadius = config.getInt(
                "scanRadius",
                "scanner",
                32,
                4,
                128,
                "Radius (in blocks) around the player to search for AE2 network nodes when scanning.");

            verboseLogging = config.getBoolean(
                "verboseLogging",
                "scanner",
                false,
                "If true, logs detailed per-node scan diagnostics to the console. Useful for debugging, noisy otherwise.");

            writeReportFile = config.getBoolean(
                "writeReportFile",
                "scanner",
                true,
                "If true, writes a full text report of every scan match (location + dimension) to <minecraft folder>/ae2debugtools/scan_reports/ each time you scan.");

            cubeTotalLifeMillis = config.getInt(
                "totalLifeMillis",
                "renderer",
                3000,
                500,
                6000000,
                "How long (in milliseconds) a highlighted cube stays visible before disappearing.");

            cubeFadeDurationMillis = config.getInt(
                "fadeDurationMillis",
                "renderer",
                1000,
                0,
                6000000,
                "How long (in milliseconds), at the end of a cube's life, it takes to fade out. Set to 0 to disable fading.");

            cubeLineWidth = config.getFloat(
                "lineWidth",
                "renderer",
                2.0f,
                0.5f,
                10.0f,
                "Line thickness of the rendered highlight cube outlines.");

            cubeHighlightColour = config.getInt(
                "highlightColor",
                "renderer",
                0x00FFFF,
                0x000000,
                0xFFFFFF,
                "RGB hex color (as a decimal integer, e.g. 65535 = 0x00FFFF = cyan) used to highlight scanned blocks.");

            cubeBorderWarningColour = config.getInt(
                "borderWarningColour",
                "renderer",
                0xFFA500,
                0x000000,
                0xFFFFFF,
                "RGB hex color (decimal) used to highlight matches near the edge of loaded chunks, where the network may extend further than we can currently see.");

        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }

    public static Configuration getConfig() {
        return config;
    }

    public static void setWriteReportFile(boolean value) {
        writeReportFile = value;

        config.get("scanner", "writeReportFile", true)
            .set(value);
        config.save();
    }
}
