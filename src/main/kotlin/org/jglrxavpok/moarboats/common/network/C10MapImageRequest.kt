package org.jglrxavpok.moarboats.common.network

import io.netty.buffer.ByteBuf
import net.minecraft.block.material.MapColor
import net.minecraft.item.ItemMap
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.storage.MapData
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.entities.ModularBoatEntity
import org.jglrxavpok.moarboats.api.BoatModuleRegistry
import org.jglrxavpok.moarboats.client.gui.GuiPathEditor
import org.jglrxavpok.moarboats.common.modules.HelmModule.StripeLength
import kotlin.concurrent.thread

class C10MapImageRequest(): IMessage {

    var mapName: String = ""

    constructor(name: String): this() {
        this.mapName = name
    }

    override fun fromBytes(buf: ByteBuf) {
        mapName = ByteBufUtils.readUTF8String(buf)
    }

    override fun toBytes(buf: ByteBuf) {
        ByteBufUtils.writeUTF8String(buf, mapName)
    }

    object Handler: MBMessageHandler<C10MapImageRequest, IMessage?> {
        override val packetClass = C10MapImageRequest::class
        override val receiverSide = Side.SERVER

        override fun onMessage(message: C10MapImageRequest, ctx: MessageContext): IMessage? {
            val player = ctx.serverHandler.player
            val world = player.world
            val mapData = world.loadData(MapData::class.java, message.mapName) as MapData
            val size = (1 shl mapData.scale.toInt())*128
            val stripes = size/ StripeLength

            repeat(stripes) { index ->
                thread {
                    val textureData = takeScreenshotOfMapArea(index, mapData, world)
                    MoarBoats.network.sendTo(S11MapImageAnswer(message.mapName, index, textureData), player)
                }
            }
            return null
        }

        private fun takeScreenshotOfMapArea(stripeIndex: Int, mapData: MapData, world: World): IntArray {
            val xCenter = mapData.xCenter
            val zCenter = mapData.zCenter
            val size = (1 shl mapData.scale.toInt())*128
            val textureData = IntArray(size* StripeLength)
            val minX = xCenter-size/2
            val minZ = zCenter-size/2+stripeIndex* StripeLength

            val maxX = xCenter+size/2-1
            val maxZ = minZ+ StripeLength -1

            val blockPos = BlockPos.PooledMutableBlockPos.retain()
            for(z in minZ..maxZ) {
                for(x in minX..maxX) {
                    val pixelX = x-minX
                    val pixelZ = z-minZ

                    val chunkX = x shr 4
                    val chunkZ = z shr 4

                    val mapZ = (((pixelZ+stripeIndex* StripeLength) / size.toDouble()) * 128.0).toInt()
                    val mapX = ((pixelX / size.toDouble()) * 128.0).toInt()
                    val i = mapZ*128+mapX
                    val j = mapData.colors[i].toInt() and 0xFF
                    val mapColor = if (j / 4 == 0) {
                        (i + i / 128 and 1) * 8 + 16 shl 24
                    } else {
                        getMapColor(MapColor.COLORS[j / 4], j and 3)
                    }
                    val chunk = try {
                        world.chunkProvider.getLoadedChunk(chunkX, chunkZ)
                    } catch(e: Exception) {
                        e.printStackTrace()
                        null
                    }
                    if(j / 4 == 0)
                        continue // let a transparent pixel here

                    textureData[pixelZ*size+pixelX] = 0xFF000000.toInt() or mapColor

                    if(chunk == null)
                        continue

                    for(y in world.actualHeight downTo 0) {
                        blockPos.setPos(x, y, z)
                        val blockState = chunk.getBlockState(blockPos)
                        val color = blockState.getMapColor(world, blockPos)
                        if(color != MapColor.AIR) {
                            textureData[pixelZ*size+pixelX] = (color.colorValue) or 0xFF000000.toInt()

                            if(blockState.material.isLiquid) {
                                var depth = 0
                                while(true) {
                                    depth++
                                    blockPos.y--
                                    val blockBelow = world.getBlockState(blockPos)
                                    if( !blockBelow.material.isLiquid) {
                                        textureData[pixelZ*size+pixelX] = (reduceBrightness(color.colorValue, depth)) or 0xFF000000.toInt()
                                        break
                                    }
                                }
                            }
                            break
                        }

                    }
                }
            }
            blockPos.release()
            return textureData
        }

        private fun getMapColor(mapColor: MapColor, index: Int): Int {
            var i = 220

            if (index == 3) {
                i = 135
            }

            if (index == 2) {
                i = 255
            }

            if (index == 1) {
                i = 220
            }

            if (index == 0) {
                i = 180
            }

            val j = (mapColor.colorValue shr 16 and 255) * i / 255
            val k = (mapColor.colorValue shr 8 and 255) * i / 255
            val l = (mapColor.colorValue and 255) * i / 255
            return -16777216 or (j shl 16) or (k shl 8) or l
        }

        private fun reduceBrightness(rgbColor: Int, depth: Int): Int {
            if(depth == 1)
                return rgbColor
            val red = (rgbColor shr 16) and 0xFF
            val green = (rgbColor shr 8) and 0xFF
            val blue = rgbColor and 0xFF

            val correctedRed = red/depth *2
            val correctedGreen = green/depth *2
            val correctedBlue = blue/depth *2

            return (correctedRed shl 16) or (correctedGreen shl 8) or correctedBlue
        }
    }
}