package artifacts.item;

import artifacts.Artifacts;
import artifacts.component.Equipable;
import artifacts.component.ability.*;
import artifacts.component.ability.mobeffect.EquipmentMobEffect;
import artifacts.component.ability.mobeffect.MobEffectProvider;
import artifacts.config.value.Value;
import artifacts.registry.ModDataComponents;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentInitializers;
import net.minecraft.core.component.DataComponentType;
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
import java.util.List;
import java.util.function.Consumer;

public final class ArtifactProperties {

    private final String itemName;
    private final Item.Properties properties;
    private final List<EquipmentAttributeModifier> attributes;
    private final List<EnchantmentLevelModifier> enchantments;

    private boolean isEquipable = false;

    public ArtifactProperties(String itemName) {
        this.itemName = itemName;
        this.properties = new Item.Properties();
        this.attributes = new ArrayList<>();
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
        return addAttributeModifier(attribute, amount, operation, true);
    }

    public ArtifactProperties addAttributeModifier(Holder<Attribute> attribute, Value<Double> amount, AttributeModifier.Operation operation, boolean ignoreCooldown) {
        attributes.add(new EquipmentAttributeModifier(attribute, amount, operation,
                Artifacts.id(itemName + '/' + attribute.unwrapKey().orElseThrow().identifier().getPath()), ignoreCooldown)
        );
        return this;
    }

    public ArtifactProperties mobEffect(Holder<MobEffect> effect, Value<Integer> level, Value<Integer> duration, EntityCondition condition) {
        return component(ModDataComponents.MOB_EFFECTS.get(), new CompositeAbility<>(List.of(
                new EquipmentMobEffect(new MobEffectProvider(effect, level, duration, Value.of(false), Value.of(true), condition))
        )));
    }

    public ArtifactProperties increasesEnchantment(ResourceKey<Enchantment> enchantment, Value<Integer> amount) {
        enchantments.add(new EnchantmentLevelModifier(enchantment, amount));
        return this;
    }

    public ArtifactProperties component(DataComponentType<Unit> type) {
        return component(type, Unit.INSTANCE);
    }

    public ArtifactProperties component(DataComponentType<SimpleAbility> type, Value<Boolean> enabled) {
        return component(type, new SimpleAbility(enabled));
    }

    public <T> ArtifactProperties component(DataComponentType<T> type, Value.ConfigValue<Boolean> condition, T component) {
        return delayedComponent(type, condition, _ -> component);
    }

    @SuppressWarnings("DataFlowIssue")
    public <T> ArtifactProperties delayedComponent(DataComponentType<T> type, Value.ConfigValue<Boolean> condition, DataComponentInitializers.SingleComponentInitializer<@Nullable T> initializer) {
        if (!condition.requiresRestart()) {
            throw new IllegalArgumentException();
        }
        properties.delayedComponent(type, context -> condition.get() ? initializer.create(context) : null);
        return this;
    }

    @SuppressWarnings("DataFlowIssue")
    public <T> ArtifactProperties component(DataComponentType<T> type, @Nullable T component) {
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
        properties.stacksTo(1)
                .rarity(Rarity.RARE)
                .fireResistant();
        if (!attributes.isEmpty()) {
            properties.component(ModDataComponents.ATTRIBUTE_MODIFIERS.get(), new CompositeAbility<>(attributes));
        }
        if (!enchantments.isEmpty()) {
            properties.component(ModDataComponents.ENCHANTMENT_LEVEL_MODIFIERS.get(), new CompositeAbility<>(enchantments));
        }
        return properties;
    }
}
