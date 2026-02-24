package artifacts.item;

import artifacts.Artifacts;
import artifacts.component.ability.AttributeModifiers;
import artifacts.component.ability.EnchantmentLevelModifiers;
import artifacts.component.ability.EntityCondition;
import artifacts.component.ability.SimpleAbility;
import artifacts.component.ability.mobeffect.EquipmentMobEffects;
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

// TODO (>1.21.1 to avoid breaking RAR-Compat) migrate the missing dependency check/tooltip to an ability, delete this class
public class WearableArtifactItem extends Item {

    public WearableArtifactItem(Item.Properties properties) {
        super(properties);
    }

    public static class Builder {

        private final String itemName;
        private final Item.Properties properties;
        private final List<AttributeModifiers.Entry> attributes;
        private final List<EnchantmentLevelModifiers.Entry> enchantments;

        public Builder(String itemName, Item.Properties properties) {
            this.itemName = itemName;
            this.properties = properties;
            this.attributes = new ArrayList<>();
            this.enchantments = new ArrayList<>();
            equipSound(SoundEvents.ARMOR_EQUIP_GENERIC);
        }

        public Builder equipSound(SoundEvent equipSound) {
            return equipSound(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(equipSound));
        }

        public Builder equipSound(Holder<SoundEvent> equipSound) {
            properties.component(ModDataComponents.EQUIP_SOUND.get(), equipSound.value());
            return this;
        }

        public Builder addAttributeModifier(Holder<Attribute> attribute, Value<Double> amount, AttributeModifier.Operation operation) {
            return addAttributeModifier(attribute, amount, operation, true);
        }

        public Builder addAttributeModifier(Holder<Attribute> attribute, Value<Double> amount, AttributeModifier.Operation operation, boolean ignoreCooldown) {
            attributes.add(new AttributeModifiers.Entry(attribute, amount, operation,
                    Artifacts.id(itemName + '/' + attribute.unwrapKey().orElseThrow().identifier().getPath()), ignoreCooldown)
            );
            return this;
        }

        public Builder mobEffect(Holder<MobEffect> effect, Value<Integer> level, Value<Integer> duration, EntityCondition condition) {
            return component(ModDataComponents.MOB_EFFECTS.get(), new EquipmentMobEffects(List.of(
                    new EquipmentMobEffects.Entry(new MobEffectProvider(effect, level, duration, Value.of(false), Value.of(true), condition))
            )));
        }

        public Builder increasesEnchantment(ResourceKey<Enchantment> enchantment, Value<Integer> amount) {
            enchantments.add(new EnchantmentLevelModifiers.Entry(enchantment, amount));
            return this;
        }

        public Builder component(DataComponentType<Unit> type) {
            return component(type, Unit.INSTANCE);
        }

        public Builder component(DataComponentType<SimpleAbility> type, Value<Boolean> enabled) {
            return component(type, new SimpleAbility(enabled));
        }

        public <T> Builder component(DataComponentType<T> type, Value.ConfigValue<Boolean> value, T component) {
            if (!value.requiresRestart()) {
                throw new IllegalArgumentException();
            }
            return component(type, value.get() ? component : null);
        }

        @SuppressWarnings("DataFlowIssue")
        public <T> Builder component(DataComponentType<T> type, @Nullable T component) {
            properties.component(type, component);
            return this;
        }

        public Builder properties(Consumer<Properties> consumer) {
            consumer.accept(this.properties);
            return this;
        }

        public Item build() {
            properties.stacksTo(1)
                    .rarity(Rarity.RARE)
                    .fireResistant()
                    .component(ModDataComponents.DEPENDENCY_CHECK_TOOLTIP.get(), Unit.INSTANCE)
                    .component(ModDataComponents.COSMETIC_TOOLTIP.get(), Unit.INSTANCE);
            if (!attributes.isEmpty()) {
                properties.component(ModDataComponents.ATTRIBUTE_MODIFIERS.get(), new AttributeModifiers(attributes));
            }
            if (!enchantments.isEmpty()) {
                properties.component(ModDataComponents.ENCHANTMENT_LEVEL_MODIFIERS.get(), new EnchantmentLevelModifiers(enchantments));
            }
            return new WearableArtifactItem(properties);
        }
    }
}
