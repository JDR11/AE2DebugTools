package com.JDR11.ae2debugtools.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.util.ForgeDirection;

import com.JDR11.ae2debugtools.Ae2DebugTools;
import com.JDR11.ae2debugtools.Config;
import com.google.common.collect.ImmutableSetMultimap;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.util.DimensionalCoord;

public class NetworkScanner {

    private static final ForgeDirection[] DIRECTIONS = new ForgeDirection[] { ForgeDirection.UNKNOWN,
        ForgeDirection.DOWN, ForgeDirection.UP, ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.EAST,
        ForgeDirection.WEST, };

    public static class ScanMatch {

        public final int x, y, z, dimension;
        public final String label;
        public final boolean nearUnloadedBorder;

        public ScanMatch(int x, int y, int z, int dimension, String label, boolean nearUnloadedBorder) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.dimension = dimension;
            this.label = label;
            this.nearUnloadedBorder = nearUnloadedBorder;
        }
    }

    /**
     * public static class ScanResult {
     *
     * public final List<ChunkCoordinates> results;
     * public final Map<Integer, Integer> otherDimensionCounts;
     *
     * public ScanResult(List<ChunkCoordinates> results, Map<Integer, Integer> otherDimensionCounts) {
     * this.results = results;
     * this.otherDimensionCounts = otherDimensionCounts;
     * }
     * }
     **/

    public static List<ScanMatch> scan(EntityPlayer player, String filter, ItemStack itemFilter, int radius) {
        List<ScanMatch> matches = new ArrayList<>();
        String lowerCaseFilter = filter.toLowerCase()
            .trim();
        Set<IGrid> visitedGrids = new HashSet<>();

        World playerWorld = player.worldObj;
        int pdim = player.dimension;

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
                    TileEntity tileEntity = playerWorld.getTileEntity(x, y, z);
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

                    collectMatchesFromGrid(grid, lowerCaseFilter, itemFilter, matches);

                    /**
                     * for (IGridNode node : grid.getNodes()) {
                     * DimensionalCoord loc = node.getGridBlock()
                     * .getLocation();
                     * ItemStack representation = node.getGridBlock()
                     * .getMachineRepresentation();
                     * 
                     * boolean textMatch = lowerCaseFilter.isEmpty()
                     * || (representation != null && representation.getDisplayName()
                     * .toLowerCase()
                     * .contains(lowerCaseFilter));
                     * boolean itemMatch = itemFilter == null
                     * || (representation != null && itemFilter.isItemEqual(representation));
                     * 
                     * // Ae2DebugTools.LOG.info("Found node {}",
                     * // node.getGridBlock().getMachineRepresentation().getDisplayName());
                     * if (textMatch && itemMatch) {
                     * String label = representation != null ? representation.getDisplayName() : "Unknown";
                     * matches.add(new ScanMatch(loc.x, loc.y, loc.z, loc.getDimension(), label));
                     * }
                     * }
                     **/
                    // Ae2DebugTools.LOG.info("[server] {}", tileEntity.getGridNode().);
                }
            }
        }

        for (WorldServer worldServer : DimensionManager.getWorlds()) {
            if (worldServer.provider.dimensionId == pdim) continue;

            ImmutableSetMultimap<ChunkCoordIntPair, ForgeChunkManager.Ticket> persistentChunks = ForgeChunkManager
                .getPersistentChunksFor(worldServer);

            for (ChunkCoordIntPair chunkCoordIntPair : persistentChunks.keySet()) {
                Chunk chunk = worldServer
                    .getChunkFromChunkCoords(chunkCoordIntPair.chunkXPos, chunkCoordIntPair.chunkZPos);
                if (chunk == null) continue;

                for (Object o : chunk.chunkTileEntityMap.values()) {
                    TileEntity tileEntity = (TileEntity) o;
                    checked++;

                    if (!(tileEntity instanceof IGridHost)) continue;
                    gridHostCount++;

                    IGridNode entryNode = findNode((IGridHost) tileEntity);
                    if (entryNode == null) continue;
                    nodeFoundCount++;

                    IGrid grid = entryNode.getGrid();
                    if (grid == null || !visitedGrids.add(grid)) continue;
                    newGridCount++;

                    collectMatchesFromGrid(grid, lowerCaseFilter, itemFilter, matches);
                }
            }
        }

        if (Config.verboseLogging) {

            Ae2DebugTools.LOG.info(
                "[server] checked={} gridHosts={} nodesFound={} newGrids={} results={}",
                checked,
                gridHostCount,
                nodeFoundCount,
                newGridCount,
                matches.size());
        }

        return matches;
    };

    /**
     * public static ScanResult buildScanResult(List<DimensionalCoord> matches, World world, EntityPlayer player) {
     * int currentDim = world != null ? world.provider.dimensionId : player.dimension;
     *
     * double px = player.posX;
     * double py = player.posY;
     * double pz = player.posZ;
     *
     * List<DimensionalCoord> sameDimension = new ArrayList<>();
     * Map<Integer, Integer> otherDimensionCounts = new HashMap<>();
     *
     * for (DimensionalCoord coord : matches) {
     * if (coord.getDimension() == currentDim) {
     * sameDimension.add(coord);
     * } else {
     * otherDimensionCounts.merge(coord.getDimension(), 1, Integer::sum);
     * }
     * }
     *
     * sameDimension.sort(Comparator.comparingDouble(loc -> distanceSq(loc, px, py, pz)));
     *
     * List<ChunkCoordinates> results = new ArrayList<>();
     * int cap = Math.min(Config.maxResults, sameDimension.size());
     * for (int i = 0; i < cap; i++) {
     * DimensionalCoord coord = sameDimension.get(i);
     * results.add(new ChunkCoordinates(coord.x, coord.y, coord.z));
     * }
     *
     * return new ScanResult(results, otherDimensionCounts);
     * }
     *
     * private static double distanceSq(DimensionalCoord loc, double px, double py, double pz) {
     * double dx = (loc.x + 0.5) - px;
     * double dy = (loc.y + 0.5) - py;
     * double dz = (loc.z + 0.5) - pz;
     * return dx * dx + dy * dy + dz * dz;
     * }
     **/

    public static void collectMatchesFromGrid(IGrid grid, String lowerCaseFilter, ItemStack itemFilter,
        List<ScanMatch> matches) {
        for (IGridNode node : grid.getNodes()) {
            DimensionalCoord loc = node.getGridBlock()
                .getLocation();
            ItemStack representation = node.getGridBlock()
                .getMachineRepresentation();

            boolean textMatch = lowerCaseFilter.isEmpty() || (representation != null && representation.getDisplayName()
                .toLowerCase()
                .contains(lowerCaseFilter));
            boolean itemMatch = itemFilter == null
                || (representation != null && itemFilter.isItemEqual(representation));

            // Ae2DebugTools.LOG.info("Found node {}",
            // node.getGridBlock().getMachineRepresentation().getDisplayName());
            if (textMatch && itemMatch) {
                String label = representation != null ? representation.getDisplayName() : "Unknown";
                World nodeWorld = loc.getWorld();
                boolean nearBorder = nodeWorld != null && isNearUnloadedBorder(nodeWorld, loc.x, loc.z, 2);
                matches.add(new ScanMatch(loc.x, loc.y, loc.z, loc.getDimension(), label, nearBorder));
            }
        }
    }

    private static boolean isNearUnloadedBorder(World world, int x, int z, int threshold) {
        if (!(world.getChunkProvider() instanceof ChunkProviderServer)) return false;
        ChunkProviderServer provider = (ChunkProviderServer) world.getChunkProvider();

        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        int localX = x & 15;
        int localZ = z & 15;

        if (localX < threshold && !provider.chunkExists(chunkX - 1, chunkZ)) return true;
        if (localX >= 16 - threshold && !provider.chunkExists(chunkX + 1, chunkZ)) return true;
        if (localZ < threshold && !provider.chunkExists(chunkX, chunkZ - 1)) return true;
        if (localZ >= 16 - threshold && !provider.chunkExists(chunkX, chunkZ + 1)) return true;

        return false;
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
