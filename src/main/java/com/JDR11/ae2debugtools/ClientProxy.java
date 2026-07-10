package com.JDR11.ae2debugtools;

import net.minecraftforge.common.MinecraftForge;

import com.JDR11.ae2debugtools.client.gui.GuiHandler;
import com.JDR11.ae2debugtools.client.render.RenderEventHandler;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    // Override CommonProxy methods here, if you want a different behaviour on the client (e.g. registering renders).
    // Don't forget to call the super methods as well.
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        // ClientRegistry.registerKeyBinding(new KeyBinding());
        ClientRegistry.registerKeyBinding(Config.openInterface);
        FMLCommonHandler.instance()
            .bus()
            .register(new GuiHandler());
        MinecraftForge.EVENT_BUS.register(new RenderEventHandler());
    }

    public void Init(FMLInitializationEvent event) {}
}
