package artifacts.ability;

import artifacts.Artifacts;
import artifacts.ArtifactsClient;
import artifacts.client.ToggleKeyHandlers;
import artifacts.registry.ModAbilities;
import artifacts.util.AbilityHelper;
import artifacts.util.ModCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public interface ArtifactAbility {

    Codec<ArtifactAbility> CODEC = ModCodecs.lazyCodec(() ->
            ModAbilities.getRegistry().byNameCodec().dispatch("type", ArtifactAbility::getType, Type::codec)
    );

    StreamCodec<RegistryFriendlyByteBuf, ArtifactAbility> STREAM_CODEC = ModCodecs.lazyStreamCodec(() ->
            ByteBufCodecs.registry(ModAbilities.REGISTRY_KEY).dispatch(ArtifactAbility::getType, Type::streamCodec)
    );

    Type<?> getType();

    boolean isNonCosmetic();

    default boolean isEnabled() {
        return isNonCosmetic();
    }

    default boolean isActive(LivingEntity entity) {
        return isEnabled() && AbilityHelper.isToggledOn(getType(), entity);
    }

    default void addTooltipIfNonCosmetic(List<MutableComponent> tooltip) {
        if (isNonCosmetic()) {
            addAbilityTooltip(tooltip);
            addToggleKeyTooltip(tooltip);
        }
    }

    @SuppressWarnings("ConstantConditions")
    default void addAbilityTooltip(List<MutableComponent> tooltip) {
        ResourceLocation id = ModAbilities.getRegistry().getKey(getType());
        tooltip.add(Component.translatable("%s.tooltip.ability.%s".formatted(id.getNamespace(), id.getPath())));
    }

    default void addToggleKeyTooltip(List<MutableComponent> tooltip) {
        KeyMapping key = ToggleKeyHandlers.getToggleKey(this.getType());
        Player player = null;
        // noinspection ConstantValue
        if (Minecraft.getInstance() != null) {
            player = ArtifactsClient.getLocalPlayer();
        }
        if (key != null && player != null && (!key.isUnbound() || !AbilityHelper.isToggledOn(getType(), player))) {
            tooltip.add(Component.translatable("%s.tooltip.toggle_keymapping".formatted(Artifacts.MOD_ID), key.getTranslatedKeyMessage()));
        }
    }

    @SuppressWarnings("ConstantConditions")
    default MutableComponent tooltipLine(String abilityName, Object... args) {
        ResourceLocation id = ModAbilities.getRegistry().getKey(getType());
        return Component.translatable("%s.tooltip.ability.%s.%s".formatted(id.getNamespace(), id.getPath(), abilityName), args);
    }

    default boolean shouldTick() {
        return false;
    }

    default void wornTick(LivingEntity entity, boolean isOnCooldown, boolean isActive) {

    }

    default void onUnequip(LivingEntity entity, boolean wasActive) {

    }

    record Type<T extends ArtifactAbility>(MapCodec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {

    }
}
