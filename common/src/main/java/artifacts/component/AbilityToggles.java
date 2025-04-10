package artifacts.component;

import artifacts.ability.ArtifactAbility;
import artifacts.network.NetworkHandler;
import artifacts.network.UpdateArtifactTogglesPacket;
import artifacts.util.AbilityHelper;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

import java.util.*;

public class AbilityToggles {

    public static final Codec<AbilityToggles> CODEC = ResourceLocation.CODEC.listOf().xmap(
            list -> {
                Set<DataComponentType<? extends ArtifactAbility>> toggles = new HashSet<>();
                for (ResourceLocation id : list) {
                    @SuppressWarnings("unchecked")
                    DataComponentType<? extends ArtifactAbility> toggle = (DataComponentType<? extends ArtifactAbility>) BuiltInRegistries.DATA_COMPONENT_TYPE.get(id);
                    toggles.add(toggle);
                }
                return new AbilityToggles(toggles);
            },
            abilityToggles -> {
                List<ResourceLocation> list = new ArrayList<>();
                for (DataComponentType<?> toggle : abilityToggles.toggles) {
                    ResourceLocation id = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(toggle);
                    list.add(id);
                }
                return list;
            }
    );

    protected final Set<DataComponentType<? extends ArtifactAbility>> toggles = new HashSet<>();

    public AbilityToggles() {
        this(Set.of());
    }

    public AbilityToggles(Collection<DataComponentType<? extends ArtifactAbility>> toggles) {
        this.toggles.addAll(toggles);
    }

    public boolean isToggledOn(DataComponentType<?> type) {
        return !toggles.contains(type);
    }

    public void toggle(DataComponentType<? extends ArtifactAbility> type, LivingEntity entity) {
        if (toggles.contains(type)) {
            toggles.remove(type);
        } else {
            toggles.add(type);
            if (!entity.level().isClientSide()) {
                AbilityHelper.forEach(type, entity, ability -> ability.onUnequip(entity, ability.isEnabled()));
            }
        }
    }

    public void applyToggles(Collection<DataComponentType<? extends ArtifactAbility>> toggles, LivingEntity entity) {
        for (DataComponentType<? extends ArtifactAbility> type : Set.copyOf(Sets.symmetricDifference(this.toggles, Set.copyOf(toggles)))) {
            toggle(type, entity);
        }
    }

    public void sendToClient(ServerPlayer player) {
        NetworkHandler.sendToPlayer(player, new UpdateArtifactTogglesPacket(List.copyOf(toggles)));
    }
}
