package artifacts.component.ability.mobeffect;

import artifacts.component.ability.AbilityCondition;
import artifacts.component.ability.EquipmentAbility;
import artifacts.equipment.EquipmentHelper;
import artifacts.registry.ModDataComponents;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;

import java.util.List;

public record PostEatingEffects(List<MobEffectProvider> effects)
        implements EquipmentAbility {

    public static final Codec<PostEatingEffects> CODEC = MobEffectProvider.codec(true)
            .listOf().xmap(PostEatingEffects::new, PostEatingEffects::effects);

    public static final StreamCodec<RegistryFriendlyByteBuf, PostEatingEffects> STREAM_CODEC = ByteBufCodecs.<RegistryFriendlyByteBuf, MobEffectProvider>list()
            .apply(MobEffectProvider.STREAM_CODEC).map(PostEatingEffects::new, PostEatingEffects::effects);

    @Override
    public boolean isNonCosmetic() {
        for (MobEffectProvider provider : effects) {
            if (provider.isNonCosmetic()) {
                return true;
            }
        }
        return false;
    }

    public static void applyEffects(LivingEntity entity, FoodProperties properties) {
        int foodPointsMissing = entity instanceof Player player ? 20 - player.getFoodData().getFoodLevel() : 20;
        int foodPointsRestored = Math.min(properties.nutrition(), foodPointsMissing);
        applyEffects(entity, foodPointsRestored);
    }

    public static void applyEffects(LivingEntity entity, int foodPointsRestored) {
        if (foodPointsRestored > 0) {
            EquipmentHelper.iterateAbilities(ModDataComponents.POST_EATING_EFFECTS.get(), entity, true, true, (ability, stack) -> {
                        for (MobEffectProvider provider : ability.effects) {
                            entity.addEffect(provider.createEffect(foodPointsRestored));
                        }
                    }
            );
        }
    }

    @Override
    public void addToTooltip(TooltipWriter writer) {
        for (MobEffectProvider provider : effects) {
            if (provider.mobEffect().equals(MobEffects.DIG_SPEED) && provider.condition() == AbilityCondition.ALWAYS && provider.isNonCosmetic()) {
                writer.add("haste");
            }
        }
    }
}
