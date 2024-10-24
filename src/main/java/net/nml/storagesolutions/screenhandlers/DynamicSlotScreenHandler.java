package net.nml.storagesolutions.screenhandlers;

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

		WItemSlot grid = WItemSlot.of(inventory, 0, columns, Math.min(MAX_ROWS, rows));
		CWScrollBar scrollbar = new CWScrollBar(Axis.VERTICAL); // Vertical scrollbar
		CWScrollPanel scroll = new CWScrollPanel(grid, scrollbar);
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

			scrollbar.setChangeListener(value -> {
				this.scrollOffset = value;
				this.slots.clear();
				WItemSlot grid2 = WItemSlot.of(inventory, columns * scrollOffset, columns, MAX_ROWS);
				scroll.widget = grid2;
				root.validate(this);
			});
		}
		root.add(scroll, 0, 10, columns * 18 + 9, height - 13);

		root.add(this.createPlayerInventoryPanel(), xOffset, height);
		root.validate(this);
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
			if (widget instanceof WPanel)
				((WPanel) widget).layout();
			children.add(widget);
			widget.setLocation(0, 0);
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
