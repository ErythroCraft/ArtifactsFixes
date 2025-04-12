package artifacts.item;

import artifacts.Artifacts;
import artifacts.component.ability.AttributeModifierAbility;
import artifacts.component.ability.IncreaseEnchantmentLevelAbility;
import artifacts.config.value.Value;
import artifacts.integration.ModCompat;
import artifacts.integration.equipment.EquipmentIntegration;
import artifacts.integration.equipment.EquipmentIntegrationUtils;
import artifacts.platform.PlatformServices;
import artifacts.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Consumer;

public class WearableArtifactItem extends Item {

    public WearableArtifactItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        if (Artifacts.CONFIG.client.showTooltips.get()
                && !PlatformServices.platformHelper.isModLoaded(ModCompat.CURIOS)
                && !PlatformServices.platformHelper.isModLoaded(ModCompat.TRINKETS)
                && !PlatformServices.platformHelper.isModLoaded(ModCompat.ACCESSORIES)
        ) {
            list.add(Component.translatable("%s.tooltip.missing_dependency".formatted(Artifacts.MOD_ID)).withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
        } else {
            super.appendHoverText(itemStack, tooltipContext, list, tooltipFlag);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        EquipmentIntegration trinkets = EquipmentIntegrationUtils.getIntegration(ModCompat.TRINKETS);
        if (!stack.has(DataComponents.FOOD) && trinkets != null && trinkets.equipAccessory(player, stack)) {
            SoundEvent sound = stack.get(ModDataComponents.EQUIP_SOUND.get());
            if (sound != null) {
                player.playSound(sound, 1, 1);
            }

            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        return super.use(level, player, hand);
    }

    public static class Builder {

        private final String itemName;
        private final Item.Properties properties = new Item.Properties();

        public Builder(String itemName) {
            this.itemName = itemName;
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
            return component(ModDataComponents.ATTRIBUTE_MODIFIER.get(), new AttributeModifierAbility(attribute, amount, operation, Artifacts.id(itemName + '/' + attribute.unwrapKey().orElseThrow().location().getPath()), ignoreCooldown));
        }

        public Builder increasesEnchantment(ResourceKey<Enchantment> enchantment, Value<Integer> amount) {
            return component(ModDataComponents.INCREASE_ENCHANTMENT_LEVEL.get(), new IncreaseEnchantmentLevelAbility(enchantment, amount));
        }

        public Builder component(DataComponentType<Unit> type) {
            return component(type, Unit.INSTANCE);
        }

        public <T> Builder component(DataComponentType<T> type, T component) {
            properties.component(type, component);
            return this;
        }

        public Builder properties(Consumer<Properties> consumer) {
            consumer.accept(this.properties);
            return this;
        }

        public WearableArtifactItem build() {
            properties.stacksTo(1).rarity(Rarity.RARE).fireResistant();
            return new WearableArtifactItem(properties);
        }
    }
}
