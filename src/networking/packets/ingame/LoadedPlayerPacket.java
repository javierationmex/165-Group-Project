package networking.packets.ingame;

import networking.packets.IPacket;

import java.util.UUID;

/**
 * Created by Max on 3/31/2015.
 */
public class LoadedPlayerPacket implements IPacket{
    private boolean loaded;
    private UUID id;

    public LoadedPlayerPacket(UUID id, boolean loaded) {
        this.loaded = loaded;
        this.id = id;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public UUID getId() {
        return id;
    }
}
