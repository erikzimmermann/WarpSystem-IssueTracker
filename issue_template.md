Version: 2.0.8
Minecraft-Version: 1.8
Server-Oparating-System: Windows/Linux
_________________________________

Expectation:

Run the "/warps"-command to open the Warps-GUI
_________________________________

Actual behavior:

Nothing happens
_________________________________

Steps to reproduce the problem:

Just run the "/warps"-Command
_________________________________

Error:

[12:08:51 ERROR]: Could not pass event PlayerCommandPreprocessEvent to WarpSystem v2.0.8
org.bukkit.event.EventException
        at org.bukkit.plugin.java.JavaPluginLoader$1.execute(JavaPluginLoader.java:310) ~[spigot_server_v1_8.jar:git-Spigot-5f38d38-18fbb24]
        at org.bukkit.plugin.RegisteredListener.callEvent(RegisteredListener.java:62) ~[spigot_server_v1_8.jar:git-Spigot-5f38d38-18fbb24]
        at org.bukkit.plugin.SimplePluginManager.fireEvent(SimplePluginManager.java:502) [spigot_server_v1_8.jar:git-Spigot-5f38d38-18fbb24]
        at org.bukkit.plugin.SimplePluginManager.callEvent(SimplePluginManager.java:487) [spigot_server_v1_8.jar:git-Spigot-5f38d38-18fbb24]
        at net.minecraft.server.v1_8_R3.PlayerConnection.handleCommand(PlayerConnection.java:1154) [spigot_server_v1_8.jar:git-Spigot-5f38d38-18fbb24]
        at net.minecraft.server.v1_8_R3.PlayerConnection.a(PlayerConnection.java:997) [spigot_server_v1_8.jar:git-Spigot-5f38d38-18fbb24]
        at net.minecraft.server.v1_8_R3.PacketPlayInChat.a(PacketPlayInChat.java:45) [spigot_server_v1_8.jar:git-Spigot-5f38d38-18fbb24]
        at net.minecraft.server.v1_8_R3.PacketPlayInChat.a(PacketPlayInChat.java:1) [spigot_server_v1_8.jar:git-Spigot-5f38d38-18fbb24]
        at net.minecraft.server.v1_8_R3.PlayerConnectionUtils$1.run(SourceFile:13) [spigot_server_v1_8.jar:git-Spigot-5f38d38-18fbb24]
        at java.util.concurrent.Executors$RunnableAdapter.call(Unknown Source) [?:1.8.0_161]
        at java.util.concurrent.FutureTask.run(Unknown Source) [?:1.8.0_161]
        at net.minecraft.server.v1_8_R3.SystemUtils.a(SourceFile:44) [spigot_server_v1_8.jar:git-Spigot-5f38d38-18fbb24]
        at net.minecraft.server.v1_8_R3.MinecraftServer.B(MinecraftServer.java:715) [spigot_server_v1_8.jar:git-Spigot-5f38d38-18fbb24]
        at net.minecraft.server.v1_8_R3.DedicatedServer.B(DedicatedServer.java:374) [spigot_server_v1_8.jar:git-Spigot-5f38d38-18fbb24]
        at net.minecraft.server.v1_8_R3.MinecraftServer.A(MinecraftServer.java:654) [spigot_server_v1_8.jar:git-Spigot-5f38d38-18fbb24]
        at net.minecraft.server.v1_8_R3.MinecraftServer.run(MinecraftServer.java:557) [spigot_server_v1_8.jar:git-Spigot-5f38d38-18fbb24]
        at java.lang.Thread.run(Unknown Source) [?:1.8.0_161]
Caused by: java.lang.NullPointerException
        at de.codingair.warpsystem.commands.CWarps$1.runCommand(CWarps.java:40) ~[?:?]
        at de.codingair.codingapi.server.commands.CommandBuilder.onCommand(CommandBuilder.java:101) ~[?:?]
        at de.codingair.codingapi.server.commands.CommandBuilder$1.onPreProcess(CommandBuilder.java:42) ~[?:?]
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[?:1.8.0_161]
        at sun.reflect.NativeMethodAccessorImpl.invoke(Unknown Source) ~[?:1.8.0_161]
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(Unknown Source) ~[?:1.8.0_161]
        at java.lang.reflect.Method.invoke(Unknown Source) ~[?:1.8.0_161]
        at org.bukkit.plugin.java.JavaPluginLoader$1.execute(JavaPluginLoader.java:306) ~[spigot_server_v1_8.jar:git-Spigot-5f38d38-18fbb24]
        ... 16 more
