package org.jglrxavpok.moarboats.server

import net.alexwells.kottle.KotlinEventBusSubscriber
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.network.PacketDistributor
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.MoarBoatsProxy
import org.jglrxavpok.moarboats.common.items.ItemGoldenTicket
import org.jglrxavpok.moarboats.common.network.SSetGoldenItinerary

@KotlinEventBusSubscriber(modid = MoarBoats.ModID, value = [Dist.DEDICATED_SERVER])
object ServerEvents: MoarBoatsProxy() {

    @SubscribeEvent
    fun onPlayerUpdate(event: LivingEvent.LivingUpdateEvent) {
        val player = event.entityLiving as? EntityPlayerMP ?: return
        for (i in 0 until player.inventory.sizeInventory) {
            val itemstack = player.inventory.getStackInSlot(i)

            if (!itemstack.isEmpty && itemstack.item == ItemGoldenTicket) {
                if(!ItemGoldenTicket.isEmpty(itemstack)) {
                    if(player.ticksExisted % 5 == 0) { // send every 5 ticks
                        val data = ItemGoldenTicket.getData(itemstack)
                        MoarBoats.network.send(PacketDistributor.PLAYER.with { player }, SSetGoldenItinerary(data))
                    }
                }
            }
        }
    }
}