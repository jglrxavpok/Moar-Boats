package org.jglrxavpok.moarboats.integration.biomesoplenty

import biomesoplenty.common.entity.item.BoatEntityBOP
import net.minecraft.data.DataGenerator
import net.minecraft.item.Item
import net.minecraft.resources.ResourcePackType
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.generators.ExistingFileHelper
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent
import net.minecraftforge.registries.GameData
import org.jglrxavpok.moarboats.common.data.BoatType
import org.jglrxavpok.moarboats.datagen.JsonModelGenerator
import org.jglrxavpok.moarboats.integration.MoarBoatsIntegration
import org.jglrxavpok.moarboats.integration.MoarBoatsPlugin

@MoarBoatsIntegration("biomesoplenty")
class BOPPlugin: MoarBoatsPlugin {

    override fun populateBoatTypes() {
        BoatEntityBOP.Type.values().forEach {
            val name = it.getName()
            val item = { GameData.getWrapper(Item::class.java).getValue(ResourceLocation("biomesoplenty", name+"_boat"))!!.get() }
            val type = createFromBOP(it, item)
            BoatType.registerBoatType(type)
        }
    }

    private fun createFromBOP(type: BoatEntityBOP.Type, itemProvider: () -> Item): BoatType {
        return object: BoatType {
            override fun getFullName(): String {
                return "biomesoplenty_${type.getName()}"
            }

            override fun getShortName(): String {
                return type.getName()
            }

            override fun getOriginModID(): String {
                return "biomesoplenty"
            }

            override fun getBaseBoatOriginModID(): String {
                return "biomesoplenty"
            }

            override fun getTexture(): ResourceLocation {
                return ResourceLocation("biomesoplenty:textures/entity/boat/${getShortName()}.png")
            }

            override fun toString(): String {
                return "Biomes'O'Plenty BoatType ${getFullName()} from ${getOriginModID()}"
            }

            override fun equals(other: Any?): Boolean {
                if(other is BoatType) {
                    return BoatType.checkEqual(this, other)
                }
                return super.equals(other)
            }

            override fun provideBoatItem(): Item {
                return itemProvider()
            }
        }
    }

    override fun registerProviders(event: GatherDataEvent, generator: DataGenerator, existingFileHelper: ExistingFileHelper) {
        val fileHelper = object: ExistingFileHelper(emptyList(), true) {
            override fun exists(loc: ResourceLocation?, type: ResourcePackType?, pathSuffix: String?, pathPrefix: String?): Boolean {
                if(loc?.namespace == "biomesoplenty")
                    return true
                return existingFileHelper.exists(loc, type, pathSuffix, pathPrefix)
            }
        }
        generator.addProvider(JsonModelGenerator(generator, "biomesoplenty", "biomesoplenty", fileHelper))
    }
}