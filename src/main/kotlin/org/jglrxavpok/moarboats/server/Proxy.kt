package org.jglrxavpok.moarboats.server

import net.minecraft.entity.player.EntityPlayerMP
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.MoarBoatsProxy
import org.jglrxavpok.moarboats.common.items.ItemGoldenTicket
import org.jglrxavpok.moarboats.common.network.SSetGoldenItinerary

class Proxy: MoarBoatsProxy() {


    override fun preInit() {
        super.preInit()
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun onPlayerUpdate(event: LivingEvent.LivingUpdateEvent) {
        val player = event.entityLiving as? EntityPlayerMP ?: return
        for (i in 0 until player.inventory.sizeInventory) {
            val itemstack = player.inventory.getStackInSlot(i)

            if (!itemstack.isEmpty && itemstack.item == ItemGoldenTicket) {
                if(!ItemGoldenTicket.isEmpty(itemstack)) {
                    if(player.ticksExisted % 5 == 0) { // send every 5 ticks
                        val data = ItemGoldenTicket.getData(itemstack)
                        MoarBoats.network.sendTo(SSetGoldenItinerary(data), player)
                    }
                }
            }
        }
    }
}