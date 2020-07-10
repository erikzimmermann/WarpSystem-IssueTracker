package de.codingair.warpsystem.spigot.features.portals.utils;

import de.codingair.codingapi.tools.Location;
import de.codingair.codingapi.tools.io.utils.DataWriter;
import de.codingair.codingapi.tools.io.utils.Serializable;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BlockHierarchy implements Serializable {
    private final List<PortalBlock> blocks = new ArrayList<>();
    private final Bounds bounds;
    //width=x-length; height=z-length
    private int width = 1;
    private final String world;

    public BlockHierarchy(PortalBlock init) {
        this.blocks.add(init);
        this.bounds = new Bounds(new Position(init.getLocation().getBlockX(), init.getLocation().getBlockY(), init.getLocation().getBlockZ()), new Position(init.getLocation().getBlockX(), init.getLocation().getBlockY(), init.getLocation().getBlockZ()), init.getType());
        this.world = init.getLocation().getWorldName();
    }

    public BlockHierarchy(String world) {
        this.world = world;
        this.bounds = new Bounds();
    }

    @Override
    public boolean read(DataWriter d) throws Exception {
        this.bounds.read(d);
        this.blocks.clear();

        for(int x = bounds.getMin().getX(); x <= bounds.getMax().getX(); x++) {
            for(int y = bounds.getMin().getY(); y <= bounds.getMax().getY(); y++) {
                for(int z = bounds.getMin().getZ(); z <= bounds.getMax().getZ(); z++) {
                    this.blocks.add(new PortalBlock(new Location(world, x, y, z), bounds.type));
                }
            }
        }

        return true;
    }

    @Override
    public void write(DataWriter d) {
        this.bounds.write(d);
    }

    @Override
    public void destroy() {
        this.blocks.clear();
    }

    public boolean isEmpty() {
        return this.blocks.isEmpty();
    }

    @Override
    public String toString() {
        return "BlockHierarchy{" +
                "blocks=" + blocks +
                ", bounds=" + bounds +
                ", width=" + width +
                '}';
    }

    public int getWidth() {
        return width;
    }

    public BlockHierarchy prepend(int x, PortalBlock block) {
        bounds.min.setX(x);
        blocks.add(0, block);
        width++;
        return this;
    }

    public BlockHierarchy append(int x, PortalBlock block) {
        bounds.max.setX(x);
        blocks.add(block);
        width++;
        return this;
    }

    public BlockHierarchy append(BlockHierarchy hierarchy) {
        bounds.max.apply(hierarchy.getMax());
        blocks.addAll(hierarchy.blocks);

        if(bounds.min.getY() == hierarchy.getMin().getY() && bounds.min.getZ() == hierarchy.getMin().getZ()) width += hierarchy.width;

        hierarchy.blocks.clear();
        return this;
    }

    public List<PortalBlock> getBlocks() {
        return blocks;
    }

    public Position getMin() {
        return bounds.min;
    }

    public Position getMax() {
        return bounds.max;
    }

    public Bounds getBounds() {
        return bounds;
    }

    public Bounds getBounds(boolean byType) {
        return byType ? bounds : new Bounds(bounds.min, bounds.max, null);
    }

    public BlockType getType() {
        return bounds.type;
    }

    public static class Bounds implements Serializable {
        private Position min;
        private Position max;
        private BlockType type;

        public Bounds(Position min, Position max, BlockType type) {
            this.min = min;
            this.max = max;
            this.type = type;
        }

        public Bounds() {
        }

        @Override
        public boolean read(DataWriter d) throws Exception {
            this.min = d.getSerializable("min", new Position());
            this.max = d.getSerializable("max", new Position());
            this.type = BlockType.values()[d.getInteger("type")];
            return true;
        }

        @Override
        public void write(DataWriter d) {
            d.put("min", min);
            d.put("max", max);
            d.put("type", this.type == null ? null : this.type.ordinal());
        }

        @Override
        public void destroy() {

        }

        public Bounds clone() {
            return new Bounds(new Position(min), new Position(max), type);
        }

        @Override
        public boolean equals(Object o) {
            if(this == o) return true;
            if(o == null || getClass() != o.getClass()) return false;
            Bounds bounds = (Bounds) o;
            return min.equals(bounds.min) &&
                    max.equals(bounds.max) &&
                    type == bounds.type;
        }

        @Override
        public int hashCode() {
            return Objects.hash(min, max, type);
        }

        @Override
        public String toString() {
            return "Bounds{" +
                    "min=" + min +
                    ", max=" + max +
                    ", type=" + type +
                    '}';
        }

        public void merge(Bounds b) {
            min.x = Math.min(min.x, b.min.x);
            min.y = Math.min(min.y, b.min.y);
            min.z = Math.min(min.z, b.min.z);
            max.x = Math.max(max.x, b.max.x);
            max.y = Math.max(max.y, b.max.y);
            max.z = Math.max(max.z, b.max.z);
        }

        public Position getMin() {
            return min;
        }

        public Position getMax() {
            return max;
        }
    }

    public static class Position implements Serializable {
        private int x, y, z;

        public Position() {
        }

        public Position(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Position(Position position) {
            this.x = position.x;
            this.y = position.y;
            this.z = position.z;
        }

        @Override
        public boolean read(DataWriter d) throws Exception {
            this.x = d.getInteger("x");
            this.y = d.getInteger("y");
            this.z = d.getInteger("z");
            return true;
        }

        @Override
        public void write(DataWriter d) {
            d.put("x", x);
            d.put("y", y);
            d.put("z", z);
        }

        @Override
        public void destroy() {
            x = 0;
            y = 0;
            z = 0;
        }

        public Location toLocation(World w) {
            return new Location(w, x, y, z);
        }

        public Position apply(Position p) {
            this.x = p.x;
            this.y = p.y;
            this.z = p.z;
            return this;
        }

        public int getX() {
            return x;
        }

        public Position setX(int x) {
            this.x = x;
            return this;
        }

        public int getY() {
            return y;
        }

        public Position setY(int y) {
            this.y = y;
            return this;
        }

        public int getZ() {
            return z;
        }

        public Position setZ(int z) {
            this.z = z;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if(this == o) return true;
            if(o == null || getClass() != o.getClass()) return false;
            Position position = (Position) o;
            return x == position.x &&
                    y == position.y &&
                    z == position.z;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, z);
        }

        @Override
        public String toString() {
            return "Position{" +
                    "x=" + x +
                    ", y=" + y +
                    ", z=" + z +
                    '}';
        }
    }
}
