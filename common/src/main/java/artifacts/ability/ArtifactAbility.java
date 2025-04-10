package artifacts.ability;

import artifacts.Artifacts;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public interface ArtifactAbility {

    boolean isNonCosmetic();

    default boolean isEnabled() {
        return isNonCosmetic();
    }

    default boolean isActive(LivingEntity entity) {
        return isEnabled() && true; // TODO AbilityHelper.isToggledOn(getType(), entity);
    }

    default void addTooltipIfNonCosmetic(List<MutableComponent> tooltip) {
        if (isNonCosmetic()) {
            addAbilityTooltip(tooltip);
            addToggleKeyTooltip(tooltip);
        }
    }

    @SuppressWarnings("ConstantConditions")
    default void addAbilityTooltip(List<MutableComponent> tooltip) {
        ResourceLocation id = Artifacts.id(""); // TODO
        tooltip.add(Component.translatable("%s.tooltip.ability.%s".formatted(id.getNamespace(), id.getPath())));
    }

    default void addToggleKeyTooltip(List<MutableComponent> tooltip) {
        // TODO
        /*
        KeyMapping key = ToggleKeyHandlers.getToggleKey(this.getType());
        Player player = null;
        // noinspection ConstantValue
        if (Minecraft.getInstance() != null) {
            player = ArtifactsClient.getLocalPlayer();
        }
        if (key != null && player != null && (!key.isUnbound() || !AbilityHelper.isToggledOn(getType(), player))) {
            tooltip.add(Component.translatable("%s.tooltip.toggle_keymapping".formatted(Artifacts.MOD_ID), key.getTranslatedKeyMessage()));
        }
        */
    }

    @SuppressWarnings("ConstantConditions")
    default MutableComponent tooltipLine(String abilityName, Object... args) {
        ResourceLocation id = Artifacts.id(""); // TODO ModAbilities.getRegistry().getKey(getType());
        return Component.translatable("%s.tooltip.ability.%s.%s".formatted(id.getNamespace(), id.getPath(), abilityName), args);
    }

    default boolean shouldTick() {
        return false;
    }

    default void wornTick(LivingEntity entity, boolean isOnCooldown, boolean isActive) {

    }

    default void onUnequip(LivingEntity entity, boolean wasActive) {

    }
}
