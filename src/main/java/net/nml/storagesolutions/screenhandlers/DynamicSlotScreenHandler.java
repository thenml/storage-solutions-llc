package net.nml.storagesolutions.screenhandlers;

import java.util.HashSet;
import java.util.Iterator;
import java.util.function.IntConsumer;

import org.jetbrains.annotations.Nullable;

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
import net.nml.storagesolutions.StorageSolutionsLLC;
import net.nml.storagesolutions.Utils;
import net.nml.storagesolutions.mixin.WItemSlotMixin;

public class DynamicSlotScreenHandler extends SyncedGuiDescription {
	private Inventory inventory;
	private int scrollOffset = 0; // Current scroll position
	private static final int MAX_ROWS = 12;

	public DynamicSlotScreenHandler(int syncId, PlayerInventory playerInventory, int slotCount,
			ScreenHandlerContext context) {
		super(StorageSolutionsLLC.DYNAMIC_SLOT_SCREEN_HANDLER_TYPE, syncId, playerInventory,
				getBlockInventory(context, slotCount), null);
		inventory = this.blockInventory;
		// StorageSolutionsLLC.LOGGER.info("Slots: " + slotCount + "; on side: "+
		// ((this.world instanceof ClientWorld) ? "client" : "server"));

		int rows = Utils.calculateRows(slotCount);
		int columns = slotCount / rows;

		WPlainPanel root = new WPlainPanel();
		setRootPanel(root);

		if (columns < 9) {
			setTitleAlignment(HorizontalAlignment.CENTER);
		}

		WItemSlot grid = WItemSlot.of(inventory, 0, columns, rows);
		CWScrollBar scrollbar = new CWScrollBar(Axis.VERTICAL); // Vertical scrollbar
		WScrollPanel scroll = new CWScrollPanel(grid, scrollbar);
		scrollbar.setMaxValue(rows); // Max scroll value based on total rows
		scrollbar.setWindow(MAX_ROWS);
		scrollbar.setValue(scrollOffset); // Set current scroll position
		scroll.setScrollingVertically(TriState.FALSE);
		scroll.setScrollingHorizontally(TriState.FALSE);

		root.setInsets(Insets.ROOT_PANEL);
		int xOffset = columns > 9 ? 9 * (columns - 9) : 0;
		int height = 13 + 18 * Math.min(rows, MAX_ROWS);

		if (rows > MAX_ROWS) {
			root.add(scrollbar, columns * 18 + 1, 10, 9, height - 13);

			updateSlots(grid, rows, columns);
			scrollbar.setChangeListener(value -> {
				scroll.setScrollingVertically(TriState.TRUE);
				this.scrollOffset = value;
				updateSlots(grid, rows, columns);
			});
		}
		root.add(scroll, 0, 10, columns * 18 + 9, height - 13);

		root.add(this.createPlayerInventoryPanel(), xOffset, height);
		root.validate(this);
	}

	private void updateSlots(WItemSlot grid, int rows, int columns) {
		// StorageSolutionsLLC.LOGGER.info("ScrollOffset: " + scrollOffset);
		int lastId = columns * scrollOffset;
		Iterator<ValidatedSlot> slots = ((WItemSlotMixin) grid).getPeers().iterator();
		grid.setLocation(0, -scrollOffset);
		for (int i = 0; slots.hasNext(); i++) {
			ValidatedSlot slot = slots.next();
			slot.setVisible(i - lastId >= 0 && i < MAX_ROWS * columns + lastId);
			// TODO: modify slot.y, somehow
		}
	}

	private class CWScrollPanel extends WScrollPanel {
		private CWScrollBar scrollBar;
		private WWidget widget;

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
			children.add(widget);
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
			if (this.onChange != null) {
				this.onChange.accept(getValue());
			}
			return super.onKeyPressed(ch, key, modifiers);
		}

		@Override
		protected void adjustSlider(int x, int y) {
			if (this.onChange != null) {
				this.onChange.accept(getValue());
			}
			super.adjustSlider(x, y);
		}

		public void setChangeListener(IntConsumer onChange) {
			this.onChange = onChange;
		}
	}
}
