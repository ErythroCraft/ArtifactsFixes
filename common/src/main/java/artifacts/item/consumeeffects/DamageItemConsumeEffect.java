package artifacts.item.consumeeffects;

import artifacts.config.value.Value;
import artifacts.config.value.ValueTypes;
import artifacts.registry.ModConsumeEffects;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.level.Level;

public record DamageItemConsumeEffect(Value<Integer> amount) implements ConsumeEffect {

    public static final MapCodec<HealConsumeEffect> CODEC = ValueTypes.NON_NEGATIVE_INT.codec()
            .fieldOf("amount")
            .xmap(HealConsumeEffect::new, HealConsumeEffect::amount);

    public static final StreamCodec<RegistryFriendlyByteBuf, HealConsumeEffect> STREAM_CODEC = StreamCodec.composite(
            ValueTypes.NON_NEGATIVE_INT.streamCodec(),
            HealConsumeEffect::amount, HealConsumeEffect::new
    );

    @Override
    public Type<? extends ConsumeEffect> getType() {
        return ModConsumeEffects.DAMAGE_ITEM.value();
    }

    @Override
    public boolean apply(Level level, ItemStack itemStack, LivingEntity livingEntity) {
        if (amount.get() > 0){
            itemStack.hurtAndBreak(amount.get(), livingEntity, InteractionHand.MAIN_HAND);
            return true;
        }
        return false;
    }
}
