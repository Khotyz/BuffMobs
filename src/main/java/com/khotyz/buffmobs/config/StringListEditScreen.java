package com.khotyz.buffmobs.config;

import com.khotyz.buffmobs.util.Guard;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class StringListEditScreen extends Screen {

    private static final int ROW_HEIGHT = 20;
    private static final int REMOVE_WIDTH = 64;
    private static final int BUTTON_WIDTH = 150;

    private final BuffMobsConfigScreen parent;
    private final ConfigMenu.Option<List<String>> option;
    private EntryList list;
    private Button addButton;
    private Button doneButton;
    private double savedScroll;

    public StringListEditScreen(BuffMobsConfigScreen parent, ConfigMenu.Option<List<String>> option) {
        super(option.label().copy().withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        this.parent = parent;
        this.option = option;
    }

    @Override
    protected void init() {
        try {
            double scroll = list != null ? list.scrollAmount() : savedScroll;
            int titleHeight = font.split(title, Math.max(1, width - 24)).size() * font.lineHeight;
            int listTop = Math.max(28, 8 + titleHeight + 8);
            list = new EntryList(this.minecraft, this.width - 24,
                    Math.max(ROW_HEIGHT, this.height - listTop - 34), listTop);
            list.setX(12);
            for (String value : option.value()) list.addValue(value);
            list.setScrollAmount(scroll);
            addRenderableWidget(list);

            addButton = Button.builder(
                            Component.translatable("buffmobs.config.menu.addEntry").withStyle(ChatFormatting.AQUA),
                            button -> {
                                ListEntry entry = list.addValue("");
                                commit();
                                list.centerOn(entry);
                            })
                    .bounds(this.width / 2 - BUTTON_WIDTH - 4, this.height - 28, BUTTON_WIDTH, ROW_HEIGHT)
                    .build();
            addRenderableWidget(addButton);

            doneButton = Button.builder(CommonComponents.GUI_DONE, button -> onClose())
                    .bounds(this.width / 2 + 4, this.height - 28, BUTTON_WIDTH, ROW_HEIGHT)
                    .build();
            addRenderableWidget(doneButton);
        } catch (Throwable throwable) {
            Guard.report("StringListEditScreen.init", throwable);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        try {
            super.extractRenderState(graphics, mouseX, mouseY, partialTick);
            var titleLines = font.split(title, Math.max(1, width - 24));
            for (int index = 0; index < titleLines.size(); index++) {
                var line = titleLines.get(index);
                graphics.text(font, line, width / 2 - font.width(line) / 2,
                        8 + index * font.lineHeight, 0xFFFFFF);
            }
        } catch (Throwable throwable) {
            Guard.report("StringListEditScreen.extractRenderState", throwable);
        }
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        try {
            if (BuffMobsConfigScreen.isEscapeKey(event)) {
                onClose();
                return true;
            }
            return super.keyPressed(event);
        } catch (Throwable throwable) {
            Guard.report("StringListEditScreen.keyPressed", throwable);
            return false;
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        try {
            if (doneButton != null && BuffMobsConfigScreen.isLeftMouseButton(event)
                    && doneButton.isMouseOver(event.x(), event.y())) {
                setFocused(doneButton);
                onClose();
                return true;
            }
            if (addButton != null && addButton.isMouseOver(event.x(), event.y())
                    && addButton.mouseClicked(event, doubleClick)) return true;
            if (list != null) {
                ListEntry entry = list.entryAt(event.x(), event.y());
                if (entry != null) {
                    list.setFocused(entry);
                    setFocused(list);
                    if (entry.mouseClicked(event, doubleClick)) return true;
                }
            }
            return super.mouseClicked(event, doubleClick);
        } catch (Throwable throwable) {
            Guard.report("StringListEditScreen.mouseClicked", throwable);
            return false;
        }
    }

    @Override
    public void onClose() {
        try {
            commit();
            parent.refreshEntries();
            if (this.minecraft != null) MinecraftScreenCompat.set(this.minecraft, parent);
        } catch (Throwable throwable) {
            Guard.report("StringListEditScreen.onClose", throwable);
        }
    }

    @Override
    public void removed() {
        try {
            commit();
            super.removed();
        } catch (Throwable throwable) {
            Guard.report("StringListEditScreen.removed", throwable);
        }
    }

    private void commit() {
        if (list == null) return;
        savedScroll = list.scrollAmount();
        List<String> values = new ArrayList<>();
        for (ListEntry entry : list.children()) values.add(entry.editBox.getValue());
        option.set(values);
    }

    private final class EntryList extends ContainerObjectSelectionList<ListEntry> {

        EntryList(Minecraft minecraft, int width, int height, int y) {
            super(minecraft, width, height, y, ROW_HEIGHT + 4);
        }

        ListEntry addValue(String value) {
            ListEntry entry = new ListEntry(value);
            addEntry(entry);
            return entry;
        }

        ListEntry entryAt(double mouseX, double mouseY) {
            return getEntryAtPosition(mouseX, mouseY);
        }

        void removeValue(ListEntry entry) {
            removeEntry(entry);
        }

        void centerOn(ListEntry entry) {
            centerScrollOn(entry);
        }

        @Override
        public int getRowWidth() {
            return Math.max(180, Math.min(this.width - 24, 430));
        }

        @Override
        protected int scrollBarX() {
            return this.getRight() - 5;
        }
    }

    private final class ListEntry extends ContainerObjectSelectionList.Entry<ListEntry> {

        private final EditBox editBox;
        private final Button removeButton;
        private final List<AbstractWidget> widgets = new ArrayList<>();

        ListEntry(String value) {
            this.editBox = new EditBox(StringListEditScreen.this.font, 0, 0, 120, ROW_HEIGHT, option.label());
            this.editBox.setMaxLength(1024);
            this.editBox.setValue(value == null ? "" : value);
            this.widgets.add(this.editBox);
            this.removeButton = Button.builder(
                    Component.translatable("buffmobs.config.menu.removeEntry").withStyle(ChatFormatting.RED),
                    button -> {
                        StringListEditScreen.this.list.removeValue(this);
                        commit();
                    }).build();
            this.removeButton.setWidth(REMOVE_WIDTH);
            this.removeButton.setHeight(ROW_HEIGHT);
            this.widgets.add(this.removeButton);
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovering, float partialTick) {
            try {
                int left = getX();
                int top = getY();
                int width = getWidth();
                int height = getHeight();
                if (hovering) graphics.fill(left - 4, top + 1, left + width + 4, top + height - 1, 0x24FFFFFF);
                graphics.fill(left - 4, top + 4, left - 2, top + height - 4, 0xFF55FFFF);

                this.editBox.setX(left);
                this.editBox.setY(top);
                this.editBox.setWidth(Math.max(40, width - REMOVE_WIDTH - 6));
                this.editBox.setHeight(ROW_HEIGHT);
                this.removeButton.setX(left + width - REMOVE_WIDTH);
                this.removeButton.setY(top);
                for (AbstractWidget widget : widgets) widget.extractRenderState(graphics, mouseX, mouseY, partialTick);
            } catch (Throwable throwable) {
                Guard.report("ListEntry.extractContent", throwable);
            }
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return widgets;
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return widgets;
        }
    }
}
