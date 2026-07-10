package com.JDR11.ae2debugtools.common;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.JDR11.ae2debugtools.Ae2DebugTools;
import com.JDR11.ae2debugtools.Config;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.util.DimensionalCoord;

public class NetworkScanner {

    private static final ForgeDirection[] DIRECTIONS = new ForgeDirection[] { ForgeDirection.UNKNOWN,
        ForgeDirection.DOWN, ForgeDirection.UP, ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.EAST,
        ForgeDirection.WEST, };

    public static class ScanResult {

        public final List<ChunkCoordinates> results;
        public final Map<Integer, Integer> otherDimensionCounts;

        public ScanResult(List<ChunkCoordinates> results, Map<Integer, Integer> otherDimensionCounts) {
            this.results = results;
            this.otherDimensionCounts = otherDimensionCounts;
        }
    }

    public static ScanResult scan(World world, EntityPlayer player, String filter, ItemStack itemFilter, int radius) {
        List<DimensionalCoord> matches = new ArrayList<>();
        if (world != null) {
            String lowerCaseFilter = filter.toLowerCase()
                .trim();
            Set<IGrid> visitedGrids = new HashSet<>();

            int px = (int) player.posX;
            int py = (int) player.posY;
            int pz = (int) player.posZ;

            int checked = 0;
            int gridHostCount = 0;
            int nodeFoundCount = 0;
            int newGridCount = 0;

            for (int x = px - radius; x <= px + radius; x++) {
                for (int y = py - radius; y <= py + radius; y++) {
                    for (int z = pz - radius; z <= pz + radius; z++) {
                        TileEntity tileEntity = world.getTileEntity(x, y, z);
                        if (tileEntity == null) continue;
                        checked++;

                        if (!(tileEntity instanceof IGridHost)) continue;
                        gridHostCount++;

                        IGridNode entryNode = findNode((IGridHost) tileEntity);
                        if (entryNode == null) continue;
                        nodeFoundCount++;

                        IGrid grid = entryNode.getGrid();
                        if (grid == null || !visitedGrids.add(grid)) continue;
                        newGridCount++;

                        for (IGridNode node : grid.getNodes()) {
                            DimensionalCoord loc = node.getGridBlock()
                                .getLocation();
                            ItemStack representation = node.getGridBlock()
                                .getMachineRepresentation();

                            boolean textMatch = lowerCaseFilter.isEmpty()
                                || (representation != null && representation.getDisplayName()
                                    .toLowerCase()
                                    .contains(lowerCaseFilter));
                            boolean itemMatch = itemFilter == null
                                || (representation != null && itemFilter.isItemEqual(representation));

                            // Ae2DebugTools.LOG.info("Found node {}",
                            // node.getGridBlock().getMachineRepresentation().getDisplayName());
                            if (textMatch && itemMatch) {
                                matches.add(loc);
                            }
                        }
                        // Ae2DebugTools.LOG.info("[server] {}", tileEntity.getGridNode().);
                    }
                }
            }

            Ae2DebugTools.LOG.info(
                "[server] checked={} gridHosts={} nodesFound={} newGrids={} results={}",
                checked,
                gridHostCount,
                nodeFoundCount,
                newGridCount,
                matches.size());
        }
        return buildScanResult(matches, world, player);
    };

    public static ScanResult buildScanResult(List<DimensionalCoord> matches, World world, EntityPlayer player) {
        int currentDim = world != null ? world.provider.dimensionId : player.dimension;

        double px = (double) player.posX;
        double py = (double) player.posY;
        double pz = (double) player.posZ;

        List<DimensionalCoord> sameDimension = new ArrayList<>();
        Map<Integer, Integer> otherDimensionCounts = new HashMap<>();

        for (DimensionalCoord coord : matches) {
            if (coord.getDimension() == currentDim) {
                sameDimension.add(coord);
            } else {
                otherDimensionCounts.merge(coord.getDimension(), 1, Integer::sum);
            }
        }

        sameDimension.sort(Comparator.comparingDouble(loc -> distanceSq(loc, px, py, pz)));

        List<ChunkCoordinates> results = new ArrayList<>();
        int cap = Math.min(Config.maxResults, sameDimension.size());
        for (int i = 0; i < cap; i++) {
            DimensionalCoord coord = sameDimension.get(i);
            results.add(new ChunkCoordinates(coord.x, coord.y, coord.z));
        }

        return new ScanResult(results, otherDimensionCounts);
    }

    private static double distanceSq(DimensionalCoord loc, double px, double py, double pz) {
        double dx = (loc.x + 0.5) - px;
        double dy = (loc.y + 0.5) - py;
        double dz = (loc.z + 0.5) - pz;
        return dx * dx + dy * dy + dz * dz;
    }

    public static IGridNode findNode(IGridHost host) {
        for (ForgeDirection direction : DIRECTIONS) {
            try {
                IGridNode node = host.getGridNode(direction);
                if (node != null) return node;
            } catch (Exception ignored) {}
        }
        return null;
    };
}
