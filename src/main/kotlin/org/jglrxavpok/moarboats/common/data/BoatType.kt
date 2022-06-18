package org.jglrxavpok.moarboats.common.data

import net.minecraft.client.renderer.entity.BoatRenderer
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.vehicle.Boat
import net.minecraft.world.item.BoatItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraftforge.fml.util.ObfuscationReflectionHelper
import net.minecraftforge.registries.ForgeRegistries
import org.jglrxavpok.moarboats.MoarBoats

interface BoatType {

    companion object {
        private val boatTypes = mutableListOf<BoatType>()

        val OAK = createFromVanilla(Items.OAK_BOAT, Boat.Type.OAK)

        fun values(): List<BoatType> {
            return boatTypes
        }

        /**
         * Load all registered items and check which are BoatItem and get their type
         */
        internal fun populateBoatTypeCache() {
            val typeField = ObfuscationReflectionHelper.findField(BoatItem::class.java, "field_185057_a")
            for(boatItem in ForgeRegistries.ITEMS.values) {
                if(boatItem is BoatItem) {
                    val boatType = createFromVanilla(boatItem, typeField[boatItem] as Boat.Type)
                    registerBoatType(boatType)
                }
            }
        }

        fun getTypeFromString(name: String): BoatType {
            return values().first { it.getFullName() == name }
        }

        // TODO: server compatible?
        // TODO: access transformer
        // load from BoatRenderer in case the class is modified for modded wood types
        private val textures: Array<ResourceLocation> by lazy { ObfuscationReflectionHelper.findField(BoatRenderer::class.java, "field_110782_f").get(null) as Array<ResourceLocation> }

        fun createFromVanilla(baseItem: Item, type: Boat.Type): BoatType = object : BoatType {
            override fun getFullName(): String {
                return "minecraft_${type.getName()}"
            }

            override fun getShortName(): String {
                return type.getName()
            }

            override fun getTexture(): ResourceLocation {
                return textures[type.ordinal]
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

            override fun provideBoatItem(): Item {
                return baseItem
            }
        }

        fun registerBoatType(boatType: BoatType) {
            boatTypes += boatType
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
     * Returns the boat item corresponding to this boat type
     */
    fun provideBoatItem(): Item
}