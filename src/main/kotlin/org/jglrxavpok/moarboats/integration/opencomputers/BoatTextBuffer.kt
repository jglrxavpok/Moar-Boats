package org.jglrxavpok.moarboats.integration.opencomputers

import li.cil.oc.api.internal.TextBuffer
import li.cil.oc.api.network.Message
import li.cil.oc.api.network.Node
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound

class BoatTextBuffer: TextBuffer {
    override fun setAspectRatio(p0: Double, p1: Double) {
        // nop
    }

    override fun copy(p0: Int, p1: Int, p2: Int, p3: Int, p4: Int, p5: Int) {

    }

    override fun rawSetForeground(p0: Int, p1: Int, p2: Array<out IntArray>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun fill(p0: Int, p1: Int, p2: Int, p3: Int, p4: Char) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDisconnect(p0: Node?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setRenderingEnabled(p0: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun keyDown(p0: Char, p1: Int, p2: EntityPlayer?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConnect(p0: Node?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun save(p0: NBTTagCompound?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPowerState(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getHeight(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun renderText(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getMaximumColorDepth(): TextBuffer.ColorDepth {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun canUpdate(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setPowerState(p0: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun mouseDrag(p0: Double, p1: Double, p2: Int, p3: EntityPlayer?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setViewport(p0: Int, p1: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun keyUp(p0: Char, p1: Int, p2: EntityPlayer?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isRenderingEnabled(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun mouseUp(p0: Double, p1: Double, p2: Int, p3: EntityPlayer?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getViewportHeight(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAspectRatio(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setColorDepth(p0: TextBuffer.ColorDepth?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getForegroundColor(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getForegroundColor(p0: Int, p1: Int): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun rawSetBackground(p0: Int, p1: Int, p2: Array<out IntArray>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setPaletteColor(p0: Int, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getMaximumWidth(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setEnergyCostPerTick(p0: Double) { }

    override fun update() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getMaximumHeight(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setBackgroundColor(p0: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setBackgroundColor(p0: Int, p1: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isBackgroundFromPalette(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isBackgroundFromPalette(p0: Int, p1: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMessage(p0: Message?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPaletteColor(p0: Int): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun renderWidth(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setMaximumResolution(p0: Int, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun get(p0: Int, p1: Int): Char {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setResolution(p0: Int, p1: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getColorDepth(): TextBuffer.ColorDepth {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBackgroundColor(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBackgroundColor(p0: Int, p1: Int): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isForegroundFromPalette(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isForegroundFromPalette(p0: Int, p1: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun rawSetText(p0: Int, p1: Int, p2: Array<out CharArray>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun mouseDown(p0: Double, p1: Double, p2: Int, p3: EntityPlayer?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun set(p0: Int, p1: Int, p2: String?, p3: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setForegroundColor(p0: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setForegroundColor(p0: Int, p1: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getWidth(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun mouseScroll(p0: Double, p1: Double, p2: Int, p3: EntityPlayer?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun clipboard(p0: String?, p1: EntityPlayer?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getEnergyCostPerTick(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun node(): Node {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getViewportWidth(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun load(p0: NBTTagCompound?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setMaximumColorDepth(p0: TextBuffer.ColorDepth?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun renderHeight(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}