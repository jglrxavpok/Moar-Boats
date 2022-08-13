package org.jglrxavpok.moarboats.integrations.journeymap;

import journeymap.client.api.ClientPlugin;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.event.ClientEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.moarboats.MoarBoats;
import org.jglrxavpok.moarboats.api.IWaypointProvider;
import org.jglrxavpok.moarboats.api.WaypointInfo;

import java.util.List;
import java.util.stream.Collectors;

import static org.jglrxavpok.moarboats.api.IWaypointProviderKt.getWaypointProviders;

@ClientPlugin
class MoarBoatsPlugin implements IClientPlugin, IWaypointProvider {

    private IClientAPI apiRef = null;

    @Override
    public void initialize(IClientAPI api) {
        apiRef = api;

        getWaypointProviders().add(this);
    }

    @Override
    public String getModId() {
        return MoarBoats.ModID;
    }

    @Override
    public void onEvent(ClientEvent event) {
        // don't care
    }

    @NotNull
    @Override
    public Component getName() {
        return Component.literal("JourneyMap");
    }

    @Override
    public List<WaypointInfo> getList() {
        return apiRef.getAllWaypoints().stream().map(waypoint ->
            new WaypointInfo("JourneyMap", waypoint.getName(), waypoint.getPosition().getX(), waypoint.getPosition().getZ(), null)
        ).collect(Collectors.toList());
    }

    @Override
    public void updateList(@NotNull Player player) {}
}