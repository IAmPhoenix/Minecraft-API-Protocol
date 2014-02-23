# Minecraft-API Protocol

Minecraft-API Protocol is a Bukkit plugin that is made with the intent to enhance your experiances with the http://Minecraft-API.com website by adding custom protocols to retrive multiple types of information in one go. Minecraft-API Protocol creates a *lightweight* server that waits for connections by http://Minecraft-API.com or any custom site that has the key, and uses a simple protocol to get the required information. Minecraft-API Protocol is *secure*, and makes sure that all packets are delivered by validated clients.

## Configuring Minecraft-API Protocol

Minecraft-API Protocol configures itself the first time it is run.

If you want to customize Minecraft-API Protocol, simply edit the `./plugins/Minecraft-API/config.yml` file.

## Encryption

Minecraft-API Protocol uses a one-way randomized key to ensure that only trusted clients are able to comunicate with the protocol on the desired server. When it is first run, Minecraft-API Protocol will generate a 512 bit key and store the key in the `./plugins/Minecraft-API/secure.key` file. It is essential that you do not share this key with your players, as a smart player can use the key to create a command packet and tell Minecraft-API to op thems, give them permissions, ranks etc.

## Custom Packets and Developing tools

This will be added later as the plugin is still in development.
