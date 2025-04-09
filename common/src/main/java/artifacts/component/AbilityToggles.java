package artifacts.component;

import artifacts.ability.ArtifactAbility;
import artifacts.network.UpdateArtifactTogglesPacket;
import artifacts.registry.ModAbilities;
import artifacts.util.AbilityHelper;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import dev.architectury.networking.NetworkManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

import java.util.*;

public class AbilityToggles {

    public static final Codec<AbilityToggles> CODEC = ResourceLocation.CODEC.listOf().xmap(
            list -> {
                Set<ArtifactAbility.Type<?>> toggles = new HashSet<>();
                for (ResourceLocation id : list) {
                    toggles.add(ModAbilities.getRegistry().get(id));
                }
                return new AbilityToggles(toggles);
            },
            abilityToggles -> {
                List<ResourceLocation> list = new ArrayList<>();
                for (ArtifactAbility.Type<?> toggle : abilityToggles.toggles) {
                    ResourceLocation id = ModAbilities.getRegistry().getKey(toggle);
                    list.add(id);
                }
                return list;
            }
    );

    protected final Set<ArtifactAbility.Type<?>> toggles = new HashSet<>();

    public AbilityToggles() {
        this(Set.of());
    }

    public AbilityToggles(Collection<ArtifactAbility.Type<?>> toggles) {
        this.toggles.addAll(toggles);
    }

    public boolean isToggledOn(ArtifactAbility.Type<?> type) {
        return !toggles.contains(type);
    }

    public void toggle(ArtifactAbility.Type<?> type, LivingEntity entity) {
        if (toggles.contains(type)) {
            toggles.remove(type);
        } else {
            toggles.add(type);
            if (!entity.level().isClientSide()) {
                AbilityHelper.forEach(type, entity, ability -> ability.onUnequip(entity, ability.isEnabled()));
            }
        }
    }

    public void applyToggles(Collection<ArtifactAbility.Type<?>> toggles, LivingEntity entity) {
        for (ArtifactAbility.Type<?> type : Set.copyOf(Sets.symmetricDifference(this.toggles, Set.copyOf(toggles)))) {
            toggle(type, entity);
        }
    }

    public void sendToClient(ServerPlayer player) {
        NetworkManager.sendToPlayer(player, new UpdateArtifactTogglesPacket(List.copyOf(toggles)));
    }
}
