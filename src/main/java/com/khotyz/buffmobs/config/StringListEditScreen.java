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
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class StringListEditScreen extends Screen {

    private static final int MAX_VISIBLE_SUGGESTIONS = 6;
    private static final int SUGGESTION_ROW_HEIGHT = 14;
    private static final int DROPDOWN_PADDING = 2;

    private final BuffMobsConfigScreen parent;
    private final ConfigMenu.Option<List<String>> option;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    private final List<String> allSuggestions = new ArrayList<>();
    private final List<String> filteredSuggestions = new ArrayList<>();
    private EntryList list;
    private Button addButton;
    private Button doneButton;
    private ListEntry activeEntry;
    private int selectedSuggestionIndex;
    private int suggestionScrollOffset;
    private boolean dropdownDismissed;
    private boolean suppressResponder;
    private double savedScroll;

    public StringListEditScreen(BuffMobsConfigScreen parent, ConfigMenu.Option<List<String>> option) {
        super(option.label().copy().withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        this.parent = parent;
        this.option = option;
    }

    @Override
    protected void init() {
        allSuggestions.clear();
        allSuggestions.addAll(option.suggestions());
        clearSuggestionState();
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
        if (list != null) {
            for (ListEntry entry : list.children()) {
                entry.renderedThisFrame = false;
            }
        }
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderSuggestionsDropdown(guiGraphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            DropdownBounds bounds = computeDropdownBounds();
            if (bounds != null && bounds.contains(mouseX, mouseY)) {
                int row = (int) ((mouseY - (bounds.y() + DROPDOWN_PADDING)) / SUGGESTION_ROW_HEIGHT);
                if (row >= 0 && row < bounds.visibleCount()) {
                    int suggestionIndex = suggestionScrollOffset + row;
                    if (suggestionIndex >= 0 && suggestionIndex < filteredSuggestions.size()) {
                        applySuggestion(filteredSuggestions.get(suggestionIndex));
                        return true;
                    }
                }
                return true;
            }
        }

        boolean handled = super.mouseClicked(mouseX, mouseY, button);
        syncFocusedEntry();
        return handled;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        DropdownBounds bounds = computeDropdownBounds();
        if (bounds != null && bounds.contains(mouseX, mouseY)) {
            int maxOffset = Math.max(0, filteredSuggestions.size() - bounds.visibleCount());
            if (scrollY > 0) {
                suggestionScrollOffset = Math.max(0, suggestionScrollOffset - 1);
            } else if (scrollY < 0) {
                suggestionScrollOffset = Math.min(maxOffset, suggestionScrollOffset + 1);
            }
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        DropdownBounds bounds = computeDropdownBounds();
        if (bounds != null) {
            if (keyCode == GLFW.GLFW_KEY_DOWN) {
                moveSelection(1, bounds.visibleCount());
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_UP) {
                moveSelection(-1, bounds.visibleCount());
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_TAB || keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                applySuggestion(filteredSuggestions.get(selectedSuggestionIndex));
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                dismissDropdown();
                return true;
            }
        } else if (activeEntry != null && activeEntry.editBox.isFocused() && !allSuggestions.isEmpty()) {
            if (keyCode == GLFW.GLFW_KEY_DOWN) {
                dropdownDismissed = false;
                updateFilteredSuggestions(true);
                if (!filteredSuggestions.isEmpty()) {
                    return true;
                }
            }
        }

        boolean handled = super.keyPressed(keyCode, scanCode, modifiers);
        syncFocusedEntry();
        return handled;
    }

    @Override
    public void onClose() {
        commit();
        parent.refreshEntries();
        if (minecraft != null) minecraft.setScreen(parent);
    }

    @Override
    public void removed() {
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
        setActiveEntry(entry, false);
    }

    private void syncFocusedEntry() {
        ListEntry focused = findFocusedEntry();
        if (focused != activeEntry) {
            setActiveEntry(focused, false);
        }
    }

    private ListEntry findFocusedEntry() {
        if (list == null) return null;
        for (ListEntry entry : list.children()) {
            if (entry.editBox.isFocused()) return entry;
        }
        return null;
    }

    private void setActiveEntry(ListEntry entry, boolean showSuggestions) {
        if (activeEntry != null && activeEntry != entry) {
            activeEntry.editBox.setSuggestion(null);
        }
        activeEntry = entry;
        filteredSuggestions.clear();
        selectedSuggestionIndex = 0;
        suggestionScrollOffset = 0;
        dropdownDismissed = !showSuggestions;
        if (showSuggestions && activeEntry != null) {
            updateFilteredSuggestions(false);
        }
    }

    private void clearSuggestionState() {
        if (activeEntry != null) {
            activeEntry.editBox.setSuggestion(null);
        }
        activeEntry = null;
        filteredSuggestions.clear();
        selectedSuggestionIndex = 0;
        suggestionScrollOffset = 0;
        dropdownDismissed = false;
    }

    private void dismissDropdown() {
        dropdownDismissed = true;
        filteredSuggestions.clear();
        selectedSuggestionIndex = 0;
        suggestionScrollOffset = 0;
        if (activeEntry != null) {
            activeEntry.editBox.setSuggestion(null);
        }
    }

    private void onEntryValueChanged(ListEntry entry) {
        if (suppressResponder) return;
        if (activeEntry != null && activeEntry != entry) {
            activeEntry.editBox.setSuggestion(null);
        }
        activeEntry = entry;
        dropdownDismissed = false;
        updateFilteredSuggestions(false);
    }

    private void updateFilteredSuggestions(boolean allowEmptyQuery) {
        filteredSuggestions.clear();
        selectedSuggestionIndex = 0;
        suggestionScrollOffset = 0;

        if (activeEntry == null || allSuggestions.isEmpty()) {
            return;
        }

        String rawText = activeEntry.editBox.getValue();
        String query = rawText.trim().toLowerCase(Locale.ROOT);
        if (query.isEmpty() && !allowEmptyQuery) {
            activeEntry.editBox.setSuggestion(null);
            return;
        }

        List<String> matches = filterSuggestions(query);
        if (matches.size() == 1 && matches.getFirst().equalsIgnoreCase(query)) {
            activeEntry.editBox.setSuggestion(null);
            return;
        }

        filteredSuggestions.addAll(matches);
        updateInlineSuggestion();
    }

    private List<String> filterSuggestions(String query) {
        if (query.isEmpty()) {
            return new ArrayList<>(allSuggestions);
        }
        List<String> exactMatches = new ArrayList<>();
        List<String> prefixMatches = new ArrayList<>();
        List<String> pathPrefixMatches = new ArrayList<>();
        List<String> containsMatches = new ArrayList<>();

        for (String candidate : allSuggestions) {
            String lower = candidate.toLowerCase(Locale.ROOT);
            int colon = lower.indexOf(':');
            String path = colon >= 0 ? lower.substring(colon + 1) : lower;

            if (lower.equals(query) || path.equals(query)) {
                exactMatches.add(candidate);
            } else if (lower.startsWith(query)) {
                prefixMatches.add(candidate);
            } else if (path.startsWith(query)) {
                pathPrefixMatches.add(candidate);
            } else if (lower.contains(query)) {
                containsMatches.add(candidate);
            }
        }

        exactMatches.sort(Comparator.naturalOrder());
        prefixMatches.sort(Comparator.naturalOrder());
        pathPrefixMatches.sort(Comparator.naturalOrder());
        containsMatches.sort(Comparator.naturalOrder());

        List<String> results = new ArrayList<>(
                exactMatches.size() + prefixMatches.size() + pathPrefixMatches.size() + containsMatches.size());
        results.addAll(exactMatches);
        results.addAll(prefixMatches);
        results.addAll(pathPrefixMatches);
        results.addAll(containsMatches);
        return results;
    }

    private void moveSelection(int delta, int visibleCount) {
        if (filteredSuggestions.isEmpty()) return;
        int size = filteredSuggestions.size();
        selectedSuggestionIndex = Math.floorMod(selectedSuggestionIndex + delta, size);
        if (selectedSuggestionIndex < suggestionScrollOffset) {
            suggestionScrollOffset = selectedSuggestionIndex;
        } else if (selectedSuggestionIndex >= suggestionScrollOffset + visibleCount) {
            suggestionScrollOffset = selectedSuggestionIndex - visibleCount + 1;
        }
        clampScrollOffset(visibleCount);
        updateInlineSuggestion();
    }

    private void clampScrollOffset(int visibleCount) {
        int maxOffset = Math.max(0, filteredSuggestions.size() - visibleCount);
        if (suggestionScrollOffset > maxOffset) suggestionScrollOffset = maxOffset;
        if (suggestionScrollOffset < 0) suggestionScrollOffset = 0;
    }

    private void updateInlineSuggestion() {
        if (activeEntry == null) return;
        if (dropdownDismissed || filteredSuggestions.isEmpty()) {
            activeEntry.editBox.setSuggestion(null);
            return;
        }
        String current = activeEntry.editBox.getValue();
        String selected = filteredSuggestions.get(selectedSuggestionIndex);
        if (!current.isEmpty() && selected.startsWith(current)) {
            activeEntry.editBox.setSuggestion(selected.substring(current.length()));
        } else {
            activeEntry.editBox.setSuggestion(null);
        }
    }

    private void applySuggestion(String suggestion) {
        if (activeEntry == null) return;
        suppressResponder = true;
        try {
            activeEntry.editBox.setValue(suggestion);
            activeEntry.editBox.setSuggestion(null);
        } finally {
            suppressResponder = false;
        }
        dismissDropdown();
        commit();
    }

    private DropdownBounds computeDropdownBounds() {
        if (activeEntry == null || dropdownDismissed || filteredSuggestions.isEmpty()) return null;
        if (!activeEntry.editBox.isFocused() || !activeEntry.renderedThisFrame) return null;
        if (list == null) return null;

        int listTop = list.getY();
        int listBottom = list.getY() + list.getHeight();
        int boxX = activeEntry.editBox.getX();
        int boxY = activeEntry.editBox.getY();
        int boxWidth = activeEntry.editBox.getWidth();
        int boxHeight = activeEntry.editBox.getHeight();

        if (boxWidth <= 0 || boxY + boxHeight <= listTop || boxY >= listBottom) return null;

        int visibleCount = Math.min(MAX_VISIBLE_SUGGESTIONS, filteredSuggestions.size());
        int dropdownHeight = visibleCount * SUGGESTION_ROW_HEIGHT + DROPDOWN_PADDING * 2;
        int bottomLimit = height - 32;
        int spaceBelow = bottomLimit - (boxY + boxHeight + 1);
        int spaceAbove = (boxY - 1) - listTop;

        int dropdownY;
        if (spaceBelow >= dropdownHeight || spaceBelow >= spaceAbove) {
            dropdownY = boxY + boxHeight + 1;
            int maxRowsBelow = Math.max(1, (bottomLimit - dropdownY - DROPDOWN_PADDING * 2) / SUGGESTION_ROW_HEIGHT);
            visibleCount = Math.min(visibleCount, maxRowsBelow);
            dropdownHeight = visibleCount * SUGGESTION_ROW_HEIGHT + DROPDOWN_PADDING * 2;
        } else {
            int maxRowsAbove = Math.max(1, (spaceAbove - DROPDOWN_PADDING * 2) / SUGGESTION_ROW_HEIGHT);
            visibleCount = Math.min(visibleCount, maxRowsAbove);
            dropdownHeight = visibleCount * SUGGESTION_ROW_HEIGHT + DROPDOWN_PADDING * 2;
            dropdownY = boxY - dropdownHeight - 1;
        }

        return new DropdownBounds(boxX, dropdownY, boxWidth, dropdownHeight, visibleCount);
    }

    private void renderSuggestionsDropdown(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        DropdownBounds bounds = computeDropdownBounds();
        if (bounds == null) return;

        clampScrollOffset(bounds.visibleCount());
        boolean hasScrollbar = filteredSuggestions.size() > bounds.visibleCount();
        int textMaxWidth = bounds.width() - (hasScrollbar ? 12 : 8);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, 0.0F, 400.0F);

        guiGraphics.fill(bounds.x() - 1, bounds.y() - 1,
                bounds.x() + bounds.width() + 1, bounds.y() + bounds.height() + 1, 0xFF55FFFF);
        guiGraphics.fill(bounds.x(), bounds.y(),
                bounds.x() + bounds.width(), bounds.y() + bounds.height(), 0xF0101018);

        for (int row = 0; row < bounds.visibleCount(); row++) {
            int suggestionIndex = suggestionScrollOffset + row;
            if (suggestionIndex >= filteredSuggestions.size()) break;
            String suggestion = filteredSuggestions.get(suggestionIndex);
            int rowTop = bounds.y() + DROPDOWN_PADDING + row * SUGGESTION_ROW_HEIGHT;
            int rowBottom = rowTop + SUGGESTION_ROW_HEIGHT;
            boolean hovered = bounds.contains(mouseX, mouseY) && mouseY >= rowTop && mouseY < rowBottom;
            boolean selected = suggestionIndex == selectedSuggestionIndex;

            if (selected) {
                guiGraphics.fill(bounds.x() + 1, rowTop, bounds.x() + bounds.width() - 1, rowBottom, 0xFF284654);
            } else if (hovered) {
                guiGraphics.fill(bounds.x() + 1, rowTop, bounds.x() + bounds.width() - 1, rowBottom, 0x40FFFFFF);
            }

            String displayText = font.plainSubstrByWidth(suggestion, Math.max(10, textMaxWidth));
            int textColor = selected ? 0xFFFF55 : hovered ? 0xFFFFFF : 0xD8D8D8;
            guiGraphics.drawString(font, displayText, bounds.x() + 4, rowTop + 3, textColor, true);
        }

        if (hasScrollbar) {
            int trackX = bounds.x() + bounds.width() - 4;
            int trackTop = bounds.y() + DROPDOWN_PADDING;
            int trackHeight = bounds.height() - DROPDOWN_PADDING * 2;
            guiGraphics.fill(trackX, trackTop, trackX + 2, trackTop + trackHeight, 0x40FFFFFF);
            int thumbHeight = Math.max(8, trackHeight * bounds.visibleCount() / filteredSuggestions.size());
            int maxOffset = Math.max(1, filteredSuggestions.size() - bounds.visibleCount());
            int thumbY = trackTop + (trackHeight - thumbHeight) * suggestionScrollOffset / maxOffset;
            guiGraphics.fill(trackX, thumbY, trackX + 2, thumbY + thumbHeight, 0xFF55FFFF);
        }

        guiGraphics.pose().popPose();
    }

    private record DropdownBounds(int x, int y, int width, int height, int visibleCount) {
        private boolean contains(double mouseX, double mouseY) {
            return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
        }
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
        private boolean renderedThisFrame;

        private ListEntry(String value) {
            editBox = new EditBox(font, 0, 0, 120, 20, option.label());
            editBox.setMaxLength(1024);
            editBox.setValue(value == null ? "" : value);
            if (!allSuggestions.isEmpty()) {
                editBox.setHint(Component.translatable("buffmobs.config.menu.entityIdHint").copy().withStyle(ChatFormatting.DARK_GRAY));
            }
            editBox.setResponder(text -> onEntryValueChanged(this));
            widgets.add(editBox);
            removeButton = Button.builder(Component.translatable("buffmobs.config.menu.removeEntry").copy().withStyle(ChatFormatting.RED), button -> {
                if (activeEntry == this) clearSuggestionState();
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
            renderedThisFrame = true;
            removeButton.setX(left + width - removeWidth);
            removeButton.setY(y);
            removeButton.setWidth(removeWidth);
            removeButton.setHeight(20);
            for (AbstractWidget widget : widgets) widget.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }
}
