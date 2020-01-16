package org.jglrxavpok.moarboats.common.data

import net.minecraft.client.renderer.entity.BoatRenderer
import net.minecraft.entity.item.BoatEntity
import net.minecraft.item.BoatItem
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.ObfuscationReflectionHelper
import net.minecraftforge.registries.GameData
import org.jglrxavpok.moarboats.MoarBoats

interface BoatType {

    companion object {
        private val boatTypes = mutableListOf<BoatType>()
        private val boatCache = mutableMapOf<BoatType, () -> Item>()

        val OAK = createFromVanilla(BoatEntity.Type.OAK)

        fun values(): List<BoatType> {
            return boatTypes
        }

        /**
         * Load all registered items and check which are BoatItem and get their type
         */
        internal fun populateBoatTypeCache() {
            val typeField = ObfuscationReflectionHelper.findField(BoatItem::class.java, "field_185057_a")
            for(boatItem in GameData.getWrapper(Item::class.java).iterator()) {
                if(boatItem is BoatItem) {
                    val boatType = createFromVanilla(typeField[boatItem] as BoatEntity.Type)
                    registerBoatType(boatType) {boatItem}
                }
            }
        }

        fun getTypeFromString(name: String): BoatType {
            return values().first { it.getName() == name }
        }

        // load from BoatRenderer in case the class is modified for modded wood types
        private val textures: Array<ResourceLocation> by lazy { ObfuscationReflectionHelper.findField(BoatRenderer::class.java, "field_110782_f").get(null) as Array<ResourceLocation> }

        fun createFromVanilla(type: BoatEntity.Type): BoatType = object : BoatType {
            override fun getName(): String {
                return type.getName()
            }

            override fun getTexture(): ResourceLocation {
                return textures[type.ordinal]
            }

            override fun getOriginModID(): String {
                return MoarBoats.ModID
            }

            override fun toString(): String {
                return "Vanilla BoatType ${getName()} from ${getOriginModID()}"
            }
        }

        fun registerBoatType(boatType: BoatType, boatItem: () -> Item) {
            boatTypes += boatType
            boatCache[boatType] = boatItem
            MoarBoats.logger.info("Registered boat type $boatType")
        }

        fun getBoatItemFromType(boatType: BoatType) = boatCache[boatType]?.invoke() ?: null

    }

    fun getName(): String
    fun getOriginModID(): String
    fun getTexture(): ResourceLocation
}