package artifacts.neoforge.integration.cosmeticarmor;

import lain.mods.cos.api.CosArmorAPI;
import net.minecraft.world.entity.player.Player;

public class CosmeticArmorCompat {

    public static boolean areBootsHidden(Player player) {
        // TODO do I still need this?
        return CosArmorAPI.getCAStacksClient(player.getUUID()).isSkinArmor(0);
    }
}
