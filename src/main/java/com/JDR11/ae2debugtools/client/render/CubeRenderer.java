package com.JDR11.ae2debugtools.client.render;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

public class CubeRenderer {

    public static final CubeRenderer INSTANCE = new CubeRenderer();
    private List<CubeRendererTarget> cubeRendererTargets = new ArrayList<>();
    private long currentTime;

    public void draw(List<CubeRendererTarget> cubeRendererTargets) {
        this.cubeRendererTargets = cubeRendererTargets;
        this.currentTime = System.currentTimeMillis();
    }

    public void tryToRender(RenderWorldLastEvent event) {
        long timeAlive = System.currentTimeMillis() - currentTime;
        long totalLife = 3000;
        long fadeDuration = 1000;
        long solidDuration = totalLife - fadeDuration;

        if (this.cubeRendererTargets.isEmpty() || timeAlive >= totalLife) {
            return;
        }

        // Ae2DebugTools.LOG.info("[render] Rendering {} targets, timeAlive={}", this.cubeRendererTargets.size(),
        // timeAlive);

        int alphaByte = getAlphaByte(timeAlive, solidDuration, fadeDuration);

        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        double playerX = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.partialTicks;
        double playerY = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.partialTicks;
        double playerZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.partialTicks;

        GL11.glPushMatrix();
        GL11.glTranslated(-playerX, -playerY, -playerZ);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glBegin(GL11.GL_LINES);
        for (CubeRendererTarget target : this.cubeRendererTargets) {
            Color c = target.getColour();
            int a = alphaByte;

            GL11.glColor4d(c.getRed() / 255.0, c.getGreen() / 255.0, c.getBlue() / 255.0, a / 255.0);
            RenderCube(target.getBlockPos());
        }

        GL11.glEnd();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopMatrix();
    }

    private static int getAlphaByte(long timeAlive, long solidDuration, long fadeDuration) {
        float alpha = 1.0f;

        if (fadeDuration > 0) {
            if (timeAlive > solidDuration) {
                long remainingTime = timeAlive - fadeDuration;
                alpha = 1.0f - ((float) remainingTime / (float) fadeDuration);
            }
        }

        if (alpha < 0.0f) {
            alpha = 0.0f;
        }

        if (alpha > 1.0f) {
            alpha = 1.0f;
        }

        return (int) (alpha * 255);
    }

    private void RenderCube(BlockPos pos) {

        // Ae2DebugTools.LOG.info("[render] Drawing Cube at ({}, {}, {})", pos.x, pos.y, pos.z);

        float x = pos.getX();
        float y = pos.getY();
        float z = pos.getZ();
        float o = -0.01f;

        float minX = x - o;
        float maxX = x + 1 + o;
        float minY = y - o;
        float maxY = y + 1 + o;
        float minZ = z - o;
        float maxZ = z + 1 + o;

        // Bottom Square
        GL11.glVertex3f(minX, minY, minZ);
        GL11.glVertex3f(maxX, minY, minZ);
        GL11.glVertex3f(maxX, minY, minZ);
        GL11.glVertex3f(maxX, minY, maxZ);
        GL11.glVertex3f(maxX, minY, maxZ);
        GL11.glVertex3f(minX, minY, maxZ);
        GL11.glVertex3f(minX, minY, maxZ);
        GL11.glVertex3f(minX, minY, minZ);

        // Top Square
        GL11.glVertex3f(minX, maxY, minZ);
        GL11.glVertex3f(maxX, maxY, minZ);
        GL11.glVertex3f(maxX, maxY, minZ);
        GL11.glVertex3f(maxX, maxY, maxZ);
        GL11.glVertex3f(maxX, maxY, maxZ);
        GL11.glVertex3f(minX, maxY, maxZ);
        GL11.glVertex3f(minX, maxY, maxZ);
        GL11.glVertex3f(minX, maxY, minZ);

        // Vertical Lines
        GL11.glVertex3f(minX, minY, minZ);
        GL11.glVertex3f(minX, maxY, minZ);
        GL11.glVertex3f(maxX, minY, minZ);
        GL11.glVertex3f(maxX, maxY, minZ);
        GL11.glVertex3f(maxX, minY, maxZ);
        GL11.glVertex3f(maxX, maxY, maxZ);
        GL11.glVertex3f(minX, minY, maxZ);
        GL11.glVertex3f(minX, maxY, maxZ);
    }

}
