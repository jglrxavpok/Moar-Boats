package org.jglrxavpok.moarboats.common.data

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.vehicle.Boat
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraftforge.fml.ModList
import net.minecraftforge.registries.ForgeRegistries
import org.jglrxavpok.moarboats.MoarBoats

interface BoatType {

    companion object {
        val OAK = createFromVanilla(Boat.Type.OAK)

        private val boatTypes = mutableListOf<BoatType>()

        fun values(): List<BoatType> {
            if(boatTypes.isEmpty()) {
                boatTypes += populateBoatTypeCache()
            }
            return boatTypes
        }

        /**
         * For each Boat.Type (Minecraft), see if there is a corresponding <type>_boat item (with any modid)
         */
        internal fun populateBoatTypeCache(): List<BoatType> {
            val result = mutableListOf<BoatType>()
            result += OAK
            for(minecraftBoatType in Boat.Type.values()) {
                if(minecraftBoatType == Boat.Type.OAK)
                    continue
                val boatType = createFromVanilla(minecraftBoatType)
                registerBoatType(result, boatType)
            }
            return result
        }

        fun getTypeFromString(name: String): BoatType {
            return values().first { it.getFullName() == name }
        }

        fun createFromVanilla(type: Boat.Type): BoatType = object : BoatType {
            private val baseItem by lazy {
                ModList.get().mods.firstNotNullOfOrNull {
                    val itemID = ResourceLocation(it.modId, "${type.getName()}_boat")
                    val item = ForgeRegistries.ITEMS.getValue(itemID)
                    if(item != Items.AIR) {
                        return@firstNotNullOfOrNull item
                    }
                    return@firstNotNullOfOrNull null
                }
            }

            override fun getFullName(): String {
                return "minecraft_${type.getName()}"
            }

            override fun getShortName(): String {
                return type.getName()
            }

            override fun getTexture(): ResourceLocation {
                return ResourceLocation("minecraft", "textures/entity/boat/${type.getName()}.png")
            }

            override fun getOriginModID(): String {
                return MoarBoats.ModID
            }

            override fun getBaseBoatOriginModID(): String {
                return "minecraft"
            }

            override fun toString(): String {
                return "Vanilla BoatType ${getFullName()} from ${getOriginModID()}"
            }

            override fun equals(other: Any?): Boolean {
                if(other is BoatType) {
                    return BoatType.checkEqual(this, other)
                }
                return super.equals(other)
            }

            override fun provideBoatItem(): Item? {
                return baseItem
            }
        }

        fun registerBoatType(target: MutableList<BoatType>, boatType: BoatType) {
            target += boatType
            MoarBoats.logger.info("Registered boat type $boatType")
        }

        fun checkEqual(typeA: BoatType, typeB: BoatType): Boolean {
            return typeA.getFullName() == typeB.getFullName() && typeA.getOriginModID() == typeB.getOriginModID()
        }

    }

    /**
     * Full name of this boat type: modid+type name (eg: minecraft_oak, biomesoplenty_fir, etc.)
     */
    fun getFullName(): String

    /**
     * Short name of this boat type (eg: oak, fir, birch, etc.)
     */
    fun getShortName(): String

    fun getOriginModID(): String
    fun getBaseBoatOriginModID(): String
    fun getTexture(): ResourceLocation

    /**
     * Returns the boat item corresponding to this boat type. May return null if none found.
     */
    fun provideBoatItem(): Item?
}