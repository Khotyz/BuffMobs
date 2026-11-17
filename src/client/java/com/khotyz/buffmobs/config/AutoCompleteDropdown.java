package com.khotyz.buffmobs.config;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class AutoCompleteDropdown {

    private static final int MAX_VISIBLE_SUGGESTIONS = 6;
    private static final int SUGGESTION_ROW_HEIGHT = 14;
    private static final int DROPDOWN_PADDING = 2;

    private final List<String> filteredSuggestions = new ArrayList<>();
    private EditBox activeBox;
    private SuggestionProvider activeProvider;
    private Consumer<String> activeApplyCallback;
    private int selectedIndex;
    private int scrollOffset;
    private boolean dismissed;
    private boolean suppressingResponder;
    private boolean activeBoxRenderedThisFrame;

    public void resetFrameTracking() {
        activeBoxRenderedThisFrame = false;
    }

    public void markBoxRendered(EditBox box) {
        if (box != null && box == activeBox) {
            activeBoxRenderedThisFrame = true;
        }
    }

    public boolean isMouseOver(double mouseX, double mouseY, int listTop, int listBottom, int bottomLimit) {
        DropdownBounds bounds = computeBounds(listTop, listBottom, bottomLimit);
        return bounds != null && bounds.contains(mouseX, mouseY);
    }

    public void activate(EditBox box, SuggestionProvider provider, Consumer<String> onApply, boolean showSuggestions) {
        if (activeBox != null && activeBox != box) {
            activeBox.setSuggestion(null);
        }
        activeBox = box;
        activeProvider = provider;
        activeApplyCallback = onApply;
        filteredSuggestions.clear();
        selectedIndex = 0;
        scrollOffset = 0;
        dismissed = !showSuggestions;
        if (showSuggestions && activeBox != null && activeProvider != null) {
            refreshSuggestions(false);
        }
    }

    public void onTextChanged(EditBox box, SuggestionProvider provider, Consumer<String> onApply) {
        if (suppressingResponder || box == null || provider == null) return;
        if (activeBox != null && activeBox != box) {
            activeBox.setSuggestion(null);
        }
        activeBox = box;
        activeProvider = provider;
        activeApplyCallback = onApply;
        dismissed = false;
        refreshSuggestions(false);
    }

    public void syncFocusedBox(EditBox focusedBox, SuggestionProvider provider, Consumer<String> onApply) {
        if (focusedBox == activeBox) {
            if (focusedBox != null && provider != null) {
                activeProvider = provider;
                activeApplyCallback = onApply;
            }
            return;
        }
        activate(focusedBox, provider, onApply, false);
    }

    public void onBoxRemoved(EditBox box) {
        if (box != null && box == activeBox) {
            clear();
        }
    }

    public void clear() {
        if (activeBox != null) {
            activeBox.setSuggestion(null);
        }
        activeBox = null;
        activeProvider = null;
        activeApplyCallback = null;
        filteredSuggestions.clear();
        selectedIndex = 0;
        scrollOffset = 0;
        dismissed = false;
        activeBoxRenderedThisFrame = false;
    }

    public void dismiss() {
        dismissed = true;
        filteredSuggestions.clear();
        selectedIndex = 0;
        scrollOffset = 0;
        if (activeBox != null) {
            activeBox.setSuggestion(null);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button, int listTop, int listBottom, int bottomLimit) {
        if (button != 0) return false;
        DropdownBounds bounds = computeBounds(listTop, listBottom, bottomLimit);
        if (bounds == null || !bounds.contains(mouseX, mouseY)) return false;

        int row = (int) ((mouseY - (bounds.y() + DROPDOWN_PADDING)) / SUGGESTION_ROW_HEIGHT);
        if (row >= 0 && row < bounds.visibleCount()) {
            int suggestionIndex = scrollOffset + row;
            if (suggestionIndex >= 0 && suggestionIndex < filteredSuggestions.size()) {
                applySuggestion(filteredSuggestions.get(suggestionIndex));
            }
        }
        return true;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollY, int listTop, int listBottom, int bottomLimit) {
        DropdownBounds bounds = computeBounds(listTop, listBottom, bottomLimit);
        if (bounds == null || !bounds.contains(mouseX, mouseY)) return false;

        int maxOffset = Math.max(0, filteredSuggestions.size() - bounds.visibleCount());
        if (scrollY > 0) {
            scrollOffset = Math.max(0, scrollOffset - 1);
        } else if (scrollY < 0) {
            scrollOffset = Math.min(maxOffset, scrollOffset + 1);
        }
        return true;
    }

    public boolean keyPressed(int keyCode, int listTop, int listBottom, int bottomLimit) {
        DropdownBounds bounds = computeBounds(listTop, listBottom, bottomLimit);
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
                applySuggestion(filteredSuggestions.get(selectedIndex));
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                dismiss();
                return true;
            }
            return false;
        }

        if (activeBox != null && activeBox.isFocused() && activeProvider != null && keyCode == GLFW.GLFW_KEY_DOWN) {
            dismissed = false;
            refreshSuggestions(true);
            return !filteredSuggestions.isEmpty();
        }

        return false;
    }

    public void render(GuiGraphics guiGraphics, Font font, int mouseX, int mouseY, int listTop, int listBottom, int bottomLimit) {
        DropdownBounds bounds = computeBounds(listTop, listBottom, bottomLimit);
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
            int suggestionIndex = scrollOffset + row;
            if (suggestionIndex >= filteredSuggestions.size()) break;
            String suggestion = filteredSuggestions.get(suggestionIndex);
            int rowTop = bounds.y() + DROPDOWN_PADDING + row * SUGGESTION_ROW_HEIGHT;
            int rowBottom = rowTop + SUGGESTION_ROW_HEIGHT;
            boolean hovered = bounds.contains(mouseX, mouseY) && mouseY >= rowTop && mouseY < rowBottom;
            boolean selected = suggestionIndex == selectedIndex;

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
            int thumbY = trackTop + (trackHeight - thumbHeight) * scrollOffset / maxOffset;
            guiGraphics.fill(trackX, thumbY, trackX + 2, thumbY + thumbHeight, 0xFF55FFFF);
        }

        guiGraphics.pose().popPose();
    }

    private void refreshSuggestions(boolean allowEmptyQuery) {
        filteredSuggestions.clear();
        selectedIndex = 0;
        scrollOffset = 0;

        if (activeBox == null || activeProvider == null) return;

        String rawText = activeBox.getValue();
        String trimmed = rawText.trim();
        if (trimmed.isEmpty() && !allowEmptyQuery) {
            activeBox.setSuggestion(null);
            return;
        }

        List<String> matches = activeProvider.suggest(rawText);
        if (matches == null || matches.isEmpty()) {
            activeBox.setSuggestion(null);
            return;
        }

        if (matches.size() == 1 && matches.getFirst().equalsIgnoreCase(trimmed)) {
            activeBox.setSuggestion(null);
            return;
        }

        filteredSuggestions.addAll(matches);
        updateInlineSuggestion();
    }

    private void moveSelection(int delta, int visibleCount) {
        if (filteredSuggestions.isEmpty()) return;
        int size = filteredSuggestions.size();
        selectedIndex = Math.floorMod(selectedIndex + delta, size);
        if (selectedIndex < scrollOffset) {
            scrollOffset = selectedIndex;
        } else if (selectedIndex >= scrollOffset + visibleCount) {
            scrollOffset = selectedIndex - visibleCount + 1;
        }
        clampScrollOffset(visibleCount);
        updateInlineSuggestion();
    }

    private void clampScrollOffset(int visibleCount) {
        int maxOffset = Math.max(0, filteredSuggestions.size() - visibleCount);
        if (scrollOffset > maxOffset) scrollOffset = maxOffset;
        if (scrollOffset < 0) scrollOffset = 0;
    }

    private void updateInlineSuggestion() {
        if (activeBox == null) return;
        if (dismissed || filteredSuggestions.isEmpty()) {
            activeBox.setSuggestion(null);
            return;
        }
        String current = activeBox.getValue();
        String selected = filteredSuggestions.get(selectedIndex);
        if (!current.isEmpty() && selected.startsWith(current)) {
            activeBox.setSuggestion(selected.substring(current.length()));
        } else {
            activeBox.setSuggestion(null);
        }
    }

    private void applySuggestion(String suggestion) {
        if (activeBox == null) return;
        suppressingResponder = true;
        try {
            activeBox.setValue(suggestion);
            activeBox.setSuggestion(null);
        } finally {
            suppressingResponder = false;
        }
        Consumer<String> callback = activeApplyCallback;
        dismiss();
        if (callback != null) {
            callback.accept(suggestion);
        }
    }

    private DropdownBounds computeBounds(int listTop, int listBottom, int bottomLimit) {
        if (activeBox == null || dismissed || filteredSuggestions.isEmpty()) return null;
        if (!activeBox.isFocused() || !activeBoxRenderedThisFrame) return null;

        int boxX = activeBox.getX();
        int boxY = activeBox.getY();
        int boxWidth = activeBox.getWidth();
        int boxHeight = activeBox.getHeight();

        if (boxWidth <= 0 || boxY + boxHeight <= listTop || boxY >= listBottom) return null;

        int visibleCount = Math.min(MAX_VISIBLE_SUGGESTIONS, filteredSuggestions.size());
        int dropdownHeight = visibleCount * SUGGESTION_ROW_HEIGHT + DROPDOWN_PADDING * 2;
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

    private record DropdownBounds(int x, int y, int width, int height, int visibleCount) {
        private boolean contains(double mouseX, double mouseY) {
            return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
        }
    }
}
