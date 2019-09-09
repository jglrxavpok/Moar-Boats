package org.jglrxavpok.moarboats.common

import net.minecraft.entity.LeashKnotEntity
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.items.RopeItem

@Mod.EventBusSubscriber(modid = MoarBoats.ModID)
class ItemEventHandler {

    companion object {
        @JvmStatic
        @SubscribeEvent
        fun onEntityInteract(event: PlayerInteractEvent.EntityInteract) {
            val player = event.PlayerEntity
            val stack = event.itemStack
            if(stack.item == RopeItem && event.target is LeashKnotEntity) {
                if(RopeItem.getState(stack) == RopeItem.State.WAITING_NEXT) {
                    event.isCanceled = true
                    event.cancellationResult = RopeItem.onEntityInteract(player, stack, event.target)
                }
            }
        }
    }

}