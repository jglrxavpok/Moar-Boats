package org.jglrxavpok.moarboats.common

import net.minecraft.world.entity.decoration.LeashFenceKnotEntity
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.items.RopeItem

@Mod.EventBusSubscriber(modid = MoarBoats.ModID, bus = Mod.EventBusSubscriber.Bus.MOD)
object ItemEventHandler {

    // TODO: fix -> not called
    @JvmStatic
    @SubscribeEvent
    fun onEntityInteract(event: PlayerInteractEvent.EntityInteract) {
        val player = event.player
        val stack = event.itemStack
        if(stack.item is RopeItem && event.target is LeashFenceKnotEntity) {
            if(RopeItem.getState(stack) == RopeItem.State.WAITING_NEXT) {
                event.isCanceled = true
                event.cancellationResult = RopeItem.onEntityInteract(player, stack, event.target)
            }
        }
    }

}