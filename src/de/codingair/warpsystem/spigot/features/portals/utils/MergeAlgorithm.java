package de.codingair.warpsystem.spigot.features.portals.utils;

import java.util.HashMap;
import java.util.List;

public class MergeAlgorithm {
    //y -> x -> z
    private final List<BlockHierarchy> blocks;

    private MergeAlgorithm(List<BlockHierarchy> blocks) {
        this.blocks = blocks;
    }

    public static void merge(List<BlockHierarchy> out, List<PortalBlock> blocks) {
        MergeAlgorithm m = new MergeAlgorithm(out);
        m.addUnsorted(false, blocks);
    }

    public static void mergeByType(List<BlockHierarchy> out, List<PortalBlock> blocks) {
        MergeAlgorithm m = new MergeAlgorithm(out);
        m.addUnsorted(true, blocks);
    }

    private void addUnsorted(boolean byType, List<PortalBlock> blocks) {
        blocks.forEach(b -> addUnsorted(byType, b));
        mergeYZ(byType);
    }

    private void mergeYZ(boolean byType) {
        if(blocks.size() < 2) return;

        //prepare z merge
        HashMap<BlockHierarchy.Bounds, BlockHierarchy> data = new HashMap<>();
        blocks.forEach(b -> data.put(b.getBounds(byType), b));

        for(int i = 0; i < blocks.size(); i++) {
            BlockHierarchy current = blocks.get(i);
            if(current.isEmpty()) continue;

            BlockHierarchy.Position min = current.getMin();
            BlockHierarchy.Position max = current.getMax();
            int diff = (max.getZ() - min.getZ()) + 1;

            BlockHierarchy.Bounds nextPos = new BlockHierarchy.Bounds(new BlockHierarchy.Position(min.getX(), min.getY(), min.getZ() + diff),
                    new BlockHierarchy.Position(max.getX(), max.getY(), min.getZ() + diff), byType ? current.getType() : null);

            BlockHierarchy next = data.remove(nextPos);
            if(next != null) {
                current.append(next);
                i--;
            }
        }

        //prepare y merge
        data.clear();
        for(int i = 0; i < blocks.size(); i++) {
            BlockHierarchy b = blocks.get(i);

            if(b.isEmpty()) {
                blocks.remove(i);
                i--;
            } else data.put(b.getBounds(byType), b);
        }

        for(int i = 0; i < blocks.size(); i++) {
            BlockHierarchy current = blocks.get(i);
            if(current.isEmpty()) continue;

            BlockHierarchy.Position min = current.getMin();
            BlockHierarchy.Position max = current.getMax();
            int diff = (max.getY() - min.getY()) + 1;

            BlockHierarchy.Bounds nextPos = new BlockHierarchy.Bounds(new BlockHierarchy.Position(min.getX(), min.getY() + diff, min.getZ()),
                    new BlockHierarchy.Position(max.getX(), min.getY() + diff, max.getZ()), byType ? current.getType() : null);

            BlockHierarchy next = data.remove(nextPos);
            if(next != null) {
                current.append(next);
                i--;
            }
        }

        data.clear();
        for(int i = 0; i < blocks.size(); i++) {
            BlockHierarchy b = blocks.get(i);
            if(b.isEmpty()) {
                blocks.remove(i);
                i--;
            }
        }
    }

    private synchronized void addUnsorted(boolean byType, PortalBlock block) {
        if(blocks.isEmpty()) {
            blocks.add(new BlockHierarchy(block));
            return;
        }

        int x = block.getLocation().getBlockX();
        int z = block.getLocation().getBlockZ();
        int y = block.getLocation().getBlockY();

        for(int i = 0; i < blocks.size(); i++) {
            BlockHierarchy other = blocks.get(i);
            BlockHierarchy.Position min = other.getMin();
            int otherY = min.getY();

            if(!byType || other.getType() == block.getType()) {
                if(otherY == y) {
                    //go to z
                    int otherZ = min.getZ();

                    if(otherZ == z) {
                        //go to x
                        int otherX = min.getX();

                        if(otherX > x) {
                            //is in front of other?
                            if(otherX - 1 == x) {
                                //prepend
                                other.prepend(x, block);
                                return;
                            }

                            //insert at i
                            blocks.add(i, new BlockHierarchy(block));
                            return;
                        } else if(otherX + other.getWidth() == x) {
                            //merge
                            other.append(x, block);

                            //check following hierarchy
                            if(blocks.size() > i + 1) {
                                BlockHierarchy following = blocks.get(i + 1);
                                min = following.getMin();

                                if(y == min.getY() && z == min.getZ() && x + 1 == min.getX()) {
                                    //append
                                    blocks.remove(i + 1);
                                    other.append(following);
                                }
                            }
                            return;
                        }
                    } else if(otherZ > z) {
                        //insert at i
                        blocks.add(i, new BlockHierarchy(block));
                        return;
                    }
                } else if(otherY > y) {
                    //insert at i
                    blocks.add(i, new BlockHierarchy(block));
                    return;
                }
            }
        }

        //no match -> append
        blocks.add(new BlockHierarchy(block));
    }
}