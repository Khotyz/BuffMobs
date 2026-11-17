package com.khotyz.buffmobs.config;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class StringListEditScreen extends Screen {

    private final BuffMobsConfigScreen parent;
    private final ConfigMenu.Option<List<String>> option;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    private final AutoCompleteDropdown autoComplete = new AutoCompleteDropdown();
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
        autoComplete.clear();
        layout.addTitleHeader(title, font);
        list = new EntryList(minecraft, width, layout.getContentHeight(), layout.getHeaderHeight(), 24);
        for (String value : option.value()) list.addValue(value);
        layout.addToContents(list);
        layout.visitWidgets(this::addRenderableWidget);
        addButton = addRenderableWidget(Button.builder(Component.translatable("buffmobs.config.menu.addEntry").copy().withStyle(ChatFormatting.AQUA), button -> {
            ListEntry entry = list.addValue("");
            list.centerOn(entry);
            focusEntry(entry);
        }).build());
        doneButton = addRenderableWidget(Button.builder(Component.translatable("gui.done").copy().withStyle(ChatFormatting.AQUA), button -> onClose()).build());
        repositionElements();
    }

    @Override
    protected void repositionElements() {
        if (list != null) savedScroll = list.getScrollAmount();
        layout.arrangeElements();
        if (list != null) {
            list.updateSize(width, layout);
            list.setScrollAmount(savedScroll);
        }
        if (addButton != null) {
            addButton.setX(width / 2 - 154);
            addButton.setY(height - 27);
            addButton.setWidth(150);
            addButton.setHeight(20);
        }
        if (doneButton != null) {
            doneButton.setX(width / 2 + 4);
            doneButton.setY(height - 27);
            doneButton.setWidth(150);
            doneButton.setHeight(20);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        boolean overDropdown = list != null && autoComplete.isMouseOver(
                mouseX, mouseY, list.getY(), list.getY() + list.getHeight(), height - 32);
        autoComplete.resetFrameTracking();
        int effectiveMouseX = overDropdown ? -1 : mouseX;
        int effectiveMouseY = overDropdown ? -1 : mouseY;
        super.render(guiGraphics, effectiveMouseX, effectiveMouseY, partialTick);
        if (list != null) {
            autoComplete.render(guiGraphics, font, mouseX, mouseY,
                    list.getY(), list.getY() + list.getHeight(), height - 32);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (list != null && autoComplete.mouseClicked(
                mouseX, mouseY, button, list.getY(), list.getY() + list.getHeight(), height - 32)) {
            return true;
        }
        boolean handled = super.mouseClicked(mouseX, mouseY, button);
        syncFocusedEntry();
        return handled;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (list != null && autoComplete.mouseScrolled(
                mouseX, mouseY, scrollY, list.getY(), list.getY() + list.getHeight(), height - 32)) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (list != null && autoComplete.keyPressed(
                keyCode, list.getY(), list.getY() + list.getHeight(), height - 32)) {
            return true;
        }
        boolean handled = super.keyPressed(keyCode, scanCode, modifiers);
        syncFocusedEntry();
        return handled;
    }

    @Override
    public void onClose() {
        autoComplete.clear();
        commit();
        parent.refreshEntries();
        if (minecraft != null) minecraft.setScreen(parent);
    }

    @Override
    public void removed() {
        autoComplete.clear();
        commit();
        super.removed();
    }

    private void commit() {
        if (list == null) return;
        List<String> values = new ArrayList<>();
        for (ListEntry entry : list.children()) {
            String value = entry.editBox.getValue().trim();
            if (!value.isEmpty()) values.add(value);
        }
        option.set(values);
    }

    private void focusEntry(ListEntry entry) {
        setFocused(list);
        list.setFocused(entry);
        entry.setFocused(entry.editBox);
        SuggestionProvider provider = option.suggestionProvider();
        if (provider != null) {
            autoComplete.activate(entry.editBox, provider, selected -> commit(), false);
        }
    }

    private void syncFocusedEntry() {
        SuggestionProvider provider = option.suggestionProvider();
        if (provider == null || list == null) {
            autoComplete.clear();
            return;
        }
        EditBox focusedBox = null;
        for (ListEntry entry : list.children()) {
            if (entry.editBox.isFocused()) {
                focusedBox = entry.editBox;
                break;
            }
        }
        autoComplete.syncFocusedBox(focusedBox, provider, selected -> commit());
    }

    private final class EntryList extends ContainerObjectSelectionList<ListEntry> {

        private EntryList(Minecraft minecraft, int width, int height, int y, int itemHeight) {
            super(minecraft, width, height, y, itemHeight);
        }

        private ListEntry addValue(String value) {
            ListEntry entry = new ListEntry(value);
            addEntry(entry);
            return entry;
        }

        private void removeValue(ListEntry entry) {
            removeEntry(entry);
        }

        private void centerOn(ListEntry entry) {
            centerScrollOn(entry);
        }

        @Override
        public int getRowWidth() {
            return Math.max(180, Math.min(getWidth() - 24, 430));
        }
    }

    private final class ListEntry extends ContainerObjectSelectionList.Entry<ListEntry> {

        private final EditBox editBox;
        private final Button removeButton;
        private final List<AbstractWidget> widgets = new ArrayList<>();

        private ListEntry(String value) {
            SuggestionProvider provider = option.suggestionProvider();
            editBox = new EditBox(font, 0, 0, 120, 20, option.label());
            editBox.setMaxLength(1024);
            editBox.setValue(value == null ? "" : value);
            if (provider != null) {
                editBox.setHint(provider.hint().copy().withStyle(ChatFormatting.DARK_GRAY));
                editBox.setResponder(text -> autoComplete.onTextChanged(editBox, provider, selected -> commit()));
            }
            widgets.add(editBox);
            removeButton = Button.builder(Component.translatable("buffmobs.config.menu.removeEntry").copy().withStyle(ChatFormatting.RED), button -> {
                autoComplete.onBoxRemoved(editBox);
                StringListEditScreen.this.list.removeValue(this);
                commit();
            }).build();
            widgets.add(removeButton);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return widgets;
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return widgets;
        }

        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            if (hovering) guiGraphics.fill(left - 4, top + 1, left + width + 4, top + height - 1, 0x24FFFFFF);
            guiGraphics.fill(left - 4, top + 4, left - 2, top + height - 4, 0xFF55FFFF);
            int y = top + 2;
            int removeWidth = 64;
            int gap = 6;
            editBox.setX(left);
            editBox.setY(y);
            editBox.setWidth(Math.max(40, width - removeWidth - gap));
            editBox.setHeight(20);
            autoComplete.markBoxRendered(editBox);
            removeButton.setX(left + width - removeWidth);
            removeButton.setY(y);
            removeButton.setWidth(removeWidth);
            removeButton.setHeight(20);
            for (AbstractWidget widget : widgets) widget.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }
}
