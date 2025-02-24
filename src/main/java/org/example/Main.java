package org.example;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.ItemEntity;
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

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    public static void main(String[] args) {


        MinecraftServer server = MinecraftServer.init();

        //Create an instance (a world)
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();


        //generate the world
        instanceContainer.setGenerator(unit -> {
            unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK);
        });
        //light world
        instanceContainer.setChunkSupplier(LightingChunk::new);

        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();

        //add an event handler to player spawning
        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setSpawningInstance(instanceContainer);
            player.setRespawnPoint(new Pos(0, 42, 0));
        });
        //place block event
        globalEventHandler.addListener(PlayerBlockPlaceEvent.class, event -> {
            event.setCancelled(false);
        });


        //give player pickaxe
        globalEventHandler.addListener(PlayerSpawnEvent.class, event -> {
            Player player = event.getPlayer();
            player.getInventory().addItemStack(ItemStack.of(Material.IRON_PICKAXE));
            player.getInventory().addItemStack(ItemStack.of(Material.GOLD_ORE));
        });


        //gold ore block break event
        globalEventHandler.addListener(PlayerBlockBreakEvent.class, event -> {
            if (event.getBlock() == Block.GOLD_ORE) {
                Player player = event.getPlayer();

                event.getInstance().setBlock(event.getBlockPosition(), Block.BEDROCK);
                player.getInventory().addItemStack(ItemStack.of(Material.RAW_GOLD));

                var message = Component.text("You mined a gold ore and earned", NamedTextColor.WHITE)
                        .append(Component.text(" 1 coin", NamedTextColor.GOLD)
                                .append(Component.text("!", NamedTextColor.WHITE)));

                event.getPlayer().sendMessage(message);
            }
        });


        server.start("0.0.0.0", 25565);
    }
}