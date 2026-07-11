package com.JDR11.ae2debugtools;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.input.Keyboard;

import com.JDR11.ae2debugtools.client.ConfigChangeHandler;
import com.JDR11.ae2debugtools.client.gui.GuiHandler;
import com.JDR11.ae2debugtools.client.render.RenderEventHandler;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    public static final KeyBinding openInterface = new KeyBinding("Open GUI", Keyboard.KEY_NONE, "AE2DebugTools");

    // Override CommonProxy methods here, if you want a different behaviour on the client (e.g. registering renders).
    // Don't forget to call the super methods as well.
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        FMLCommonHandler.instance()
            .bus()
            .register(new GuiHandler());
        MinecraftForge.EVENT_BUS.register(new RenderEventHandler());
        FMLCommonHandler.instance()
            .bus()
            .register(new ConfigChangeHandler());
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        ClientRegistry.registerKeyBinding(openInterface);
    }

}
