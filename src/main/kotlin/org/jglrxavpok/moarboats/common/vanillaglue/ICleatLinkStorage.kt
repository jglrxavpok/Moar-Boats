package org.jglrxavpok.moarboats.common.vanillaglue

import org.jglrxavpok.moarboats.api.Cleat
import org.jglrxavpok.moarboats.api.Link
import java.util.concurrent.ConcurrentHashMap

interface ICleatLinkStorage {
    fun getLinkStorage(): ConcurrentHashMap<Cleat, Link>
    fun syncLinkStorage(newValue: Map<Cleat, Link>)
}