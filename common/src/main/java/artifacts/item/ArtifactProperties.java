package artifacts.item;

import artifacts.Artifacts;
import artifacts.component.Equipable;
import artifacts.component.ability.*;
import artifacts.component.ability.mobeffect.EquipmentMobEffect;
import artifacts.component.ability.mobeffect.MobEffectProvider;
import artifacts.config.value.ConfigValue;
import artifacts.config.value.Value;
import artifacts.registry.ModDataComponents;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentInitializers;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Unit;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ArtifactProperties {

    private final String itemName;
    private final Item.Properties properties;
    private final Map<Supplier<Boolean>, EquipmentAttributeModifier> attributes;
    private final List<EnchantmentLevelModifier> enchantments;

    private boolean isEquipable = false;

    public ArtifactProperties(String itemName) {
        this.itemName = itemName;
        this.properties = new Item.Properties();
        this.attributes = new HashMap<>();
        this.enchantments = new ArrayList<>();
    }

    public ArtifactProperties equipable() {
        return equipable(SoundEvents.ARMOR_EQUIP_GENERIC);
    }

    public ArtifactProperties equipable(SoundEvent equipSound) {
        return equipable(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(equipSound));
    }

    public ArtifactProperties equipable(Holder<SoundEvent> equipSound) {
        this.isEquipable = true;
        component(ModDataComponents.EQUIPABLE.get(), new Equipable(equipSound, true));
        return this;
    }

    public ArtifactProperties addAttributeModifier(Holder<Attribute> attribute, Value<Double> amount, AttributeModifier.Operation operation) {
        return addAttributeModifier(attribute, amount, operation, () -> true, true);
    }

    public ArtifactProperties addAttributeModifier(Holder<Attribute> attribute, Value<Double> amount, AttributeModifier.Operation operation, Supplier<Boolean> condition, boolean ignoreCooldown) {
        attributes.put(condition, new EquipmentAttributeModifier(attribute, amount, operation,
                Artifacts.id(itemName + '/' + attribute.unwrapKey().orElseThrow().identifier().getPath()), ignoreCooldown)
        );
        return this;
    }

    public ArtifactProperties mobEffect(Holder<MobEffect> effect, Value<Integer> level, Value<Integer> duration, Supplier<EntityCondition> condition) {
        return delayedComponent(ModDataComponents.MOB_EFFECTS.get(), _ -> new CompositeAbility<>(List.of(
                new EquipmentMobEffect(new MobEffectProvider(effect, level, duration, Value.of(false), Value.of(true), condition.get()))
        )));
    }

    public ArtifactProperties increasesEnchantment(ResourceKey<Enchantment> enchantment, Value<Integer> amount) {
        enchantments.add(new EnchantmentLevelModifier(enchantment, amount));
        return this;
    }

    public ArtifactProperties durability(ItemDamageProperties durability) {
        // Vanilla also sets max stack size to 1, but we already do this by default in ArtifactProperties#build()
        delayedComponent(DataComponents.DAMAGE, durability::canBeDamaged, _ -> 0);
        delayedComponent(DataComponents.MAX_DAMAGE, durability::canBeDamaged, _ -> durability.getMaxDamage());
        return this;
    }

    public ArtifactProperties component(DataComponentType<Unit> type) {
        return component(type, Unit.INSTANCE);
    }

    public ArtifactProperties component(DataComponentType<SimpleAbility> type, Value<Boolean> enabled) {
        return component(type, new SimpleAbility(enabled));
    }

    public <T> ArtifactProperties delayedComponent(DataComponentType<T> type, DataComponentInitializers.SingleComponentInitializer<@Nullable T> initializer) {
        return delayedComponent(type, () -> true, initializer);
    }

    @SuppressWarnings("DataFlowIssue")
    public <T> ArtifactProperties delayedComponent(DataComponentType<T> type, Supplier<Boolean> condition, DataComponentInitializers.SingleComponentInitializer<@Nullable T> initializer) {
        assertRequiresWorldRestart(condition);
        properties.delayedComponent(type, context -> condition.get() ? initializer.create(context) : null);
        return this;
    }

    public <T> ArtifactProperties component(DataComponentType<T> type, T component) {
        properties.component(type, component);
        return this;
    }

    public ArtifactProperties properties(Consumer<Item.Properties> consumer) {
        consumer.accept(this.properties);
        return this;
    }

    public Item.Properties build() {
        if (isEquipable) {
            component(ModDataComponents.DEPENDENCY_CHECK_TOOLTIP.get());
        }
        component(ModDataComponents.COSMETIC_TOOLTIP.get());
        properties.stacksTo(1);
        properties.rarity(Rarity.RARE);
        properties.fireResistant();
        if (!attributes.isEmpty()) {
            properties.delayedComponent(ModDataComponents.ATTRIBUTE_MODIFIERS.get(), _ -> {
                List<EquipmentAttributeModifier> result = new ArrayList<>();
                for (Map.Entry<Supplier<Boolean>, EquipmentAttributeModifier> entry : attributes.entrySet()) {
                    assertRequiresWorldRestart(entry.getKey());
                    if (entry.getKey().get()) {
                        result.add(entry.getValue());
                    }
                }
                return new CompositeAbility<>(List.copyOf(result));
            });
        }
        if (!enchantments.isEmpty()) {
            properties.component(ModDataComponents.ENCHANTMENT_LEVEL_MODIFIERS.get(), new CompositeAbility<>(enchantments));
        }
        return properties;
    }

    private static void assertRequiresWorldRestart(Supplier<Boolean> condition) {
        if (condition instanceof ConfigValue<Boolean> configValue && !configValue.requiresRestart()) {
            throw new IllegalArgumentException(
                    "Config value '%s' used as a component condition should require world restart".formatted(configValue.getKey())
            );
        }
    }
}
