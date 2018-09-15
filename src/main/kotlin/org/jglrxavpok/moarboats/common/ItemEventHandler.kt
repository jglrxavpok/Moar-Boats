package org.jglrxavpok.moarboats.common

import net.minecraft.entity.EntityLeashKnot
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.items.ItemGoldenItinerary
import org.jglrxavpok.moarboats.common.items.ItemMapWithPath
import org.jglrxavpok.moarboats.common.items.RopeItem
import org.jglrxavpok.moarboats.common.items.UpgradeToGoldenItineraryRecipe

@Mod.EventBusSubscriber(modid = MoarBoats.ModID)
object ItemEventHandler {

    @SubscribeEvent
    fun onEntityInteract(event: PlayerInteractEvent.EntityInteract) {
        val player = event.entityPlayer
        val stack = event.itemStack
        if(stack.item == RopeItem && event.target is EntityLeashKnot) {
            if(RopeItem.getState(stack) == RopeItem.State.WAITING_NEXT) {
                event.isCanceled = true
                event.cancellationResult = RopeItem.onEntityInteract(player, stack, event.target)
            }
        }
    }

}