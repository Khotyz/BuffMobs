package com.khotyz.buffmobs.config;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
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
    private double savedScroll;

    public StringListEditScreen(BuffMobsConfigScreen parent, ConfigMenu.Option<List<String>> option) {
        super(option.label().copy().withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        this.parent = parent;
        this.option = option;
    }

    @Override
    protected void init() {
        double scroll = list != null ? list.scrollAmount() : savedScroll;
        list = new EntryList(this.minecraft, this.width - 24,
                Math.max(ROW_HEIGHT, this.height - 32 - 34), 28);
        list.setX(12);
        for (String value : option.value()) list.addValue(value);
        list.setScrollAmount(scroll);
        addRenderableWidget(list);

        addRenderableWidget(Button.builder(
                        Component.translatable("buffmobs.config.menu.addEntry").withStyle(ChatFormatting.AQUA),
                        button -> {
                            ListEntry entry = list.addValue("");
                            commit();
                            list.centerOn(entry);
                        })
                .bounds(this.width / 2 - BUTTON_WIDTH - 4, this.height - 28, BUTTON_WIDTH, ROW_HEIGHT)
                .build());

        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> onClose())
                .bounds(this.width / 2 + 4, this.height - 28, BUTTON_WIDTH, ROW_HEIGHT)
                .build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 10, 0xFFFFFF);
    }

    @Override
    public void onClose() {
        commit();
        parent.refreshEntries();
        if (this.minecraft != null) this.minecraft.setScreen(parent);
    }

    @Override
    public void removed() {
        commit();
        super.removed();
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
                        list.removeValue(this);
                        commit();
                    }).build();
            this.removeButton.setWidth(REMOVE_WIDTH);
            this.removeButton.setHeight(ROW_HEIGHT);
            this.widgets.add(this.removeButton);
        }

        @Override
        public void renderContent(GuiGraphics graphics, int mouseX, int mouseY, boolean hovering, float partialTick) {
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
            for (AbstractWidget widget : widgets) widget.render(graphics, mouseX, mouseY, partialTick);
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
