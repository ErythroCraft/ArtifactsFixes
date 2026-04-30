package artifacts.component.ability.retaliation;

import artifacts.component.ability.EquipmentAbility;
import artifacts.config.value.Value;
import artifacts.config.value.ValueTypes;
import artifacts.equipment.EquipmentSlotAccess;
import artifacts.registry.ModDataComponents;
import artifacts.util.DamageSourceHelper;
import artifacts.util.ItemStackUtil;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

public abstract class RetaliationEffect implements EquipmentAbility {

    private final String name;
    private final ActivationParams activationParams;

    public RetaliationEffect(String name, ActivationParams activationParams) {
        this.name = name;
        this.activationParams = activationParams;
    }

    public ActivationParams activationParams() {
        return activationParams;
    }

    public void onLivingHurt(LivingEntity entity, EquipmentSlotAccess slotAccess, DamageSource damageSource) {
        LivingEntity attacker = DamageSourceHelper.getAttacker(damageSource);
        if (attacker != null && !slotAccess.get().has(ModDataComponents.DISABLED_BY_TOGGLE.get()) && entity.getRandom().nextDouble() < activationParams.strikeChance.get()) {
            if (applyEffect(entity, attacker)) {
                if (entity instanceof Player player && activationParams.cooldown.get() > 0) {
                    player.getCooldowns().addCooldown(slotAccess.get(), activationParams.cooldown.get() * 20);
                }
                if (activationParams.durabilityCost.get() > 0) {
                    ItemStackUtil.hurtAndBreak(slotAccess, activationParams.durabilityCost.get(), entity);
                }
            }
        }
    }

    protected abstract boolean applyEffect(LivingEntity target, LivingEntity attacker);

    @Override
    public boolean isNonCosmetic() {
        return !Mth.equal(activationParams.strikeChance.get(), 0);
    }

    @Override
    public void addToTooltip(TooltipWriter writer) {
        if (Mth.equal(activationParams.strikeChance.get(), 1)) {
            writer.add(name + ".constant");
        } else {
            writer.add(name + ".chance", Math.round(activationParams.strikeChance.get() * 100));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RetaliationEffect that)) return false;

        return activationParams.equals(that.activationParams);
    }

    @Override
    public int hashCode() {
        return Objects.hash(activationParams);
    }

    public record ActivationParams(
            Value<Double> strikeChance,
            Value<Integer> cooldown,
            Value<Integer> durabilityCost
    ) {

        public static final MapCodec<ActivationParams> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ValueTypes.FRACTION.codec().fieldOf("chance").forGetter(ActivationParams::strikeChance),
                ValueTypes.cooldownField().forGetter(ActivationParams::cooldown),
                ValueTypes.NON_NEGATIVE_INT.codec().fieldOf("damage_per_activation").forGetter(ActivationParams::durabilityCost)
        ).apply(instance, ActivationParams::new));

        public static final StreamCodec<ByteBuf, ActivationParams> STREAM_CODEC = StreamCodec.composite(
                ValueTypes.FRACTION.streamCodec(),
                ActivationParams::strikeChance,
                ValueTypes.DURATION.streamCodec(),
                ActivationParams::cooldown,
                ValueTypes.NON_NEGATIVE_INT.streamCodec(),
                ActivationParams::durabilityCost,
                ActivationParams::new
        );
    }
}
