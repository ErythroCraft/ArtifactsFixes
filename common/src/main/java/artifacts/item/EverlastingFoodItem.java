package artifacts.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

// TODO rewrite/redesign everlasting foods to avoid mod conflicts https://github.com/ochotonida/artifacts/issues/56, https://github.com/ochotonida/artifacts/issues/201
public class EverlastingFoodItem extends ArtifactItem {

    private final Supplier<Integer> eatingCooldown;
    private final Supplier<Boolean> isEnabled;

    public EverlastingFoodItem(Properties properties, Supplier<Integer> eatingCooldown, Supplier<Boolean> isEnabled) {
        super(properties);
        this.eatingCooldown = eatingCooldown;
        this.isEnabled = isEnabled;
    }

    @Override
    public boolean isCosmetic() {
        return !isEnabled.get();
    }

    @Override
    protected String getTooltipItemName() {
        return "everlasting_food";
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        // TODO add a separate data component for infinite consumables
        Consumable consumable = stack.get(DataComponents.CONSUMABLE);
        if (consumable == null) {
            return stack;
        }
        consumable.onConsume(level, entity, stack.copy());
        // TODO move this to a consumablelistener data component
        if (eatingCooldown.get() > 0 && !entity.level().isClientSide() && entity instanceof Player player) {
            player.getCooldowns().addCooldown(stack, eatingCooldown.get() * 20);
        }
        return stack;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!isEnabled.get()) {
            return InteractionResult.PASS;
        }
        return super.use(level, player, hand);
    }
}


