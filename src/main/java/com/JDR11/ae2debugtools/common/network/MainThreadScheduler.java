package com.JDR11.ae2debugtools.common.network;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class MainThreadScheduler {

    private static final Queue<Runnable> SERVER_TASKS = new ConcurrentLinkedQueue<>();
    private static final Queue<Runnable> CLIENT_TASKS = new ConcurrentLinkedQueue<>();

    public static void scheduleServerTask(Runnable task) {
        SERVER_TASKS.add(task);
    }

    public static void scheduleClientTask(Runnable task) {
        CLIENT_TASKS.add(task);
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Runnable task;
        while ((task = SERVER_TASKS.poll()) != null) task.run();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Runnable task;
        while ((task = CLIENT_TASKS.poll()) != null) task.run();
    }
}
