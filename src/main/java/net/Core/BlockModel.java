package net.Core;

import net.GameLayer.ChunkGen;

import java.util.List;
import java.util.Map;

public class BlockModel extends Model {

    public String getTexture(int x) {
        String key = switch (x) {
            case 0 -> this.textures.get("north");
            case 1 -> this.textures.get("south");
            case 2 -> this.textures.get("east");
            case 3 -> this.textures.get("west");
            case 4 -> this.textures.get("up");
            case 5 -> this.textures.get("down");
            default -> "";
        };

        return this.textures.get(key);
    }

}
