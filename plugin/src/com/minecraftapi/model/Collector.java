package com.minecraftapi.model;

import com.google.gson.Gson;
import com.minecraftapi.MinecraftAPI;
import java.util.LinkedHashMap;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Collector implements Runnable
{

    /**  */
    private final MinecraftAPI plugin;
    private int uptime = 0;
    private int taskID = -1;

    public Collector(MinecraftAPI plugin)
    {
        this.plugin = plugin;

        startScheduler();
    }

    public String getJsonOutput()
    {
        Gson gson = new Gson();
        LinkedHashMap<String, Object> json = new LinkedHashMap();
        LinkedHashMap<String, Object> players = new LinkedHashMap();
        LinkedHashMap<String, Object> worlds = new LinkedHashMap();
        LinkedHashMap<String, Object> plugins = new LinkedHashMap();
        LinkedHashMap<String, Object> list = new LinkedHashMap();

        // Status
        json.put("status", true);

        // Players
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
        for (Plugin pluginObject : this.plugin.getServer().getPluginManager().getPlugins()) {
            LinkedHashMap<String, Object> pluginList = new LinkedHashMap();
            pluginList.put("version", pluginObject.getDescription().getVersion());
            pluginList.put("authors", pluginObject.getDescription().getAuthors());
            pluginList.put("description", pluginObject.getDescription().getDescription());

            plugins.put(pluginObject.getName(), pluginList);
        }
        json.put("plugins", plugins);

        // Worlds
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
        json.put("uptime", uptime);



        return gson.toJson(json);
    }

    public void startScheduler()
    {
        if (taskID != -1) {
            return;
        }
        taskID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, 20);
    }

    public void stopScheduler()
    {
        if (taskID == -1) {
            return;
        }
        plugin.getServer().getScheduler().cancelTask(taskID);
    }

    @Override
    public void run()
    {
        uptime += 1;
    }
}
