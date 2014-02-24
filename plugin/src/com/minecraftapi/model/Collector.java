package com.minecraftapi.model;

import com.google.gson.Gson;
import com.minecraftapi.MinecraftAPI;
import java.util.LinkedHashMap;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * The collector class for Miencraft-API.
 * 
 * @author Alexis
 */
public class Collector
{

    /** Instance of the Main class. */
    private final MinecraftAPI plugin;
    /** The time the server started in milliseconds. */
    private final long serverStart;

    public Collector(MinecraftAPI plugin)
    {
        this.plugin = plugin;

        serverStart = System.currentTimeMillis();
    }

    /**
     * Loops through a ton of data and stores it in a LinkedHashMap 
     * to create a Gson object that can be parsed on to the client.
     * 
     * @return The Gson object of sever information
     */
    public String getJsonOutput()
    {
        Gson gson = new Gson();
        LinkedHashMap<String, Object> json = new LinkedHashMap();

        // Status
        json.put("status", true);

        // Players
        LinkedHashMap<String, Object> players = new LinkedHashMap();
        LinkedHashMap<String, Object> list = new LinkedHashMap();
        players.put("online", plugin.getServer().getOnlinePlayers().length);
        players.put("max", plugin.getServer().getMaxPlayers());
        for (Player playerObject : plugin.getServer().getOnlinePlayers()) {
            LinkedHashMap<String, Object> player = new LinkedHashMap();

            player.put("health-level", playerObject.getHealth());
            player.put("food-level", playerObject.getFoodLevel());
            player.put("gamemode", playerObject.getGameMode());

            list.put(playerObject.getName(), player);
        }
        players.put("list", list);
        json.put("players", players);

        // MoTD
        json.put("motd", plugin.getServer().getMotd());

        // Version
        String version = (plugin.getServer().getVersion().contains(" ")) ? plugin.getServer().getVersion().split(" ")[2] : "Unknow version..";
        json.put("version", version.substring(0, version.length() - 1));

        // Software
        json.put("software", plugin.getServer().getVersion());

        // Plugins
        LinkedHashMap<String, Object> plugins = new LinkedHashMap();
        for (Plugin pluginObject : this.plugin.getServer().getPluginManager().getPlugins()) {
            LinkedHashMap<String, Object> pluginList = new LinkedHashMap();
            pluginList.put("version", pluginObject.getDescription().getVersion());
            pluginList.put("authors", pluginObject.getDescription().getAuthors());
            pluginList.put("description", pluginObject.getDescription().getDescription());

            plugins.put(pluginObject.getName(), pluginList);
        }
        json.put("plugins", plugins);

        // Worlds
        LinkedHashMap<String, Object> worlds = new LinkedHashMap();
        for (World worldObject : plugin.getServer().getWorlds()) {
            LinkedHashMap<String, Object> world = new LinkedHashMap();
            world.put("world-type", worldObject.getWorldType().getName());
            world.put("difficulty", worldObject.getDifficulty().name());
            world.put("gamerules", worldObject.getGameRules());
            world.put("pvp-enabled", worldObject.getPVP());

            worlds.put(worldObject.getName(), world);
        }
        json.put("worlds", worlds);

        // Uptime
        final long diff = System.currentTimeMillis() - serverStart;
        json.put("uptime", (int) (diff / 1000));

        return gson.toJson(json);
    }
}
