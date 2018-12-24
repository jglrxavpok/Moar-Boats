package org.jglrxavpok.moarboats.integration.opencomputers

import li.cil.oc.api.network.ComponentConnector

class BoatNode(val base: ComponentConnector): ComponentConnector by base {

    override fun address(): String {
        return "cafebabe-beef-dead-beef-01234679abcd"
    }
}