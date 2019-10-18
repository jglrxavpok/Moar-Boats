package org.jglrxavpok.moarboats.common.network

import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.ListNBT
import net.minecraftforge.fml.network.NetworkEvent
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.HelmModule

class SUpdateMapWithPathInBoat: SxxUpdateMapWithPath {

    var boatID: Int = 0

    constructor()

    constructor(list: ListNBT, boatID: Int): super(list) {
        this.boatID = boatID
    }

    object Handler: SxxUpdateMapWithPath.Handler<SUpdateMapWithPathInBoat>() {
        override val packetClass = SUpdateMapWithPathInBoat::class.java

        override fun updatePath(message: SUpdateMapWithPathInBoat, ctx: NetworkEvent.Context, list: ListNBT) {
            with(message) {
                val player = Minecraft.getInstance().player
                val level = player.world
                val boat = level.getEntityByID(message.boatID) as? ModularBoatEntity ?: return
                val stack = boat.getInventory(HelmModule).getStackInSlot(0)
                if(stack.tag == null) {
                    stack.tag = CompoundNBT()
                }
                stack.tag!!.put("${MoarBoats.ModID}.path", list)
            }
        }

    }
}