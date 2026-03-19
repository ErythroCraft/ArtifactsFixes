package artifacts.item;

import artifacts.Artifacts;
import artifacts.component.Equipable;
import artifacts.component.ability.*;
import artifacts.component.ability.mobeffect.EquipmentMobEffect;
import artifacts.component.ability.mobeffect.MobEffectProvider;
import artifacts.config.value.Value;
import artifacts.registry.ModDataComponents;
import net.minecraft.core.Holder;
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

public final class EquipableArtifactBuilder {

    private final String itemName;
    private final Item.Properties properties;
    private final List<EquipmentAttributeModifier> attributes;
    private final List<EnchantmentLevelModifier> enchantments;

    private boolean isEquipable = false;

    public EquipableArtifactBuilder(String itemName) {
        this.itemName = itemName;
        this.properties = new Item.Properties();
        this.attributes = new ArrayList<>();
        this.enchantments = new ArrayList<>();
    }

    public EquipableArtifactBuilder equipable() {
        return equipable(SoundEvents.ARMOR_EQUIP_GENERIC);
    }

    public EquipableArtifactBuilder equipable(SoundEvent equipSound) {
        return equipable(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(equipSound));
    }

    public EquipableArtifactBuilder equipable(Holder<SoundEvent> equipSound) {
        this.isEquipable = true;
        component(ModDataComponents.EQUIPABLE.get(), new Equipable(equipSound, true));
        return this;
    }

    public EquipableArtifactBuilder addAttributeModifier(Holder<Attribute> attribute, Value<Double> amount, AttributeModifier.Operation operation) {
        return addAttributeModifier(attribute, amount, operation, true);
    }

    public EquipableArtifactBuilder addAttributeModifier(Holder<Attribute> attribute, Value<Double> amount, AttributeModifier.Operation operation, boolean ignoreCooldown) {
        attributes.add(new EquipmentAttributeModifier(attribute, amount, operation,
                Artifacts.id(itemName + '/' + attribute.unwrapKey().orElseThrow().identifier().getPath()), ignoreCooldown)
        );
        return this;
    }

    public EquipableArtifactBuilder mobEffect(Holder<MobEffect> effect, Value<Integer> level, Value<Integer> duration, EntityCondition condition) {
        return component(ModDataComponents.MOB_EFFECTS.get(), new CompositeAbility<>(List.of(
                new EquipmentMobEffect(new MobEffectProvider(effect, level, duration, Value.of(false), Value.of(true), condition))
        )));
    }

    public EquipableArtifactBuilder increasesEnchantment(ResourceKey<Enchantment> enchantment, Value<Integer> amount) {
        enchantments.add(new EnchantmentLevelModifier(enchantment, amount));
        return this;
    }

    public EquipableArtifactBuilder component(DataComponentType<Unit> type) {
        return component(type, Unit.INSTANCE);
    }

    public EquipableArtifactBuilder component(DataComponentType<SimpleAbility> type, Value<Boolean> enabled) {
        return component(type, new SimpleAbility(enabled));
    }

    public <T> EquipableArtifactBuilder component(DataComponentType<T> type, Value.ConfigValue<Boolean> condition, T component) {
        if (!condition.requiresRestart()) {
            throw new IllegalArgumentException();
        }
        return component(type, condition.get() ? component : null);
    }

    @SuppressWarnings("DataFlowIssue")
    public <T> EquipableArtifactBuilder component(DataComponentType<T> type, @Nullable T component) {
        properties.component(type, component);
        return this;
    }

    public EquipableArtifactBuilder properties(Consumer<Item.Properties> consumer) {
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
