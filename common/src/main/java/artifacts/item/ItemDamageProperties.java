package artifacts.item;

public interface ItemDamageProperties {

    /**
     * Whether this item can be damaged
     */
    boolean canBeDamaged();

    /**
     * The maximum damage this item can take, must be greater than 0
     */
    int getMaxDamage();
}
