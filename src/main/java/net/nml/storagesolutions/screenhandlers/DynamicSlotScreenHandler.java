package net.nml.storagesolutions.screenhandlers;

import java.util.function.IntConsumer;

import org.jetbrains.annotations.Nullable;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.ValidatedSlot;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WPanel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WScrollBar;
import io.github.cottonmc.cotton.gui.widget.WScrollPanel;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandlerContext;
import net.nml.storagesolutions.Utils;
import net.nml.storagesolutions.mixin.WItemSlotMixin;
import net.nml.storagesolutions.registers.RegisterOthers;

public class DynamicSlotScreenHandler extends SyncedGuiDescription {
	private Inventory inventory;
	private int scrollOffset = 0;
	private static final int MAX_ROWS = 12;
	private CWItemSlot grid;

	public DynamicSlotScreenHandler(int syncId, PlayerInventory playerInventory, int slotCount,
			ScreenHandlerContext context) {
		super(RegisterOthers.DYNAMIC_SLOT_SCREEN_HANDLER_TYPE, syncId, playerInventory,
				getBlockInventory(context, slotCount), null);
		inventory = this.blockInventory;
		// StorageSolutionsLLC.LOGGER.info("Slots: " + slotCount + "; on side: "+
		// ((this.world instanceof ClientWorld) ? "client" : "server"));

		int rows = Utils.calculateRows(slotCount);
		int columns = slotCount / rows;
		int xOffset = columns > 9 ? 9 * (columns - 9) : 0;
		int height = 13 + 18 * Math.min(rows, MAX_ROWS);

		if (columns < 9) {
			setTitleAlignment(HorizontalAlignment.CENTER);
		}

		WPlainPanel root = new WPlainPanel();
		setRootPanel(root);

		grid = new CWItemSlot(inventory, 0, columns, rows);

		CWScrollBar scrollbar = new CWScrollBar(Axis.VERTICAL);
		scrollbar.setMaxValue(rows);
		scrollbar.setWindow(MAX_ROWS);

		CWScrollPanel scroll = new CWScrollPanel(grid, scrollbar);
		scroll.setScrollingVertically(TriState.FALSE);
		scroll.setScrollingHorizontally(TriState.FALSE);

		root.setInsets(Insets.ROOT_PANEL);
		if (rows > MAX_ROWS) {
			root.add(scrollbar, columns * 18 + 1, 10, 9, height - 13);
			scrollbar.setChangeListener(value -> {
				this.scrollOffset = value;
				this.slots.clear();
				root.validate(this);
			});
		}
		root.add(scroll, columns < 9 ? 9 * (9 - columns) : 0, 10, columns * 18, height - 13);
		root.add(this.createPlayerInventoryPanel(), xOffset, height);
		root.validate(this);
	}

	private class CWItemSlot extends WItemSlot {
		int columns;

		public CWItemSlot(Inventory inventory, int startIndex, int slotsWide, int slotsHigh) {
			super(inventory, startIndex, slotsWide, slotsHigh, false);
			this.columns = slotsWide;
		}

		@Override
		public void validate(GuiDescription host) {
			super.validate(host);
			int i = 0;
			for (ValidatedSlot peer : ((WItemSlotMixin) this).getPeers()) {
				peer.setVisible(i >= columns * scrollOffset && i < (MAX_ROWS + scrollOffset) * columns);
				i++;
			}
		}
	}

	private class CWScrollPanel extends WScrollPanel {
		private CWScrollBar scrollBar;
		public WWidget widget;

		public CWScrollPanel(WWidget widget, CWScrollBar scrollBar) {
			super(widget);
			this.widget = widget;
			this.scrollBar = scrollBar;
			children.clear();
		}

		@Override
		public InputResult onMouseScroll(int x, int y, double amount) {
			return scrollBar.onMouseScroll(0, 0, amount);
		}

		@Override
		public void layout() {
			children.clear();
			if (widget instanceof WPanel panel)
				panel.layout();
			children.add(widget);
			grid.setLocation(0, -18 * scrollOffset);
		}
	}

	private class CWScrollBar extends WScrollBar {
		private static final int SCROLLING_SPEED = 1;
		@Nullable
		private IntConsumer onChange;

		public CWScrollBar(Axis axis) {
			super(axis);
		}

		@Environment(EnvType.CLIENT)
		@Override
		public InputResult onMouseScroll(int x, int y, double amount) {
			setValue(getValue() + (int) -amount * SCROLLING_SPEED);
			if (this.onChange != null) {
				this.onChange.accept(getValue());
			}
			return InputResult.PROCESSED;
		}

		@Override
		public InputResult onKeyPressed(int ch, int key, int modifiers) {
			InputResult ret = super.onKeyPressed(ch, key, modifiers);
			if (this.onChange != null) {
				this.onChange.accept(getValue());
			}
			return ret;
		}

		@Override
		protected void adjustSlider(int x, int y) {
			super.adjustSlider(x, y);
			if (this.onChange != null) {
				this.onChange.accept(getValue());
			}
		}

		public void setChangeListener(IntConsumer onChange) {
			this.onChange = onChange;
		}
	}
}
