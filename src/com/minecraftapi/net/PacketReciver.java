package com.minecraftapi.net;

import com.minecraftapi.MinecraftAPI;
import com.minecraftapi.net.events.CommandPacketEvent;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Packet receiving server.
 * 
 * @author Alexis Tan
 */
public class PacketReciver extends Thread
{

    /** The logger instance. */
    private static final Logger LOG = Logger.getLogger("Minecraft-API");
    /** The plugin instance. */
    private final MinecraftAPI plugin;
    /** The host to listen on. */
    private final String host;
    /** The port to listen on. */
    private final int port;
    /** The server socket. */
    private ServerSocket server;
    /** The running flag. */
    private boolean running = true;

    public PacketReciver(MinecraftAPI plugin, String host, int port) throws Exception
    {
        this.plugin = plugin;
        this.host = host;
        this.port = port;

        initialize();
    }

    private void initialize() throws Exception
    {
        try {
            server = new ServerSocket();
            server.bind(new InetSocketAddress(host, port));
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "------------------------------------------------------------------------------");
            LOG.log(Level.SEVERE, "Error initializing packet receiver. Please verify that the configured IP ");
            LOG.log(Level.SEVERE, "address and port are not already in use. This is a common problem with hosting");
            LOG.log(Level.SEVERE, "services and, if so, you should check with your hosting provider.", ex);
            LOG.log(Level.SEVERE, "------------------------------------------------------------------------------");
            throw new Exception(ex);
        }
    }

    /**
     * Shuts the packet reciver down cleanly.
     */
    public void shutdown()
    {
        running = false;
        if (server == null) {
            return;
        }
        try {
            server.close();
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "Unable to shut down packet receiver cleanly.");
        }
    }

    @Override
    public void run()
    {
        while (running) {
            try {
                Socket socket = server.accept();
                socket.setSoTimeout(5000); // Don't hang on slow connections.

                // Create our read and write objects.
                InputStreamReader inputStream = new InputStreamReader(socket.getInputStream());
                BufferedReader input = new BufferedReader(inputStream);
                DataOutputStream response = new DataOutputStream(socket.getOutputStream());

                // Get the packet list
                String packet = input.readLine().replace("\\0x", "0x");
                String[] packetList = packet.split("0x00");

                String key = packetList[0];

                // Find action packet
                Packet action;
                switch (packetList[1]) {
                    case "0x01":
                        action = Packet.REQUEST;
                        break;
                    case "0x02":
                        action = Packet.COMMAND;
                        break;
                    default:
                        action = Packet.UNKNOW;
                        break;
                }

                // Excute action if the key match up
                String responseString = "{\"error\":\"Invalid key\"}";
                if (plugin.keyManager.getKey().equals(key)) {
                    if (action.equals(Packet.REQUEST)) {
                        if (plugin.isDebug()) {
                            LOG.log(Level.INFO, "Recived request packet -> IP:{0}", new Object[]{socket.getRemoteSocketAddress()});
                        }
                        responseString = plugin.collector.getJsonOutput();
                    } else if (action.equals(Packet.COMMAND)) {
                        String command = packetList[2];
                        if (plugin.isDebug()) {
                            LOG.log(Level.INFO, "Recived command packet -> IP:{0} COMMAND:({1})", new Object[]{socket.getRemoteSocketAddress(), command});
                        }

                        CommandPacketEvent event = new CommandPacketEvent(command, socket.getInetAddress().getHostAddress());
                        plugin.getServer().getPluginManager().callEvent(event);

                        if (!event.isCanceled()) {
                            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), event.getCommand());
                            responseString = "{\"message\":\"Command have been executed successfully\",\"command\":\"" + command + "\"}";
                        } else {
                            responseString = "{\"message\":\"Command have been canceld by an Event\",\"command\":\"" + command + "\"}";
                        }
                    } else if (action.equals(Packet.UNKNOW)) {
                        responseString = "{\"error\":\"Unknow action packet. Ignoring request.\"}";
                    }
                }

                // Send response
                response.writeBytes(responseString);
                response.flush();
                response.close();
            } catch (SocketException ex) {
                if (plugin.isDebug()) {
                    LOG.log(Level.WARNING, "Protocol error. Ignoring packet - {0}", ex.getLocalizedMessage());
                }
            } catch (Exception ex) {
                if (plugin.isDebug()) {
                    LOG.log(Level.WARNING, "Exception caught while receiving a packet notification - {0}", ex.getLocalizedMessage());
                }
            }
        }
    }
}
