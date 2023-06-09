package de.flam.untitled;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Command implements CommandExecutor, TabCompleter {

    static HashMap<UUID, Location> playerPos1 = new HashMap<>();
    static HashMap<UUID, Location> playerPos2 = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {

        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            if (strings.length == 0) {
                player.sendMessage("§cPlease use §6/holo help §cfor help.");
                return false;
            }

            if (strings[0].equalsIgnoreCase("help")) {
                player.sendMessage("§6/holo help §7- §fShows this help message.");
                player.sendMessage("§6/holo pos1 §7- §fSets the first position.");
                player.sendMessage("§6/holo pos2 §7- §fSets the second position.");
                player.sendMessage("§6/holo scan <type>§7- §fScans the blocks between the two positions.");
                player.sendMessage("§6/holo desel §7- §fDeselects the current selection.");
                player.sendMessage("§6/holo delete <id> §7- §fDeletes the hologram with the given id.");
                player.sendMessage("§6/holo size <id> <size> §7- §fSets the size of the hologram with the given id.");
                player.sendMessage("§6/holo moveHere <id> §7- §fMoves the hologram with the given id to your position.");
                player.sendMessage("§6/holo rotate <id> <x,y,z> <angle[0-360]> §7- §fRotates the hologram with the given id around the given axis.");
                return true;
            }

            if (strings[0].equalsIgnoreCase("pos1")) {
                playerPos1.put(player.getUniqueId(), player.getLocation());
                player.sendMessage("§aPosition 1 set.");

                if (playerPos2.containsKey(player.getUniqueId()) && playerPos1.containsKey(player.getUniqueId())) {
                    // count blocks
                    Location pos1 = playerPos1.get(player.getUniqueId());
                    Location pos2 = playerPos2.get(player.getUniqueId());

                    int x1 = pos1.getBlockX();
                    int y1 = pos1.getBlockY();
                    int z1 = pos1.getBlockZ();

                    int x2 = pos2.getBlockX();
                    int y2 = pos2.getBlockY();
                    int z2 = pos2.getBlockZ();

                    int xMin = Math.min(x1, x2);
                    int yMin = Math.min(y1, y2);
                    int zMin = Math.min(z1, z2);

                    int xMax = Math.max(x1, x2) + 1;
                    int yMax = Math.max(y1, y2) + 1;
                    int zMax = Math.max(z1, z2) + 1;

                    int xDiff = xMax - xMin;
                    int yDiff = yMax - yMin;
                    int zDiff = zMax - zMin;

                    int blocks = xDiff * yDiff * zDiff;

                    player.sendMessage("§aSelected " + blocks + " blocks.");
                }

                return true;
            }

            if (strings[0].equalsIgnoreCase("pos2")) {
                playerPos2.put(player.getUniqueId(), player.getLocation());
                player.sendMessage("§aPosition 2 set.");

                if (playerPos2.containsKey(player.getUniqueId()) && playerPos1.containsKey(player.getUniqueId())) {
                    // count blocks
                    Location pos1 = playerPos1.get(player.getUniqueId());
                    Location pos2 = playerPos2.get(player.getUniqueId());

                    int x1 = pos1.getBlockX();
                    int y1 = pos1.getBlockY();
                    int z1 = pos1.getBlockZ();

                    int x2 = pos2.getBlockX();
                    int y2 = pos2.getBlockY();
                    int z2 = pos2.getBlockZ();

                    int xMin = Math.min(x1, x2);
                    int yMin = Math.min(y1, y2);
                    int zMin = Math.min(z1, z2);

                    int xMax = Math.max(x1, x2) + 1;
                    int yMax = Math.max(y1, y2) + 1;
                    int zMax = Math.max(z1, z2) + 1;

                    int xDiff = xMax - xMin;
                    int yDiff = yMax - yMin;
                    int zDiff = zMax - zMin;

                    int blocks = xDiff * yDiff * zDiff;

                    player.sendMessage("§aSelected " + blocks + " blocks.");
                }

                return true;
            }

            if (strings[0].equalsIgnoreCase("scan")) {
                if (playerPos1.containsKey(player.getUniqueId()) && playerPos2.containsKey(player.getUniqueId())) {
                    Location pos1 = playerPos1.get(player.getUniqueId());
                    Location pos2 = playerPos2.get(player.getUniqueId());

                    ScanBlocks scanBlocks = new ScanBlocks(pos1, pos2);

                    if (strings.length == 2) {
                        if (strings[1].equalsIgnoreCase("hollow")) {
                            scanBlocks.setFilled(false);
                        } else if (strings[1].equalsIgnoreCase("filled")) {
                            scanBlocks.setFilled(true);
                        } else {
                            player.sendMessage("§cPlease use §6/holo help §cfor help.");
                            return false;
                        }
                    }

                    scanBlocks.scan();
                    return true;
                } else {
                    player.sendMessage("§cPlease set both positions first.");
                    return false;
                }
            }

            if (strings[0].equalsIgnoreCase("desel")) {
                playerPos1.remove(player.getUniqueId());
                playerPos2.remove(player.getUniqueId());
                player.sendMessage("§aSelection deselected.");
                return true;
            }

            if (strings[0].equalsIgnoreCase("delete")) {
                if (strings.length == 2) {
                    try {
                        if (ScanBlocks.scanBlocks.containsKey(Integer.valueOf(strings[1]))) {
                            ScanBlocks.scanBlocks.get(Integer.valueOf(strings[1])).delete();
                            player.sendMessage("§aHologram deleted.");
                            return true;
                        } else {
                            player.sendMessage("§cHologram not found.");
                            return false;
                        }
                    }catch (NumberFormatException e) {
                        player.sendMessage("§cPlease use §6/holo help §cfor help.");
                        return false;
                    }

                } else {
                    player.sendMessage("§cPlease use §6/holo help §cfor help.");
                    return false;
                }
            }

            if (strings[0].equalsIgnoreCase("size")) {
                if (strings.length == 3) {
                    try {
                        if (ScanBlocks.scanBlocks.containsKey(Integer.valueOf(strings[1]))) {
                            ScanBlocks.scanBlocks.get(Integer.valueOf(strings[1])).changeScale(Float.valueOf(strings[2]));
                            player.sendMessage("§aHologram size set.");
                            return true;
                        } else {
                            player.sendMessage("§cHologram not found.");
                            return false;
                        }
                    }catch (NumberFormatException e) {
                        player.sendMessage("§cPlease use §6/holo help §cfor help.");
                        return false;
                    }

                } else {
                    player.sendMessage("§cPlease use §6/holo help §cfor help.");
                    return false;
                }
            }

            if (strings[0].equalsIgnoreCase("moveHere")) {
                if (strings.length == 2) {
                    try {
                        if (ScanBlocks.scanBlocks.containsKey(Integer.valueOf(strings[1]))) {
                            ScanBlocks.scanBlocks.get(Integer.valueOf(strings[1])).moveHere(player.getLocation());
                            player.sendMessage("§aHologram moved.");
                            return true;
                        } else {
                            player.sendMessage("§cHologram not found.");
                            return false;
                        }
                    }catch (NumberFormatException e) {
                        player.sendMessage("§cPlease use §6/holo help §cfor help.");
                        return false;
                    }

                } else {
                    player.sendMessage("§cPlease use §6/holo help §cfor help.");
                    return false;
                }
            }

            if (strings[0].equalsIgnoreCase("rotate")) {
                // get the id, the axis and the angle
                if (strings.length == 4) {
                    try {
                        if (ScanBlocks.scanBlocks.containsKey(Integer.valueOf(strings[1]))) {
                            ScanBlocks.scanBlocks.get(Integer.valueOf(strings[1])).rotate(strings[2], Float.valueOf(strings[3]));
                            player.sendMessage("§aHologram rotated.");
                            return true;
                        } else {
                            player.sendMessage("§cHologram not found.");
                            return false;
                        }
                    }catch (NumberFormatException e) {
                        player.sendMessage("§cPlease use §6/holo help §cfor help.");
                        return false;
                    }

                } else {
                    player.sendMessage("§cPlease use §6/holo help §cfor help.");
                    return false;
                }
            }

            player.sendMessage("§cPlease use §6/holo help §cfor help.");
            return false;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {

        List<String> tabComplete = new ArrayList<>();

        if (strings.length == 1) {
            tabComplete.add("help");
            tabComplete.add("pos1");
            tabComplete.add("pos2");
            tabComplete.add("scan");
            tabComplete.add("desel");
            tabComplete.add("size");
            tabComplete.add("moveHere");
            tabComplete.add("rotate");
            tabComplete.add("delete");

            tabComplete.removeIf(string -> !string.startsWith(strings[0]));

        }

        if (strings.length == 2) {
            if (strings[0].equalsIgnoreCase("scan")) {
                tabComplete.add("hollow");
                tabComplete.add("filled");
            }

            if (strings[0].equalsIgnoreCase("delete") || strings[0].equalsIgnoreCase("size") || strings[0].equalsIgnoreCase("moveHere") || strings[0].equalsIgnoreCase("rotate")) {
                ScanBlocks.scanBlocks.forEach((integer, scanBlocks) -> {
                    tabComplete.add(String.valueOf(integer));
                });
            }


            tabComplete.removeIf(string -> !string.startsWith(strings[1]));

        }

        if (strings.length == 3 && strings[0].equalsIgnoreCase("rotate")) {
            tabComplete.add("x");
            tabComplete.add("y");
            tabComplete.add("z");

            tabComplete.removeIf(string -> !string.startsWith(strings[2]));
        }

        return tabComplete;

    }

    public void start(){
        //runnable that runs every 20 ticks and spawns particles as bounding box for each player
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Holo.getPlugin(), new Runnable() {
            @Override
            public void run() {
                for (UUID uuid : playerPos1.keySet()) {
                    Location pos1 = playerPos1.get(uuid);
                    Location pos2 = playerPos2.get(uuid);

                    if (pos1 != null && pos2 != null) {
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

                        for (int x = xMin; x <= xMax; x++) {
                            for (int y = yMin; y <= yMax; y++) {
                                for (int z = zMin; z <= zMax; z++) {
                                    if (x == xMin || x == xMax || y == yMin || y == yMax || z == zMin || z == zMax) {
                                        Location location = pos1.getWorld().getBlockAt(x, y, z).getLocation();
                                        location.add(0.5, 0.5, 0.5);
                                        location.getWorld().spawnParticle(org.bukkit.Particle.REDSTONE, location, 1, 0, 0, 0, 0, new org.bukkit.Particle.DustOptions(org.bukkit.Color.RED, 1));
                                    }
                                }
                            }
                        }
                    }

                    if (pos1 != null) {
                        Location location = pos1.getBlock().getLocation().clone();
                        location.add(0.5, 0.5, 0.5);
                        location.getWorld().spawnParticle(org.bukkit.Particle.REDSTONE, location, 1, 0, 0, 0, 0, new org.bukkit.Particle.DustOptions(Color.GREEN, 3));
                    }

                    if (pos2 != null) {
                        Location location = pos2.getBlock().getLocation().clone();
                        location.add(0.5, 0.5, 0.5);
                        location.getWorld().spawnParticle(org.bukkit.Particle.REDSTONE, location, 1, 0, 0, 0, 0, new org.bukkit.Particle.DustOptions(Color.BLUE, 3));
                    }

                }
            }
        }, 0, 20);
    }

}
