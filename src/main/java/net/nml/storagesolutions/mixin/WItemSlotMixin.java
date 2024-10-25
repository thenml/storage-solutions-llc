package net.nml.storagesolutions.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import io.github.cottonmc.cotton.gui.ValidatedSlot;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;

@Mixin(WItemSlot.class)
public interface WItemSlotMixin {
	@Accessor
	List<ValidatedSlot> getPeers();
}
