package com.minecraftapi.net;

/**
 * The Packets that are available within the protocol.
 * 
 * @author Alexis
 */
enum Packet
{

    /** 0x00 */
    HANDSHAKE(0),
    /** 0x01 */
    REQUEST(1),
    /** 0x02 */
    COMMAND(2),
    /** This is used for when a packet is non of the above. */
    UNKNOW(9);
    /** Packet ID */
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
