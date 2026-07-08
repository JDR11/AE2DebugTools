package com.JDR11.ae2debugtools.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.util.DimensionalCoord;

public class NetworkScanner {

    private static final ForgeDirection[] DIRECTIONS = new ForgeDirection[] { ForgeDirection.UNKNOWN,
        ForgeDirection.DOWN, ForgeDirection.UP, ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.EAST,
        ForgeDirection.WEST, };

    public static List<ChunkCoordinates> scan(String filter) {
        List<ChunkCoordinates> results = new ArrayList<>();
        Minecraft mc = Minecraft.getMinecraft();
        World world = mc.theWorld;
        if (world == null) return results;

        String lowerCaseFilter = filter.toLowerCase()
            .trim();

        Set<IGrid> visitedGrids = new HashSet<>();
        Set<Long> visitedCoords = new HashSet<>();

        List<TileEntity> tileEntities = new ArrayList<>(world.loadedTileEntityList);

        for (TileEntity tileEntity : tileEntities) {
            if (!(tileEntity instanceof IGridHost)) continue;

            IGridNode entryNode = findNode((IGridHost) tileEntity);
            if (entryNode == null) continue;

            IGrid grid = entryNode.getGrid();
            if (grid == null || !visitedGrids.add(grid)) continue;

            for (IGridNode node : grid.getMachines(IGridHost.class)) {
                DimensionalCoord loc;
                try {
                    loc = node.getGridBlock()
                        .getLocation();
                } catch (Exception e) {
                    continue;
                }
                if (loc == null) continue;

                int x = loc.x, y = loc.y, z = loc.z;

                long key = ((long) (x + 30_000_000) << 40) | ((long) (y + 256) << 20) | ((long) (z + 30_000_000));
                if (!visitedCoords.add(key)) continue;

                if (matchesFilter(world, x, y, z, lowerCaseFilter)) {
                    results.add(new ChunkCoordinates(x, y, z));
                }
            }
        }

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
