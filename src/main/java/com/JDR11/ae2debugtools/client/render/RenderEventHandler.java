package com.JDR11.ae2debugtools.client.render;

import net.minecraftforge.client.event.RenderWorldLastEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class RenderEventHandler {

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        CubeRenderer.INSTANCE.tryToRender(event);
    }
}
