package artifacts.ability.retaliation;

import artifacts.ability.ArtifactAbility;
import artifacts.config.value.Value;
import artifacts.config.value.ValueTypes;
import artifacts.registry.ModDataComponents;
import artifacts.util.DamageSourceHelper;
import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public abstract class RetaliationAbility implements ArtifactAbility {

    private final Value<Double> strikeChance;
    private final Value<Integer> cooldown;

    protected static <T extends RetaliationAbility> Products.P2<RecordCodecBuilder.Mu<T>, Value<Double>, Value<Integer>> codecStart(RecordCodecBuilder.Instance<T> instance) {
        return instance.group(
                ValueTypes.FRACTION.codec().fieldOf("chance").forGetter(RetaliationAbility::strikeChance),
                ValueTypes.cooldownField().forGetter(RetaliationAbility::cooldown)
        );
    }

    public RetaliationAbility(Value<Double> strikeChance, Value<Integer> cooldown) {
        this.strikeChance = strikeChance;
        this.cooldown = cooldown;
    }

    public Value<Double> strikeChance() {
        return strikeChance;
    }

    public Value<Integer> cooldown() {
        return cooldown;
    }

    public void onLivingHurt(LivingEntity entity, ItemStack stack, DamageSource damageSource) {
        LivingEntity attacker = DamageSourceHelper.getAttacker(damageSource);
        if (attacker != null && !stack.has(ModDataComponents.DISABLED_BY_TOGGLE.get()) && entity.getRandom().nextDouble() < strikeChance().get()) {
            applyEffect(entity, attacker);
            if (entity instanceof Player player && cooldown().get() > 0) {
                player.getCooldowns().addCooldown(stack.getItem(), cooldown().get() * 20);
            }
        }
    }

    protected abstract void applyEffect(LivingEntity target, LivingEntity attacker);

    @Override
    public boolean isNonCosmetic() {
        return !Mth.equal(strikeChance().get(), 0);
    }

    @Override
    public void addAbilityTooltip(List<MutableComponent> tooltip) {
        if (Mth.equal(strikeChance().get(), 1)) {
            tooltip.add(tooltipLine("constant"));
        } else {
            tooltip.add(tooltipLine("chance"));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RetaliationAbility that)) return false;

        return strikeChance.equals(that.strikeChance) && cooldown.equals(that.cooldown);
    }

    @Override
    public int hashCode() {
        int result = strikeChance.hashCode();
        result = 31 * result + cooldown.hashCode();
        return result;
    }
}
