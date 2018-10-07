package org.jglrxavpok.moarboats.common.modules

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.ChunkPos
import net.minecraftforge.common.ForgeChunkManager
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.BoatModule
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiNoConfigModule
import org.jglrxavpok.moarboats.common.containers.EmptyContainer

object ChunkLoadingModule: BoatModule() {
    override val id = ResourceLocation(MoarBoats.ModID, "chunk_loading")

    override val usesInventory = false
    override val moduleSpot = Spot.Misc

    override fun onInteract(from: IControllable, player: EntityPlayer, hand: EnumHand, sneaking: Boolean) = false

    override fun controlBoat(from: IControllable) { }

    override fun update(from: IControllable) {
        forceChunks(from)
    }

    private fun forceChunks(boat: IControllable) {
        boat.chunkTicket?.let {

            val centerPos = ChunkPos(boat.correspondingEntity.chunkCoordX, boat.correspondingEntity.chunkCoordZ)
            for(i in -1..1) {
                for(j in -1..1) {
                    val pos = ChunkPos(centerPos.x+i, centerPos.z+j)
                    ForgeChunkManager.forceChunk(it, pos)
                }
            }
        }
    }

    override fun onInit(to: IControllable, fromItem: ItemStack?) {
        super.onInit(to, fromItem)
        forceChunks(to)
    }

    override fun onAddition(to: IControllable) { }

    override fun createContainer(player: EntityPlayer, boat: IControllable) = EmptyContainer(player.inventory)

    override fun createGui(player: EntityPlayer, boat: IControllable) = GuiNoConfigModule(player.inventory, this, boat)
}