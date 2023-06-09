package de.flam.untitled;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.HashMap;

public class ScanBlocks {

    public static HashMap<Integer, ScanBlocks> scanBlocks = new HashMap<>();
    public static Integer atId = 0;

    Location pos1;
    Location pos2;

    ArrayList<BlockDisplay> blockDisplays = new ArrayList<>();

    Boolean filled = false;

    Integer id;

    Location origin;

    Float scale = 0.5f;

    Float rotationX = 0f;
    Float rotationY = 0f;
    Float rotationZ = 0f;

    public ScanBlocks(Location pos1, Location pos2) {
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    public void setFilled(Boolean filled) {
        this.filled = filled;
    }

    public void scan() {
        int x1 = pos1.getBlockX();
        int y1 = pos1.getBlockY();
        int z1 = pos1.getBlockZ();

        int x2 = pos2.getBlockX();
        int y2 = pos2.getBlockY();
        int z2 = pos2.getBlockZ();

        int xMin = Math.min(x1, x2);
        int yMin = Math.min(y1, y2);
        int zMin = Math.min(z1, z2);

        int xMax = Math.max(x1, x2);
        int yMax = Math.max(y1, y2);
        int zMax = Math.max(z1, z2);

        origin = new Location(pos1.getWorld(), xMin, yMax, zMin);

        int atX = 0;
        int atY = 0;
        int atZ = 0;

        for (int x = xMin; x <= xMax; x++) {
            atX++;
            atY = 0;
            for (int y = yMin; y <= yMax; y++) {
                atY++;
                atZ = 0;
                for (int z = zMin; z <= zMax; z++) {
                    atZ++;

                    //skip if is air
                    if (pos1.getWorld().getBlockAt(x, y, z).getType() == Material.AIR) {
                        continue;
                    }

                    if (!filled) {
                        //skip if is not connected to air
                        if (pos1.getWorld().getBlockAt(x, y, z).getRelative(0, 1, 0).getType().isSolid() &&
                                pos1.getWorld().getBlockAt(x, y, z).getRelative(0, -1, 0).getType().isSolid() &&
                                pos1.getWorld().getBlockAt(x, y, z).getRelative(1, 0, 0).getType().isSolid() &&
                                pos1.getWorld().getBlockAt(x, y, z).getRelative(-1, 0, 0).getType().isSolid() &&
                                pos1.getWorld().getBlockAt(x, y, z).getRelative(0, 0, 1).getType().isSolid() &&
                                pos1.getWorld().getBlockAt(x, y, z).getRelative(0, 0, -1).getType().isSolid()) {

                            // wenn es nicht am rand ist, dann skip
                            if (x != xMin && x != xMax && y != yMin && y != yMax && z != zMin && z != zMax) {
                                // und wenn es keine leaves sind
                                if (!pos1.getWorld().getBlockAt(x, y, z).getType().isTransparent()) {
                                    continue;
                                }
                            }

                        }
                    }

                    BlockData block = pos1.getWorld().getBlockAt(x, y, z).getBlockData();

                    // Spawn BlockDisplay at the location of the block
                    Location location = pos1.getWorld().getBlockAt(x, y, z).getLocation();

                    // bewege es doppelt so hoch wie die selection groß ist
                    // (yMax - yMin) als y-koordinate und dazu alle je nach position 0.5 näher bei aneinander
                    location.add(-0.5 * atX, (yMax - yMin) -0.5 * atY, -0.5 * atZ);


                    BlockDisplay blockDisplay = location.getWorld().spawn(location, BlockDisplay.class);
                    blockDisplay.setBlock(block);

                    // set the scale of the BlockDisplay to 0.5

                    blockDisplay.setTransformation(new Transformation(new Vector3f(0.5f, 0.5f, 0.5f), new Quaternionf(), new Vector3f(0.5f, 0.5f, 0.5f), new Quaternionf()));

                    blockDisplays.add(blockDisplay);

                }
            }
        }

        scanBlocks.put(atId, this);
        id = atId;
        atId++;

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendMessage("§aScanned " + blockDisplays.size() + " blocks");
            player.sendMessage("§aScan ID: " + (atId - 1));
        });
    }

    public void delete() {
        blockDisplays.forEach(blockDisplay -> {
            blockDisplay.remove();
        });

        scanBlocks.remove(id);

    }

    public void changeScale(float scale) {
        blockDisplays.forEach(blockDisplay -> {

            // Scale und Position neu berechnen

            blockDisplay.setTransformation(new Transformation(new Vector3f(scale, scale, scale), new Quaternionf().rotationXYZ((float) Math.toRadians(rotationX), (float) Math.toRadians(rotationY), (float) Math.toRadians(rotationZ)), new Vector3f(scale, scale, scale), new Quaternionf()));

            Location location = blockDisplay.getLocation();

            // nutze den origin um den abstand zu berechnen um dann die position neu zu setzen mit dem neuen scale
            // desto weiter vom origin entfernt umso weiter weg von der position bewegen

            location.add((location.getX() - origin.getX()) * (scale - this.scale) / this.scale, (location.getY() - origin.getY()) * (scale - this.scale) / this.scale, (location.getZ() - origin.getZ()) * (scale - this.scale) / this.scale);

            blockDisplay.teleport(location);

        });

        this.scale = scale;

    }

    public void moveHere(Location location) {

        double xDistance = location.getX() - origin.getX();
        double yDistance = location.getY() - origin.getY();
        double zDistance = location.getZ() - origin.getZ();

        blockDisplays.forEach(blockDisplay -> {

            Location blockLocation = blockDisplay.getLocation();

            blockLocation.add(xDistance, yDistance, zDistance);

            blockDisplay.teleport(blockLocation);

        });

        origin = location;

    }

    public void rotate(String axis, Float angel){

        if (axis.equals("x")){
            rotationX += angel;
        } else if (axis.equals("y")){
            rotationY += angel;
        } else if (axis.equals("z")){
            rotationZ += angel;
        }

        blockDisplays.forEach(blockDisplay -> {

            blockDisplay.setTransformation(new Transformation(new Vector3f(scale, scale, scale), new Quaternionf().rotationXYZ((float) Math.toRadians(rotationX), (float) Math.toRadians(rotationY), (float) Math.toRadians(rotationZ)), new Vector3f(scale, scale, scale), new Quaternionf()));

            //position neu berechnen anhand des origins mit sin und cos
            Location location = blockDisplay.getLocation();

            // Neue Position basierend auf der Rotation berechnen
            double newX = origin.getX() + (location.getX() - origin.getX()) * Math.cos(Math.toRadians(rotationY)) - (location.getZ() - origin.getZ()) * Math.sin(Math.toRadians(rotationY));
            double newZ = origin.getZ() + (location.getZ() - origin.getZ()) * Math.cos(Math.toRadians(rotationY)) + (location.getX() - origin.getX()) * Math.sin(Math.toRadians(rotationY));
            double newY = origin.getY() + (location.getY() - origin.getY()) * Math.cos(Math.toRadians(rotationX)) + (newX - origin.getX()) * Math.sin(Math.toRadians(rotationX));

            // Neue Position setzen
            Location newLocation = new Location(origin.getWorld(), newX, newY, newZ, location.getYaw(), location.getPitch());
            blockDisplay.teleport(newLocation);


        });

    }

}
