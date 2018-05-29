package org.jglrxavpok.moarboats.common.modules

interface BlockReason {
    fun blocksSpeed() = true
    fun blocksRotation() = true
}
object NoBlockReason: BlockReason
object BlockedByRedstone: BlockReason