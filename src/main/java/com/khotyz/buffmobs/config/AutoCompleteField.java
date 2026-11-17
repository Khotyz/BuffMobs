package com.khotyz.buffmobs.config;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.List;

public final class AutoCompleteField {

    private static final int MAX_SUGGESTIONS = 64;
    private static final int MAX_VISIBLE_ROWS = 8;
    private static final int ROW_HEIGHT = 11;
    private static final int PADDING = 3;
    private static final int MIN_WIDTH = 150;
    private static final int BACKGROUND_COLOR = 0xF0101020;
    private static final int BORDER_COLOR = 0xFF2E9BB5;
    private static final int SELECTION_COLOR = 0x903FA9C4;
    private static final int TEXT_COLOR = 0xFFC8C8C8;
    private static final int SELECTED_TEXT_COLOR = 0xFFFFFFFF;
    private static final int HINT_COLOR = 0xFF7E7E7E;

    private final EditBox editBox;
    private final SuggestionKind kind;
    private List<String> suggestions = List.of();
    private int selected;
    private boolean popupRendered;
    private int popupX;
    private int popupY;
    private int popupWidth;
    private int popupHeight;
    private int firstVisibleRow;
    private int visibleRows;

    public AutoCompleteField(EditBox editBox, SuggestionKind kind) {
        this.editBox = editBox;
        this.kind = kind == null ? SuggestionKind.NONE : kind;
        refresh();
    }

    public boolean isEnabled() {
        return kind.isEnabled();
    }

    public void refresh() {
        if (!isEnabled()) return;
        suggestions = AutoComplete.suggestions(kind, editBox.getValue(), MAX_SUGGESTIONS);
        selected = 0;
    }

    public boolean isOpen() {
        return isEnabled() && editBox.isFocused() && !suggestions.isEmpty();
    }

    public boolean keyPressed(KeyEvent event) {
        if (!isOpen()) return false;
        int key = event.key();
        if (key == InputConstants.KEY_TAB || key == InputConstants.KEY_DOWN) {
            moveSelection(1);
            return true;
        }
        if (key == InputConstants.KEY_UP) {
            moveSelection(-1);
            return true;
        }
        if (key == InputConstants.KEY_RETURN || key == InputConstants.KEY_NUMPADENTER) {
            acceptSelection();
            return true;
        }
        return false;
    }

    public boolean mouseClicked(MouseButtonEvent event) {
        if (!isOpen() || !popupRendered) return false;
        if (event.button() != InputConstants.MOUSE_BUTTON_LEFT) return false;
        double mouseX = event.x();
        double mouseY = event.y();
        if (mouseX < popupX || mouseX > popupX + popupWidth) return false;
        if (mouseY < popupY || mouseY > popupY + popupHeight) return false;
        int row = (int) Math.floor((mouseY - (popupY + PADDING + ROW_HEIGHT)) / (double) ROW_HEIGHT);
        if (row >= 0 && row < visibleRows) {
            selected = firstVisibleRow + row;
            acceptSelection();
        }
        return true;
    }

    public void render(GuiGraphics graphics, Font font, int screenWidth, int screenHeight, boolean areaVisible) {
        popupRendered = false;
        if (!isOpen() || !areaVisible) return;

        firstVisibleRow = firstVisibleRowIndex();
        visibleRows = Math.min(MAX_VISIBLE_ROWS, suggestions.size());
        Component hint = Component.translatable(kind.hintKey());
        int contentWidth = font.width(hint);
        for (int row = 0; row < visibleRows; row++) {
            contentWidth = Math.max(contentWidth, font.width(suggestions.get(firstVisibleRow + row)));
        }

        int width = Math.max(MIN_WIDTH, Math.max(editBox.getWidth(), contentWidth + PADDING * 2 + 4));
        width = Math.min(width, Math.max(MIN_WIDTH, screenWidth - editBox.getX() - PADDING * 2));
        int contentHeight = ROW_HEIGHT * (visibleRows + 1) + 2;
        int x = editBox.getX();
        int contentTop = editBox.getY() + editBox.getHeight() + 1;
        if (contentTop + contentHeight + PADDING > screenHeight - 2) {
            contentTop = editBox.getY() - contentHeight - PADDING * 2 - 1;
        }

        popupX = x - PADDING;
        popupY = contentTop - PADDING;
        popupWidth = width + PADDING * 2;
        popupHeight = contentHeight + PADDING * 2;

        graphics.fill(popupX, popupY, popupX + popupWidth, popupY + popupHeight, BACKGROUND_COLOR);
        graphics.renderOutline(popupX, popupY, popupWidth, popupHeight, BORDER_COLOR);
        graphics.drawString(font, hint, x + 2, contentTop + 1, HINT_COLOR);
        for (int row = 0; row < visibleRows; row++) {
            int suggestionIndex = firstVisibleRow + row;
            int rowTop = contentTop + ROW_HEIGHT * (row + 1);
            if (suggestionIndex == selected) {
                graphics.fill(x, rowTop, x + width, rowTop + ROW_HEIGHT, SELECTION_COLOR);
            }
            int textColor = suggestionIndex == selected ? SELECTED_TEXT_COLOR : TEXT_COLOR;
            graphics.drawString(font, suggestions.get(suggestionIndex), x + 2, rowTop + 1, textColor);
        }
        popupRendered = true;
    }

    private void moveSelection(int delta) {
        if (suggestions.isEmpty()) return;
        selected = Math.floorMod(selected + delta, suggestions.size());
    }

    private void acceptSelection() {
        if (suggestions.isEmpty()) return;
        int index = Math.min(Math.max(selected, 0), suggestions.size() - 1);
        editBox.setValue(suggestions.get(index));
        refresh();
    }

    private int firstVisibleRowIndex() {
        if (suggestions.size() <= MAX_VISIBLE_ROWS) return 0;
        return Math.max(0, Math.min(selected - MAX_VISIBLE_ROWS / 2, suggestions.size() - MAX_VISIBLE_ROWS));
    }
}
