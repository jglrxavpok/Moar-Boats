package org.jglrxavpok.moarboats.server

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.event.level.LevelEvent
import net.minecraftforge.event.server.ServerStartingEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.network.PacketDistributor
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.EntityEntries
import org.jglrxavpok.moarboats.common.data.ForcedChunks
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.items.ItemGoldenTicket
import org.jglrxavpok.moarboats.common.modules.ChunkLoadingModule
import org.jglrxavpok.moarboats.common.network.SSetGoldenItinerary

object ServerEvents {

    @SubscribeEvent
    fun onPlayerUpdate(event: LivingEvent.LivingTickEvent) {
        val player = event.entity as? ServerPlayer ?: return
        for(i in 0 until player.inventory.containerSize) {
            val itemstack = player.inventory.getItem(i)

            if(!itemstack.isEmpty && itemstack.item is ItemGoldenTicket) {
                if(!ItemGoldenTicket.isEmpty(itemstack)) {
                    if(player.tickCount % 5 == 0) { // send every 5 ticks
                        val data = ItemGoldenTicket.getData(itemstack)
                        MoarBoats.network.send(PacketDistributor.PLAYER.with {player}, SSetGoldenItinerary(data, false))
                    }
                }
            }
        }
    }

    @SubscribeEvent
    fun onWorldLoad(event: LevelEvent.Load) {
        val world = (event.level as? ServerLevel) ?: return
        val chunks = world.dataStorage.computeIfAbsent(::ForcedChunkList, ::ForcedChunkList, ForcedChunkList.getId())
        for(nbt in chunks.list) {
            nbt as CompoundTag
            val chunks = ForcedChunks(world)
            chunks.read(nbt)
            chunks.forceAfterWorldLoad()
        }
    }

    @SubscribeEvent
    fun onWorldSave(event: LevelEvent.Save) {
        val world = (event.level as? ServerLevel) ?: return
        val boats = world.getEntities<ModularBoatEntity>(EntityEntries.ModularBoat.get()) { ChunkLoadingModule in (it as ModularBoatEntity).modules }.map { it as ModularBoatEntity }
        val nbtList = ListTag()
        boats.forEach {
            nbtList.add(it.forcedChunks.write(CompoundTag()))
        }

        world.dataStorage.set(ForcedChunkList.getId(), ForcedChunkList(nbtList))
        world.dataStorage.save()
    }

    @SubscribeEvent
    fun initDedicatedServer(event: ServerStartingEvent) {
        MoarBoats.dedicatedServerInstance = event.server
    }
}
