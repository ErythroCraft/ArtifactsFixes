package artifacts.component.ability.mobeffect;

import artifacts.component.ability.EntityCondition;
import artifacts.component.ability.EquipmentAbility;
import artifacts.equipment.EquipmentHelper;
import artifacts.registry.ModDataComponents;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;

public record PostEatingEffect(MobEffectProvider provider) implements EquipmentAbility {

    public static final Codec<PostEatingEffect> CODEC =
            MobEffectProvider.codec(false).xmap(PostEatingEffect::new, PostEatingEffect::provider);
    public static final StreamCodec<RegistryFriendlyByteBuf, PostEatingEffect> STREAM_CODEC =
            MobEffectProvider.STREAM_CODEC.map(PostEatingEffect::new, PostEatingEffect::provider);

    public static void applyEffects(LivingEntity entity, FoodProperties properties) {
        int foodPointsMissing = entity instanceof Player player ? 20 - player.getFoodData().getFoodLevel() : 20;
        int foodPointsRestored = Math.min(properties.nutrition(), foodPointsMissing);
        applyEffects(entity, foodPointsRestored);
    }

    public static void applyEffects(LivingEntity entity, int foodPointsRestored) {
        if (foodPointsRestored > 0) {
            EquipmentHelper.iterateAbilities(ModDataComponents.POST_EATING_EFFECTS.get(), entity, true, true, (ability, stack) -> {
                for (PostEatingEffect entry : ability.entries()) {
                    entity.addEffect(entry.provider().createEffect(foodPointsRestored));
                }
            });
        }
    }

    @Override
    public boolean isNonCosmetic() {
        return provider().isNonCosmetic();
    }

    @Override
    public void addToTooltip(TooltipWriter writer) {
        if (provider.mobEffect().equals(MobEffects.HASTE) && provider.condition() == EntityCondition.ALWAYS) {
            writer.add("haste");
        }
    }
}
