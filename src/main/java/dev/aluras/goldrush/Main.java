package dev.aluras.goldrush;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.timer.TaskSchedule;

public class Main {
    public static void main(final String[] args) {
        final MinecraftServer server = MinecraftServer.init();

        //Create an instance (a world)
        final InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        final InstanceContainer instanceContainer = instanceManager.createInstanceContainer();

        //generate the world
        instanceContainer.setGenerator(unit -> {
            unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK);
        });
        //light world
        instanceContainer.setChunkSupplier(LightingChunk::new);

        final GlobalEventHandler handler = MinecraftServer.getGlobalEventHandler();

        //add an event handler to player spawning
        handler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setSpawningInstance(instanceContainer);
            player.setRespawnPoint(new Pos(0, 42, 0));
        });
        //place block event
        handler.addListener(PlayerBlockPlaceEvent.class, event -> {
            event.setCancelled(false);
        });

        //give player pickaxe
        handler.addListener(PlayerSpawnEvent.class, event -> {
            final Player player = event.getPlayer();
            player.getInventory().addItemStack(ItemStack.of(Material.IRON_PICKAXE));
            player.getInventory().addItemStack(ItemStack.of(Material.GOLD_ORE));
        });

        //gold ore block break event
        handler.addListener(PlayerBlockBreakEvent.class, event -> {
            if (event.getBlock() == Block.GOLD_ORE) {
                final Player player = event.getPlayer();

                event.setResultBlock(Block.BEDROCK);
                MinecraftServer.getSchedulerManager().scheduleTask(() -> event.getInstance().setBlock(event.getBlockPosition(), Block.GOLD_ORE), TaskSchedule.seconds(5), TaskSchedule.stop()); //epic block respawn moment

                player.getInventory().addItemStack(ItemStack.of(Material.RAW_GOLD));
                player.sendMessage(Component.text("You mined a gold ore and earned", NamedTextColor.WHITE)
                        .append(Component.text(" 1 coin", NamedTextColor.GOLD)
                                .append(Component.text("!", NamedTextColor.WHITE))));
            }
        });

        server.start("0.0.0.0", 25565);
    }
}