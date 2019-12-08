package org.jglrxavpok.moarboats.common.network

import org.jglrxavpok.moarboats.common.data.LoopingOptions

abstract class CChangeLoopingStateBase(): MoarBoatsPacket {

    var loopingOption: LoopingOptions = LoopingOptions.NoLoop

    constructor(loopingOption: LoopingOptions): this() {
        this.loopingOption = loopingOption
    }

}