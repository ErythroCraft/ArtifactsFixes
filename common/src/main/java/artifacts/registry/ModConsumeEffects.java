package artifacts.registry;

import artifacts.item.consumeeffects.HealConsumeEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.consume_effects.ConsumeEffect;

public class ModConsumeEffects {

    public static final Register<ConsumeEffect.Type<?>> CONSUME_EFFECT_TYPES = Register.create(Registries.CONSUME_EFFECT_TYPE);

    public static final Holder<ConsumeEffect.Type<?>> HEAL = CONSUME_EFFECT_TYPES.register("heal", () -> new ConsumeEffect.Type<>(HealConsumeEffect.CODEC, HealConsumeEffect.STREAM_CODEC));

}
