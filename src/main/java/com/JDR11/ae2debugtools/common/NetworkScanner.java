package com.JDR11.ae2debugtools.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.JDR11.ae2debugtools.Ae2DebugTools;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.util.DimensionalCoord;

public class NetworkScanner {

    private static final ForgeDirection[] DIRECTIONS = new ForgeDirection[] { ForgeDirection.UNKNOWN,
        ForgeDirection.DOWN, ForgeDirection.UP, ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.EAST,
        ForgeDirection.WEST, };

    public static List<ChunkCoordinates> scan(World world, EntityPlayer player, String filter, int radius) {
        List<ChunkCoordinates> results = new ArrayList<>();
        if (world == null) return results;

        String lowerCaseFilter = filter.toLowerCase()
            .trim();

        Set<IGrid> visitedGrids = new HashSet<>();
        Set<Long> visitedCoords = new HashSet<>();

        int px = (int) player.posX;
        int py = (int) player.posY;
        int pz = (int) player.posZ;

        int checked = 0;
        /**
         * List<TileEntity> tileEntities = new ArrayList<>(world.loadedTileEntityList);
         * Ae2DebugTools.LOG.info("[server] {} loaded tile entities", tileEntities.size());
         * Ae2DebugTools.LOG.info(
         * "[server] Player {} at dim={} pos=({}, {}, {})",
         * player.getDisplayName(),
         * world.provider.dimensionId,
         * player.posX,
         * player.posY,
         * player.posZ);
         *
         * for (TileEntity tileEntity : tileEntities) {
         * Ae2DebugTools.LOG.info(
         * "[server] - {} at ({}, {}, {})",
         * tileEntity.getClass()
         * .getName(),
         * tileEntity.xCoord,
         * tileEntity.yCoord,
         * tileEntity.zCoord);
         * }
         **/

        int gridHostCount = 0;
        int nodeFoundCount = 0;
        int newGridCount = 0;
        int machineCount = 0;
        /**
         * for (TileEntity tileEntity : tileEntities) {
         *
         * if (!(tileEntity instanceof IGridHost)) continue;
         * gridHostCount++;
         *
         * IGridNode entryNode = findNode((IGridHost) tileEntity);
         * if (entryNode == null) continue;
         * nodeFoundCount++;
         *
         * IGrid grid = entryNode.getGrid();
         * if (grid == null || !visitedGrids.add(grid)) continue;
         * newGridCount++;
         *
         * for (IGridNode node : grid.getMachines(IGridHost.class)) {
         * machineCount++;
         * DimensionalCoord loc;
         * try {
         * loc = node.getGridBlock()
         * .getLocation();
         * } catch (Exception e) {
         * continue;
         * }
         * if (loc == null) continue;
         *
         * int x = loc.x, y = loc.y, z = loc.z;
         *
         * long key = ((long) (x + 30_000_000) << 40) | ((long) (y + 256) << 20) | ((long) (z + 30_000_000));
         * if (!visitedCoords.add(key)) continue;
         *
         * if (matchesFilter(world, x, y, z, lowerCaseFilter)) {
         * results.add(new ChunkCoordinates(x, y, z));
         * }
         * }
         * }
         **/

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

                    IGrid grid = entryNode.getGrid();
                    if (grid == null || !visitedGrids.add(grid)) continue;

                    for (IGridNode node : grid.getNodes()) {
                        DimensionalCoord loc = node.getGridBlock()
                            .getLocation();
                        Ae2DebugTools.LOG.info(
                            "Found node {}",
                            node.getGridBlock()
                                .getMachineRepresentation()
                                .getDisplayName());
                        if (node.getGridBlock()
                            .getMachineRepresentation()
                            .getDisplayName()
                            .toLowerCase()
                            .contains(lowerCaseFilter)) {
                            results.add(new ChunkCoordinates(loc.x, loc.y, loc.z));
                        }
                    }
                    // Ae2DebugTools.LOG.info("[server] {}", tileEntity.getGridNode().);

                    /**
                     * for (IGridNode node : grid.getMachines(IGridHost.class)) {
                     * machineCount++;
                     * DimensionalCoord loc;
                     * try {
                     * loc = node.getGridBlock()
                     * .getLocation();
                     * } catch (Exception e) {
                     * continue;
                     * }
                     * if (loc == null) continue;
                     * 
                     * int coordx = loc.x, coordy = loc.y, coordz = loc.z;
                     * 
                     * long key = ((long) (coordx + 30_000_000) << 40) | ((long) (coordy + 256) << 20)
                     * | ((long) (coordz + 30_000_000));
                     * if (!visitedCoords.add(key)) continue;
                     * 
                     * if (matchesFilter(world, coordx, coordy, coordz, lowerCaseFilter)) {
                     * results.add(new ChunkCoordinates(x, y, z));
                     * }
                     * 
                     * //Ae2DebugTools.LOG.info("[server] {}", node.getMachine().getClass().getSimpleName());
                     * }
                     **/
                }
            }
        }

        Ae2DebugTools.LOG.info(
            "[server] checked={} gridHosts={} nodesFound={} newGrids={} machinesWalked={} results={}",
            checked,
            gridHostCount,
            nodeFoundCount,
            newGridCount,
            machineCount,
            results.size());
        return results;
    };

    public static IGridNode findNode(IGridHost host) {
        for (ForgeDirection direction : DIRECTIONS) {
            try {
                IGridNode node = host.getGridNode(direction);
                if (node != null) return node;
            } catch (Exception ignored) {}
        }
        return null;
    };

    public static boolean matchesFilter(World world, int x, int y, int z, String lowerCaseFilter) {
        if (lowerCaseFilter.isEmpty()) return true;

        Block block = world.getBlock(x, y, z);
        if (block == null) return false;
        int meta = world.getBlockMetadata(x, y, z);

        try {
            if (block.getLocalizedName()
                .toLowerCase()
                .contains(lowerCaseFilter)) return true;
        } catch (Exception ignored) {}

        try {
            if (block.getUnlocalizedName()
                .toLowerCase()
                .contains(lowerCaseFilter)) return true;
        } catch (Exception ignored) {}

        try {
            if (new ItemStack(block, 1, meta).getDisplayName()
                .toLowerCase()
                .contains(lowerCaseFilter)) return true;
        } catch (Exception ignored) {}

        return false;
    };
}
