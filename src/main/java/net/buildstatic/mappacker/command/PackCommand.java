package net.buildstatic.mappacker.command;

import net.buildstatic.mappacker.MapPacker;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 BuildStatic
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class PackCommand implements CommandExecutor, Listener {

    private static final String[] FILES_TO_REMOVE = new String[] { "level.dat_old", "session.lock", "playerdata", "players" };

    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        try {
            if (args.length > 1) {
                World world = Bukkit.getWorld(args[0]);
                if (world != null) {
                    File tempDir = new File(System.getProperty("java.io.tmpdir"));
                    File tempWorld = new File(tempDir, world.getWorldFolder().getName() + File.separator + world.getWorldFolder().getName());
                    FileUtils.copyDirectory(world.getWorldFolder(), tempWorld);

                    File mapDat = new File(tempWorld, "map.dat");
                    if (!mapDat.exists() && !mapDat.createNewFile()) {
                        FileUtils.deleteDirectory(tempWorld);
                        sender.sendMessage(ChatColor.RED + "Error: Aborting... unable to create map.dat. Try again?");
                        return false;
                    }
                    YamlConfiguration configuration = YamlConfiguration.loadConfiguration(mapDat);
                    StringBuilder creator = new StringBuilder();
                    for(int i=1; i < args.length; i++) {
                        creator.append(i == 1 || i+1 == args.length ? "" : " ");
                        creator.append(args[i]);
                    }
                    configuration.set("Creator", creator.toString());
                    configuration.save(mapDat);

                    Arrays.stream(FILES_TO_REMOVE).forEach(fileName -> {
                        File file = new File(tempWorld, fileName);
                        if(file.isDirectory()) Arrays.stream(file.listFiles()).forEach(file1 -> file1.delete());
                        file.delete();
                    });

                    File zipped = new File(MapPacker.getInstance().getDataFolder(), tempWorld.getName() + ".zip");
                    if(zipped.exists() && !zipped.delete()) {
                        sender.sendMessage(ChatColor.RED + "Error: Unable to delete old zip. Try again?");
                        FileUtils.deleteDirectory(tempWorld);
                        return false;
                    }
                    ZipUtil.pack(new File(tempDir, world.getWorldFolder().getName()), zipped);
                    if (zipped.exists())
                        sender.sendMessage(ChatColor.GREEN + "Success! The map is all ready to go @ " + zipped.getAbsolutePath() + ".");
                    else sender.sendMessage(ChatColor.RED + "Error: File was not able to be zipped up. Try again?");

                    FileUtils.deleteDirectory(tempWorld);
                    return true;
                }
                sender.sendMessage(ChatColor.RED + "Error: \"" + args[0] + "\" is not a valid world.");
            } else sender.sendMessage(ChatColor.RED + "Usage: /pack <world name> <creator>");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }

}
