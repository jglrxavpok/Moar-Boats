package org.jglrxavpok.moarboats.integration.opencomputers.client

import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiButtonImage
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import org.jglrxavpok.moarboats.MoarBoats
import org.jglrxavpok.moarboats.api.IControllable
import org.jglrxavpok.moarboats.client.gui.GuiModuleBase
import org.jglrxavpok.moarboats.common.containers.EmptyContainer
import org.jglrxavpok.moarboats.integration.opencomputers.BoatMachineHost
import org.jglrxavpok.moarboats.integration.opencomputers.ComputerModule
import org.jglrxavpok.moarboats.integration.opencomputers.OpenComputersPlugin
import org.jglrxavpok.moarboats.integration.opencomputers.network.CPingComputer
import org.jglrxavpok.moarboats.integration.opencomputers.network.CTurnOnOffComputer
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11

class GuiComputerModule(val player: EntityPlayer, boat: IControllable): GuiModuleBase(ComputerModule, boat, player.inventory, EmptyContainer(player.inventory, true, 27)) {
    val host: BoatMachineHost = OpenComputersPlugin.getHost(boat)!!

    init {
        renderPlayerInventoryName = false
        renderPlayerInventoryName = false
    }

    override val moduleBackground = ResourceLocation(MoarBoats.ModID, "textures/gui/opencomputer/background.png")
    private val pressedKeys = mutableMapOf<Int, Char>()
    private val turnOffButton = GuiButtonImage(-1,0,0,24,26,214, 136,26, moduleBackground)
    private val turnOnButton = GuiButtonImage(-2,0,0,24,26,214, 188,26, moduleBackground)
    private var machineRunning = false

    override fun initGui() {
        super.initGui()
        turnOnButton.setPosition(guiLeft-turnOnButton.width+3, guiTop+23+5)
        turnOffButton.setPosition(guiLeft-turnOffButton.width+3, guiTop+23+5)
        addButton(turnOnButton)
        addButton(turnOffButton)

        turnOffButton.visible = machineRunning
        turnOnButton.visible = !turnOffButton.visible
    }

    override fun updateScreen() {
        super.updateScreen()
        if(boat.correspondingEntity.ticksExisted % 10 == 0) { // ping every 0.5s
            MoarBoats.network.sendToServer(CPingComputer(boat.entityID))
        }
    }

    override fun actionPerformed(button: GuiButton?) {
        super.actionPerformed(button)
        if(button == turnOnButton) {
            MoarBoats.network.sendToServer(CTurnOnOffComputer(boat.entityID, turnOn = true))
        } else if(button == turnOffButton) {
            MoarBoats.network.sendToServer(CTurnOnOffComputer(boat.entityID, turnOn = false))
        }
    }

    override fun renderBackground() {}

    override fun computeSizeX(): Int {
        return 214
    }

    override fun computeSizeY(): Int {
        return 222
    }

    override fun drawModuleForeground(mouseX: Int, mouseY: Int) {
        super.drawModuleForeground(mouseX, mouseY)
        GlStateManager.disableLighting()

        GlStateManager.pushMatrix()
        val tess = Tessellator.getInstance()
        val buffer = tess.buffer
        val w = host.buffer.renderWidth().toDouble()
        val h = host.buffer.renderHeight().toDouble()
        GlStateManager.translate(7f, 7f, 0f)

        GlStateManager.disableTexture2D()
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)
        buffer.pos(0.0, h, 0.0).color(0,0,0,255).endVertex()
        buffer.pos(w, h, 0.0).color(0,0,0,255).endVertex()
        buffer.pos(w, 0.0, 0.0).color(0,0,0,255).endVertex()
        buffer.pos(0.0, 0.0, 0.0).color(0,0,0,255).endVertex()
        tess.draw()
        GlStateManager.enableTexture2D()

        host.buffer.isRenderingEnabled = true
        host.buffer.renderText()
        GlStateManager.popMatrix()
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {

    }

    override fun handleKeyboardInput() {
        super.handleKeyboardInput()
        val buffer = host.buffer
        val code = Keyboard.getEventKey()
        if (code != Keyboard.KEY_ESCAPE && code != Keyboard.KEY_F11) {
            if (Keyboard.getEventKeyState()) {
                val char = Keyboard.getEventCharacter()
                if (!pressedKeys.contains(code) || !ignoreRepeat(char, code)) {
                    if (code == Keyboard.KEY_V && Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
                        buffer.clipboard(GuiScreen.getClipboardString(), null)
                    } else {
                        buffer.keyDown(char, code, null)
                    }
                    pressedKeys += code to char
                }
            } else {
                val char = pressedKeys.remove(code)
                char?.let {
                    buffer.keyUp(char, code, null)
                }
            }

        } else if(code == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(null)
        }
    }

    private fun ignoreRepeat(char: Char, code: Int): Boolean {
        return code == Keyboard.KEY_LCONTROL ||
                code == Keyboard.KEY_RCONTROL ||
                code == Keyboard.KEY_LMENU ||
                code == Keyboard.KEY_RMENU ||
                code == Keyboard.KEY_LSHIFT ||
                code == Keyboard.KEY_RSHIFT ||
                code == Keyboard.KEY_LMETA ||
                code == Keyboard.KEY_RMETA
    }

    fun pong(isRunning: Boolean) {
        this.machineRunning = isRunning

        turnOffButton.visible = machineRunning
        turnOnButton.visible = !turnOffButton.visible
    }
}