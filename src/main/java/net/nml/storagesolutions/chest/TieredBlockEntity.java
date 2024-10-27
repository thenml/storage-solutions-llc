package net.nml.storagesolutions.chest;

import net.minecraft.util.Identifier;

// TODO: convert to class
public interface TieredBlockEntity {
	public int size();

	public Identifier getBaseBlockIdentifier();
}
