package artifacts.component.ability.retaliation;

import artifacts.component.ability.EquipmentAbility;
import artifacts.config.value.Value;
import artifacts.config.value.ValueTypes;
import artifacts.registry.ModDataComponents;
import artifacts.util.DamageSourceHelper;
import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public abstract class RetaliationEffect implements EquipmentAbility {

    private final String name;
    private final Value<Double> strikeChance;
    private final Value<Integer> cooldown;

    protected static <T extends RetaliationEffect> Products.P2<RecordCodecBuilder.Mu<T>, Value<Double>, Value<Integer>> codecStart(RecordCodecBuilder.Instance<T> instance) {
        return instance.group(
                ValueTypes.FRACTION.codec().fieldOf("chance").forGetter(RetaliationEffect::strikeChance),
                ValueTypes.cooldownField().forGetter(RetaliationEffect::cooldown)
        );
    }

    public RetaliationEffect(String name, Value<Double> strikeChance, Value<Integer> cooldown) {
        this.name = name;
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
    public void addToTooltip(TooltipWriter writer) {
        if (Mth.equal(strikeChance().get(), 1)) {
            writer.add(name + ".constant");
        } else {
            writer.add(name + ".chance", Math.round(strikeChance().get() * 100));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RetaliationEffect that)) return false;

        return strikeChance.equals(that.strikeChance) && cooldown.equals(that.cooldown);
    }

    @Override
    public int hashCode() {
        int result = strikeChance.hashCode();
        result = 31 * result + cooldown.hashCode();
        return result;
    }
}
