/*
 * Copyright (C) 2014 Minecraft-API.com
 * This file is part of Minecraft-API.
 * 
 * Minecraft-API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Minecraft-API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Minecraft-API. If not, see <http://www.gnu.org/licenses/>.
 */
package com.minecraftapi;

import com.minecraftapi.metrics.Metrics;
import com.minecraftapi.model.Collector;
import com.minecraftapi.model.KeyManager;
import com.minecraftapi.net.PacketReciver;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main Minecraft-API plugin class.
 * 
 * @author Alexis Tan
 */
public class MinecraftAPI extends JavaPlugin
{

    /** The logger instance */
    private static final Logger LOG = Logger.getLogger("Minecraft-API");
    /** Log entry prefix */
    private static final String logPrefix = "[Minecraft-API] ";
    /** The Minecraft-API instance. */
    private static MinecraftAPI instance;
    /** The current plugin version */
    private String version;
    /** The packet reciver */
    private PacketReciver packetReciver;
    /** The key manager */
    public KeyManager keyManager;
    /** The data collector */
    public Collector collector;
    /** Debug mode flag */
    private boolean debug;

    /**
     * Attach custom log filter to logger.
     */
    static {
        LOG.setFilter(new LogFilter(logPrefix));
    }

    @Override
    public void onEnable()
    {
        MinecraftAPI.instance = this;

        // Run metrics 
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Failed to start Metrics - {0}", e.getLocalizedMessage());
        }

        // Set the plugin version.
        version = getDescription().getVersion();

        // instantiate the collector
        collector = new Collector(this);

        // instantiate the key manager.
        keyManager = new KeyManager(getDataFolder());

        // Handle configuration.
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        File config = new File(getDataFolder() + "/config.yml");

        /*
         * Use IP address from server.properties as a default for
         * configurations. Do not use InetAddress.getLocalHost() as it most
         * likely will return the main server address instead of the address
         * assigned to the server.
         */
        String hostAddr = Bukkit.getServer().getIp();
        if (hostAddr == null || hostAddr.length() == 0) {
            hostAddr = "0.0.0.0";
        }

        // Create secure.key if it doesn't exists, otherwise, load it.
        if (keyManager.getKey() == null) {
            LOG.log(Level.SEVERE, "Error creating secure.key file");
            gracefulExit();
            return;
        }
        // Create config.yml if it doesn't exists.
        if (!config.exists()) {
            // First time run - do some initialization.
            LOG.info("Configuring Minecraft-API for the first time...");

            // Initialize the configuration file.
            getConfig().options().copyDefaults(true);
            saveDefaultConfig();

            /*
             * Remind hosted server admins to be sure they have the right
             * port number.
             */
            LOG.info("------------------------------------------------------------------------------");
            LOG.info("Assigning Minecraft-API to listen on port 39299. If you are hosting Craftbukkit");
            LOG.info("on a shared server please check with your hosting provider to verify that this");
            LOG.info("port is available for your use. Chances are that your hosting provider will");
            LOG.info("assign a different port, which you need to specify in config.yml");
            LOG.info("------------------------------------------------------------------------------");
        }

        // Initialize the receiver.
        String host = getConfig().getString("host", hostAddr);
        int port = getConfig().getInt("port", 39299);
        debug = getConfig().getBoolean("debug", false);
        if (debug) {
            LOG.info("DEBUG mode enabled!");
        }

        try {
            packetReciver = new PacketReciver(this, host, port);
            packetReciver.start();

            LOG.info("Minecraft-API enabled.");
        } catch (Exception ex) {
            gracefulExit();
        }
    }

    @Override
    public void onDisable()
    {
        packetReciver.shutdown();
    }

    private void gracefulExit()
    {
        LOG.log(Level.SEVERE, "Minecraft-API did not initialize properly!");
        getServer().getPluginManager().disablePlugin(this);
    }

    /**
     * Gets the instance.
     * 
     * @return The instance
     */
    public static MinecraftAPI getInstance()
    {
        return instance;
    }

    /**
     * Gets the version.
     * 
     * @return The version
     */
    public String getVersion()
    {
        return version;
    }

    /**
     * Checks to see if debug mode is enabled.
     * 
     * @return The debug flag
     */
    public boolean isDebug()
    {
        return debug;
    }
}
