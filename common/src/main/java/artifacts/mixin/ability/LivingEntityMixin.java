package artifacts.mixin.ability;

import artifacts.event.ArtifactHooks;
import artifacts.extensions.ability.LivingEntityExtensions;
import com.llamalad7.mixinextras.injector.ModifyReceiver;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements LivingEntityExtensions {

    @Unique
    private boolean artifacts$hasTickingAbilities;

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
        throw new UnsupportedOperationException();
    }

    @Unique
    @Override
    public boolean artifacts$hasTickingAbilities() {
        return artifacts$hasTickingAbilities;
    }

    @Unique
    @Override
    public void artifacts$setTickingAbilities(boolean hasTickingAbilities) {
        this.artifacts$hasTickingAbilities = hasTickingAbilities;
    }

    @Accessor
    protected abstract Map<EquipmentSlot, ItemStack> getLastEquipmentItems();

    @Shadow
    public abstract ItemStack getItemBySlot(EquipmentSlot slot);

    @Inject(method = "handleEquipmentChanges", at = @At("HEAD"))
    private void handleEquipmentChanges(Map<EquipmentSlot, ItemStack> changedItems, CallbackInfo info) {
        for (EquipmentSlot slot : changedItems.keySet()) {
            if (!slot.isArmor()) {
                continue;
            }
            ItemStack oldStack = getLastEquipmentItems().getOrDefault(slot, ItemStack.EMPTY);
            ItemStack newStack = getItemBySlot(slot);

            ArtifactHooks.onItemChanged((LivingEntity) (Object) this, oldStack, newStack);
        }
    }

    // Using ModifyReceiver here to reliably capture the final damage value applied to the entity,
    // after armor and effects. The entity being damaged remains the same.
    @ModifyReceiver(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setHealth(F)V"))
    private LivingEntity onEntityDamaged(LivingEntity instance, float health, ServerLevel level, DamageSource source) {
        ArtifactHooks.beforeLivingDamaged((LivingEntity) (Object) this, source, health);
        return instance;
    }
}
