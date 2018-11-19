package org.jglrxavpok.moarboats.integration.opencomputers.client

import com.google.common.cache.Cache
import li.cil.oc.api.internal.TextBuffer
import li.cil.oc.api.network.ManagedEnvironment
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiModuleBase
import org.jglrxavpok.moarboats.common.containers.EmptyContainer
import org.jglrxavpok.moarboats.integration.opencomputers.BoatMachineHost
import org.jglrxavpok.moarboats.integration.opencomputers.ComputerModule
import org.jglrxavpok.moarboats.integration.opencomputers.OpenComputerPlugin

class GuiComputerModule(val player: EntityPlayer, boat: IControllable): GuiModuleBase(ComputerModule, boat, player.inventory, EmptyContainer(player.inventory, true)) {
    val host: BoatMachineHost = OpenComputerPlugin.getHost(boat)!!

    override val moduleBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/opencomputer/background.png")

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawScreen(mouseX, mouseY, partialTicks)
        var y = 50
        for(elem in host.internalComponents()) {
            itemRender.renderItemIntoGUI(elem, 120, y)
            y+=20
        }

        GlStateManager.pushMatrix()
        host.buffer.isRenderingEnabled = true
        host.buffer.renderText() ?: fontRenderer.drawString("no screen env :c", 0, y, 0xF0F0F0)
        GlStateManager.popMatrix()

        val ocClass = Class.forName("li.cil.oc.common.ComponentTracker")
        val ocClientClass = Class.forName("li.cil.oc.client.ComponentTracker$")
        val compMethod = ocClass.getDeclaredMethod("components", World::class.java)
        compMethod.isAccessible = true
        val tracker = ocClientClass.getField("MODULE\$").get(null)
        val components: Cache<String, ManagedEnvironment> = compMethod.invoke(tracker, Minecraft.getMinecraft().world) as Cache<String, ManagedEnvironment>

/*        val buffer = (components.getIfPresent("0000-0000-0000-0001")!! as TextBuffer)
        buffer.isRenderingEnabled = true
        fontRenderer.drawString(buffer.renderText().toString()+" "+buffer.renderWidth()+"x"+buffer.renderHeight(), 20, 50, 0xF0F0F0)
        fontRenderer.drawString("addr: ${(components.getIfPresent("0000-0000-0000-0001")!! as TextBuffer)}", 20, 20, 0xF0F0F0)*/
    }
}