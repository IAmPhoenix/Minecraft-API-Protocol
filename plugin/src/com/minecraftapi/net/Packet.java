package com.minecraftapi.net;

public enum Packet
{

    HANDSHAKE(0), REQUEST(1), COMMAND(2), UNKNOW(9);
    private int packetID;

    private Packet(int id)
    {
        packetID = id;
    }

    private int getPacketID()
    {
        return packetID;
    }
}
