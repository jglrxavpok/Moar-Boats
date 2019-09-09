package org.jglrxavpok.moarboats.common.network

import net.minecraft.block.material.MaterialColor
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.chunk.ChunkStatus
import net.minecraft.world.dimension.DimensionType
import net.minecraft.world.storage.MapData
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.network.NetworkEvent
import net.minecraftforge.fml.network.PacketDistributor
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.common.modules.HelmModule.StripeLength
import kotlin.concurrent.thread

class CMapImageRequest(): MoarBoatsPacket {

    var mapName: String = ""

    constructor(name: String): this() {
        this.mapName = name
    }

    object Handler: MBMessageHandler<CMapImageRequest, MoarBoatsPacket?> {
        override val packetClass = CMapImageRequest::class.java
        override val receiverSide = Dist.DEDICATED_SERVER

        override fun onMessage(message: CMapImageRequest, ctx: NetworkEvent.Context): MoarBoatsPacket? {
            val player = ctx.sender!!
            val world = player.level
            val mapData = world.getMapData(message.mapName) as? MapData ?: return null
            val size = (1 shl mapData.scale.toInt())*128
            val stripes = size/ StripeLength

            repeat(stripes) { index ->
                thread {
                    val textureData = takeScreenshotOfMapArea(index, mapData, world)
                    MoarBoats.network.send(PacketDistributor.PLAYER.with { player }, SMapImageAnswer(message.mapName, index, textureData))
                }
            }
            return null
        }

        private fun takeScreenshotOfMapArea(stripeIndex: Int, mapData: MapData, world: World): IntArray {
            val xCenter = mapData.x
            val zCenter = mapData.z
            val size = (1 shl mapData.scale.toInt())*128
            val textureData = IntArray(size* StripeLength)
            val minX = xCenter-size/2
            val minZ = zCenter-size/2+stripeIndex* StripeLength

            val maxX = xCenter+size/2-1
            val maxZ = minZ+ StripeLength -1

            val blockPos = BlockPos.PooledMutableBlockPos.acquire()
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
                        getMapColor(MaterialColor.MATERIAL_COLORS[j / 4], j and 3)
                    }
                    val chunk = try {
                        world.chunkSource.getChunk(chunkX, chunkZ, ChunkStatus.FULL, false)
                    } catch(e: Exception) {
                        e.printStackTrace()
                        null
                    }
                    if(j / 4 == 0)
                        continue // let a transparent pixel here

                    textureData[pixelZ*size+pixelX] = 0xFF000000.toInt() or mapColor

                    if(chunk == null)
                        continue

                    for(y in world.height downTo 0) {
                        blockPos.set(x, y, z)
                        val blockState = chunk.getBlockState(blockPos)
                        val color = blockState.getMapColor(world, blockPos)
                        if(color != MaterialColor.NONE) {
                            textureData[pixelZ*size+pixelX] = (color.col) or 0xFF000000.toInt()

                            if(blockState.material.isLiquid) {
                                var depth = 0
                                while(true) {
                                    depth++
                                    blockPos.y--
                                    val blockBelow = world.getBlockState(blockPos)
                                    if( !blockBelow.material.isLiquid) {
                                        textureData[pixelZ*size+pixelX] = (reduceBrightness(color.col, depth)) or 0xFF000000.toInt()
                                        break
                                    }
                                }
                            }
                            break
                        }

                    }
                }
            }
            blockPos.close()
            return textureData
        }

        private fun getMapColor(mapColor: MaterialColor, index: Int): Int {
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

            val j = (mapColor.col shr 16 and 255) * i / 255
            val k = (mapColor.col shr 8 and 255) * i / 255
            val l = (mapColor.col and 255) * i / 255
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