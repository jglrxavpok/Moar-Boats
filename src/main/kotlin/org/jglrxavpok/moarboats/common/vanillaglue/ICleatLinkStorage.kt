package org.jglrxavpok.moarboats.common.vanillaglue

import org.jglrxavpok.moarboats.api.Cleat
import org.jglrxavpok.moarboats.api.Link

interface ICleatLinkStorage {
    fun getLinkStorage(): MutableMap<Cleat, Link>
    fun syncLinkStorage(newValue: MutableMap<Cleat, Link>)
}