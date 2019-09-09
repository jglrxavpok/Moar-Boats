package org.jglrxavpok.moarboats.server

import net.alexwells.kottle.KotlinEventBusSubscriber
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.network.PacketDistributor
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.MoarBoatsPacketList
import org.jglrxavpok.moarboats.common.items.ItemGoldenTicket
import org.jglrxavpok.moarboats.common.network.SSetGoldenItinerary

@KotlinEventBusSubscriber(modid = MoarBoats.ModID, value = [Dist.DEDICATED_SERVER])
object ServerEvents {

    @SubscribeEvent
    fun onPlayerUpdate(event: LivingEvent.LivingUpdateEvent) {
        val player = event.entityLiving as? ServerPlayerEntity ?: return
        for (i in 0 until player.inventory.containerSize) {
            val itemstack = player.inventory.getItem(i)

            if (!itemstack.isEmpty && itemstack.item == ItemGoldenTicket) {
                if(!ItemGoldenTicket.isEmpty(itemstack)) {
                    if(player.tickCount % 5 == 0) { // send every 5 ticks
                        val data = ItemGoldenTicket.getData(itemstack)
                        MoarBoats.network.send(PacketDistributor.PLAYER.with { player }, SSetGoldenItinerary(data))
                    }
                }
            }
        }
    }
}