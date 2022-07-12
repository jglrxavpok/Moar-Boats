package org.jglrxavpok.moarboats.common.network

import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraftforge.network.NetworkEvent
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.HelmModule

class SUpdateMapWithPathInBoat: SxxUpdateMapWithPath {

    var boatID: Int = 0

    constructor(): super(false)

    constructor(list: ListTag, boatID: Int): super(list, false) {
        this.boatID = boatID
    }

    object Handler: SxxUpdateMapWithPath.Handler<SUpdateMapWithPathInBoat>() {
        override val packetClass = SUpdateMapWithPathInBoat::class.java

        override fun updatePath(message: SUpdateMapWithPathInBoat, ctx: NetworkEvent.Context, list: ListTag) {
            with(message) {
                val player = Minecraft.getInstance().player
                val level = player!!.level
                val boat = level.getEntity(message.boatID) as? ModularBoatEntity ?: return
                val stack = boat.getInventory(HelmModule).getItem(0)
                if(stack.tag == null) {
                    stack.tag = CompoundTag()
                }
                stack.tag!!.put("${MoarBoats.ModID}.path", list)
            }
        }

    }
}