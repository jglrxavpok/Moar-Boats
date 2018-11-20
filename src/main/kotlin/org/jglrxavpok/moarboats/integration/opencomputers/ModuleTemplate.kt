package org.jglrxavpok.moarboats.integration.opencomputers

import li.cil.oc.api.Items
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.text.TextComponentTranslation
import net.minecraftforge.oredict.OreDictionary
import org.jglrxavpok.moarboats.common.items.ModularBoatItem
import org.jglrxavpok.moarboats.integration.opencomputers.items.ModuleHolderItem


object ModuleTemplate {

    val caseStack = Items.get("case1").createItemStack(1)
    val gpuStack = OreDictionary.getOres("oc:graphicsCard1")[0]
    val screenStack = Items.get("screen1").createItemStack(1)
    val biosStack = Items.get("luabios").createItemStack(1)
    val osStack = Items.get("openos").createItemStack(1)
    val hddStack = OreDictionary.getOres("oc:hdd3")[0]
    val cpuStack = OreDictionary.getOres("oc:cpu3")[0]
    val ramStack = OreDictionary.getOres("oc:ram5")[0]
    val keyboardStack = Items.get("keyboard").createItemStack(1)

    val noCaseText = TextComponentTranslation("moarboats.opencomputers.assembler.missing.case")
    val noGpuText = TextComponentTranslation("moarboats.opencomputers.assembler.missing.gpu")
    val noScreenText = TextComponentTranslation("moarboats.opencomputers.assembler.missing.screen")
    val noHDDText = TextComponentTranslation("moarboats.opencomputers.assembler.missing.hdd")
    val noBiosText = TextComponentTranslation("moarboats.opencomputers.assembler.missing.bios")
    val noOSText = TextComponentTranslation("moarboats.opencomputers.assembler.missing.os")
    val noCPUText = TextComponentTranslation("moarboats.opencomputers.assembler.missing.cpu")
    val noRAMText = TextComponentTranslation("moarboats.opencomputers.assembler.missing.ram")
    val noKeyboardText = TextComponentTranslation("moarboats.opencomputers.assembler.missing.keyboard")
    val okText = TextComponentTranslation("moarboats.opencomputers.assembler.ok")

    @JvmStatic
    fun select(stack: ItemStack): Boolean {
        return stack.item == ModularBoatItem
    }

    private fun has(inv: IInventory, stack: ItemStack): Boolean {
        for (i in 0 until inv.sizeInventory) {
            val invStack = inv.getStackInSlot(i)
            if(invStack.isItemEqual(stack))
                return true
        }
        return false
    }

    @JvmStatic
    fun validate(inv: IInventory): Array<Any> {
        /*if(!has(inv, caseStack))
            return arrayOf(false, noCaseText)*/
        if(!has(inv, cpuStack))
            return arrayOf(false, noCPUText)
        if(!has(inv, gpuStack))
            return arrayOf(false, noGpuText)
        if(!has(inv, ramStack))
            return arrayOf(false, noRAMText)
      /*  if(!has(inv, screenStack))
            return arrayOf(false, noScreenText)*/
        if(!has(inv, biosStack))
            return arrayOf(false, noBiosText)
        if(!has(inv, osStack))
            return arrayOf(false, noOSText)
        if(!has(inv, hddStack))
            return arrayOf(false, noHDDText)
       /* if(!has(inv, keyboardStack))
            return arrayOf(false, noKeyboardText)*/
        return arrayOf(true, okText)
    }

    @JvmStatic
    fun assemble(inv: IInventory): Array<Any> {
        val stack =
            if(validate(inv)[0] as Boolean) {
                ItemStack(ModuleHolderItem) // TODO: NBT data
            } else {
                ItemStack.EMPTY
            }
        return arrayOf(stack)
    }
}