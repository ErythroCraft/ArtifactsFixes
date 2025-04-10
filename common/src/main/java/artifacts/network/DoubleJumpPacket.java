package artifacts.network;

import artifacts.Artifacts;
import artifacts.ability.DoubleJumpAbility;
import artifacts.registry.ModDataComponents;
import artifacts.util.AbilityHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public record DoubleJumpPacket() implements CustomPacketPayload {

    public static final Type<DoubleJumpPacket> TYPE = new Type<>(Artifacts.id("double_jump"));

    public static final StreamCodec<FriendlyByteBuf, DoubleJumpPacket> CODEC = StreamCodec.unit(new DoubleJumpPacket());

    void apply(NetworkHandler.PayloadContext context) {
        if (context.player() instanceof ServerPlayer player && AbilityHelper.hasAbilityActive(ModDataComponents.DOUBLE_JUMP.get(), player, true)) {
            context.queue(() -> {
                DoubleJumpAbility.jump(player);

                for (int i = 0; i < 20; ++i) {
                    double motionX = player.getRandom().nextGaussian() * 0.02;
                    double motionY = player.getRandom().nextGaussian() * 0.02 + 0.20;
                    double motionZ = player.getRandom().nextGaussian() * 0.02;
                    ParticleOptions particleType = player.isInWater() ? ParticleTypes.BUBBLE : ParticleTypes.POOF;
                    player.serverLevel().sendParticles(particleType, player.getX(), player.getY(), player.getZ(), 1, motionX, motionY, motionZ, 0.15);
                }
            });
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
