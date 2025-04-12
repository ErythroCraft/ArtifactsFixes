package artifacts.component;

import artifacts.component.ability.SwimInAirAbility;
import artifacts.network.NetworkHandler;
import artifacts.network.SwimPacket;
import net.minecraft.world.entity.LivingEntity;

public class SwimData {

    protected boolean shouldSwim;
    protected boolean hasTouchedWater;
    protected int swimTime;

    public boolean isSwimming() {
        return shouldSwim;
    }

    public boolean isWet() {
        return hasTouchedWater;
    }

    public int getSwimTime() {
        return swimTime;
    }

    public void setSwimming(LivingEntity entity, boolean shouldSwim) {
        if (this.shouldSwim && !shouldSwim) {
            int rechargeTime = SwimInAirAbility.getRechargeDuration(entity);
            int maxFlightTime = Math.max(1, SwimInAirAbility.getFlightDuration(entity));

            setSwimTime((int) (-rechargeTime * getSwimTime() / (float) maxFlightTime));
        }

        this.shouldSwim = shouldSwim;
    }

    public void setWet(boolean hasTouchedWater) {
        this.hasTouchedWater = hasTouchedWater;
    }

    public void setSwimTime(int swimTime) {
        this.swimTime = swimTime;
    }

    public void syncSwimming() {
        NetworkHandler.sendToServer(new SwimPacket(shouldSwim));
    }
}
