package com.minecraftapi.net.events;

import java.util.LinkedHashMap;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Custom event that will be triggered when the PacketReciver gets the request packet.
 * 
 * @author Alexis Tan
 */
public class RequestPacketEvent extends Event
{

    private static final HandlerList handlers = new HandlerList();
    private LinkedHashMap json;

    public RequestPacketEvent(LinkedHashMap json)
    {
        this.json = json;
    }

    /**
     * @return the json
     */
    public LinkedHashMap getJson()
    {
        return json;
    }

    /**
     * @param json the json to set
     */
    public void setJson(LinkedHashMap json)
    {
        if (json == null) {
            this.json.clear();
        } else {
            this.json = json;
        }
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}
