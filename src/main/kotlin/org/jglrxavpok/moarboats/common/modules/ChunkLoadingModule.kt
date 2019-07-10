package org.jglrxavpok.moarboats.common.modules

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Particles
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiNoConfigModule
import org.jglrxavpok.moarboats.common.MoarBoatsConfig
import org.jglrxavpok.moarboats.common.containers.EmptyContainer
import org.jglrxavpok.moarboats.common.items.ChunkLoaderItem
import org.jglrxavpok.moarboats.extensions.toRadians

object ChunkLoadingModule: BoatModule() {
    override val id = ResourceLocation(MoarBoats.ModID, "chunk_loading")

    override val usesInventory = false
    override val moduleSpot = Spot.Misc
    override val isMenuInteresting = false

    private val corners = arrayOf(
            Pair(-1, -1),
            Pair(-1, 1),
            Pair(1, 1),
            Pair(1, -1)
    )

    override fun onInteract(from: IControllable, player: EntityPlayer, hand: EnumHand, sneaking: Boolean) = false

    override fun controlBoat(from: IControllable) { }

    override fun update(from: IControllable) {
        if(!MoarBoatsConfig.chunkLoader.allowed.get())
            return
        forceChunks(from)


        if(!from.world.isRemote)
            return
        val yaw = (from.yaw+90f).toRadians().toDouble()//Math.toRadians(from.yaw.toDouble())
        val width = .0625f * 15f
        val length = 0.5f
        val world = from.worldRef
        for ((x, z) in corners) {
            val posX = from.positionX + x * width * Math.cos(yaw) + z * length * Math.sin(yaw)
            val posZ = from.positionZ + x * width * Math.sin(yaw) - z * length * Math.cos(yaw)
            val posY = from.positionY
            val vx = (Math.random() * 2 -1) * 0.2
            val vy = 0.3
            val vz = (Math.random() * 2 -1) * 0.2
            world.addParticle(Particles.PORTAL, posX, posY, posZ, vx, vy, vz)
        }
    }

    private fun forceChunks(boat: IControllable) {
        /*boat.chunkTicket?.let {

            val centerPos = ChunkPos(boat.correspondingEntity.chunkCoordX, boat.correspondingEntity.chunkCoordZ)
            for(i in -1..1) {
                for(j in -1..1) {
                    val pos = ChunkPos(centerPos.x+i, centerPos.z+j)
                    ForgeChunkManager.forceChunk(it, pos)
                }
            }
        } FIXME */
    }

    override fun onInit(to: IControllable, fromItem: ItemStack?) {
        super.onInit(to, fromItem)
        if(!MoarBoatsConfig.chunkLoader.allowed.get())
            return
        forceChunks(to)
    }

    override fun onAddition(to: IControllable) { }

    override fun createContainer(player: EntityPlayer, boat: IControllable) = EmptyContainer(player.inventory)

    override fun createGui(player: EntityPlayer, boat: IControllable) = GuiNoConfigModule(player.inventory, this, boat)

    override fun dropItemsOnDeath(boat: IControllable, killedByPlayerInCreative: Boolean) {
        if(!killedByPlayerInCreative)
            boat.correspondingEntity.entityDropItem(ChunkLoaderItem, 1)
    }
}