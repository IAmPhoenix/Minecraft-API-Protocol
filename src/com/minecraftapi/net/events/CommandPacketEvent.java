package com.minecraftapi.net.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Custom event that will be triggered when the PacketReciver gets the command packet.
 * 
 * @author Alexis Tan
 */
public class CommandPacketEvent extends Event
{

    private static final HandlerList handlers = new HandlerList();
    private final String address;
    private String command;
    private boolean canceled;

    public CommandPacketEvent(String command, String address)
    {
        this.command = command;
        this.address = address;

        canceled = false;
    }

    /**
     * @return the address
     */
    public String getAddress()
    {
        return address;
    }

    /**
     * @return the command
     */
    public String getCommand()
    {
        return command;
    }

    /**
     * @param command the command to set
     */
    public void setCommand(String command)
    {
        this.command = command;
    }

    /**
     * @return the canceled
     */
    public boolean isCanceled()
    {
        return canceled;
    }

    /**
     * @param canceled the canceled to set
     */
    public void setCanceled(boolean canceled)
    {
        this.canceled = canceled;
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
