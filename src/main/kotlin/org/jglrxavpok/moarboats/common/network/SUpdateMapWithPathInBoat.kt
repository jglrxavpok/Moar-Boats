package org.jglrxavpok.moarboats.common.network

import net.minecraft.client.Minecraft
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraftforge.fml.network.NetworkEvent
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.common.modules.HelmModule

class SUpdateMapWithPathInBoat: SxxUpdateMapWithPath {

    var boatID: Int = 0

    constructor()

    constructor(list: NBTTagList, boatID: Int): super(list) {
        this.boatID = boatID
    }

    object Handler: SxxUpdateMapWithPath.Handler<SUpdateMapWithPathInBoat>() {
        override val packetClass = SUpdateMapWithPathInBoat::class.java

        override fun updatePath(message: SUpdateMapWithPathInBoat, ctx: NetworkEvent.Context, list: NBTTagList) {
            with(message) {
                val player = Minecraft.getInstance().player
                val world = player.world
                val boat = world.getEntityByID(message.boatID) as? ModularBoatEntity ?: return
                val stack = boat.getInventory(HelmModule).getStackInSlot(0)
                if(stack.tag == null) {
                    stack.tag = NBTTagCompound()
                }
                stack.tag!!.put("${MoarBoats.ModID}.path", list)
            }
        }

    }
}