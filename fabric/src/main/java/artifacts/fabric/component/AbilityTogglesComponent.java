package artifacts.fabric.component;

import artifacts.ability.ArtifactAbility;
import artifacts.component.AbilityToggles;
import artifacts.registry.ModAbilities;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

import java.util.HashSet;
import java.util.Set;

public class AbilityTogglesComponent extends AbilityToggles implements AutoSyncedComponent {

    @Override
    public void readFromNbt(CompoundTag tag, HolderLookup.Provider provider) {
        ListTag list = tag.getList("toggles", Tag.TAG_STRING);
        Set<ArtifactAbility.Type<?>> toggles = new HashSet<>();
        for (Tag stringTag : list) {
            toggles.add(ModAbilities.getRegistry().get(ResourceLocation.parse(stringTag.getAsString())));
        }
        this.toggles.clear();
        this.toggles.addAll(toggles);
    }

    @Override
    public void writeToNbt(CompoundTag tag, HolderLookup.Provider provider) {
        ListTag list = new ListTag();
        for (ArtifactAbility.Type<?> toggle : toggles) {
            // noinspection ConstantConditions
            list.add(StringTag.valueOf(ModAbilities.getRegistry().getKey(toggle).toString()));
        }
        tag.put("toggles", list);
    }
}
