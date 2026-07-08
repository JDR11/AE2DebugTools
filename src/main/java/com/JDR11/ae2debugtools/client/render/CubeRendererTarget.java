package com.JDR11.ae2debugtools.client.render;

import java.awt.Color;
import java.util.Objects;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

public class CubeRendererTarget {

    private final BlockPos blockPos;
    private final Color colour;

    public CubeRendererTarget(BlockPos blockPos, Color colour) {
        this.blockPos = blockPos;
        this.colour = colour;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public Color getColour() {
        return colour;
    }

    @Override
    public String toString() {
        return "BlockPos: [" + blockPos.toString() + "] " + "Colour: [" + colour.toString() + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof CubeRendererTarget)) {
            return false;
        }

        CubeRendererTarget cubeRendererTarget = (CubeRendererTarget) o;

        return Objects.equals(blockPos, cubeRendererTarget.blockPos)
            && Objects.equals(colour, cubeRendererTarget.colour);
    }

    @Override
    public int hashCode() {
        return Objects.hash(blockPos, colour);
    }
}
