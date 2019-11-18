package org.jglrxavpok.moarboats.server

import net.alexwells.kottle.KotlinEventBusSubscriber
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.ListNBT
import net.minecraft.world.server.ServerWorld
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.network.PacketDistributor
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.EntityEntries
import org.jglrxavpok.moarboats.common.data.ForcedChunks
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.items.ItemGoldenTicket
import org.jglrxavpok.moarboats.common.modules.ChunkLoadingModule
import org.jglrxavpok.moarboats.common.network.SSetGoldenItinerary
import org.jglrxavpok.moarboats.extensions.getEntities

object ServerEvents {

    @SubscribeEvent
    fun onPlayerUpdate(event: LivingEvent.LivingUpdateEvent) {
        val player = event.entityLiving as? ServerPlayerEntity ?: return
        for(i in 0 until player.inventory.sizeInventory) {
            val itemstack = player.inventory.getStackInSlot(i)

            if(!itemstack.isEmpty && itemstack.item == ItemGoldenTicket) {
                if(!ItemGoldenTicket.isEmpty(itemstack)) {
                    if(player.ticksExisted % 5 == 0) { // send every 5 ticks
                        val data = ItemGoldenTicket.getData(itemstack)
                        MoarBoats.network.send(PacketDistributor.PLAYER.with {player}, SSetGoldenItinerary(data))
                    }
                }
            }
        }
    }

    @SubscribeEvent
    fun onWorldLoad(event: WorldEvent.Load) {
        val world = (event.world as? ServerWorld) ?: return
        val chunks = world.savedData.getOrCreate({ForcedChunkList(ListNBT())}, "moarboats_forced_chunks")
        for(nbt in chunks.list) {
            nbt as CompoundNBT
            val chunks = ForcedChunks(world)
            chunks.read(nbt)
            chunks.forceAfterWorldLoad()
        }
    }

    @SubscribeEvent
    fun onWorldSave(event: WorldEvent.Save) {
        val world = (event.world as? ServerWorld) ?: return
        val boats = world.getEntities<ModularBoatEntity>(EntityEntries.ModularBoat) { ChunkLoadingModule in (it as ModularBoatEntity).modules }.map { it as ModularBoatEntity }
        val nbtList = ListNBT()
        boats.forEach {
            nbtList.add(it.forcedChunks.write(CompoundNBT()))
        }

        world.savedData.set(ForcedChunkList(nbtList))
        world.savedData.save()
    }
}
