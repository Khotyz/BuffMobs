package com.khotyz.buffmobs.config;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.List;

public class SuggestionBox extends EditBox {

    private static final int ROW_HEIGHT = 12;
    private static final int MAX_VISIBLE_ROWS = 6;
    private static final int BACKGROUND_COLOR = 0xF0101018;
    private static final int BORDER_COLOR = 0xFF9A9A9A;
    private static final int HOVER_COLOR = 0x6040A0FF;
    private static final int SELECTED_COLOR = 0x9040A0FF;
    private static final int TEXT_COLOR = 0xFFE0E0E0;
    private static final int SELECTED_TEXT_COLOR = 0xFFFFFFFF;
    private static final int HEADER_COLOR = 0xFF9AC8FF;

    private static final int KEY_TAB = 258;
    private static final int KEY_ENTER = 257;
    private static final int KEY_KEYPAD_ENTER = 335;
    private static final int KEY_UP = 265;
    private static final int KEY_DOWN = 264;

    private final Font font;
    private SuggestionProvider provider;
    private List<String> suggestions = List.of();
    private String cachedValue;
    private int highlighted = -1;

    public SuggestionBox(Font font, int x, int y, int width, int height, Component message) {
        super(font, x, y, width, height, message);
        this.font = font;
    }

    public void setSuggestionProvider(SuggestionProvider provider) {
        this.provider = provider;
        this.cachedValue = null;
        this.suggestions = List.of();
        this.highlighted = -1;
    }

    public void refresh() {
        if (provider == null) {
            suggestions = List.of();
            return;
        }
        String value = getValue();
        if (value.equals(cachedValue)) return;
        cachedValue = value;
        highlighted = -1;
        suggestions = List.copyOf(provider.suggestions(value));
    }

    public void clearOverlay() {
        suggestions = List.of();
        highlighted = -1;
    }

    public boolean hasOverlay() {
        if (provider == null || !isFocused() || suggestions.isEmpty()) return false;
        return !(suggestions.size() == 1 && suggestions.get(0).equalsIgnoreCase(getValue().trim()));
    }

    public boolean handleKey(int key) {
        if (!hasOverlay()) return false;
        if (key == KEY_TAB || key == KEY_DOWN) {
            cycle(1);
            return true;
        }
        if (key == KEY_UP) {
            cycle(-1);
            return true;
        }
        if (key == KEY_ENTER || key == KEY_KEYPAD_ENTER) return accept(highlighted);
        return false;
    }

    public boolean clickSuggestion(double mouseX, double mouseY, int viewportHeight) {
        if (!hasOverlay()) return false;
        return accept(indexAt(mouseX, mouseY, viewportHeight));
    }

    public void renderOverlay(GuiGraphicsExtractor graphics, int mouseX, int mouseY, int viewportHeight) {
        if (!hasOverlay()) return;

        int left = getX();
        int top = overlayTop(viewportHeight);
        int width = overlayWidth();
        graphics.fill(left, top, left + width, top + overlayHeight(), BACKGROUND_COLOR);
        graphics.fill(left, top, left + width, top + 1, BORDER_COLOR);
        graphics.text(font, Component.translatable("buffmobs.config.menu.suggestions"),
                left + 4, top + 2, HEADER_COLOR);

        int hovered = indexAt(mouseX, mouseY, viewportHeight);
        int first = firstVisible();
        for (int row = 0; row < visibleRows(); row++) {
            int index = first + row;
            if (index >= suggestions.size()) break;
            int rowTop = firstRowTop(top) + row * ROW_HEIGHT;
            int color = TEXT_COLOR;
            if (index == hovered) {
                graphics.fill(left + 1, rowTop, left + width - 1, rowTop + ROW_HEIGHT, HOVER_COLOR);
                color = SELECTED_TEXT_COLOR;
            } else if (index == highlighted) {
                graphics.fill(left + 1, rowTop, left + width - 1, rowTop + ROW_HEIGHT, SELECTED_COLOR);
                color = SELECTED_TEXT_COLOR;
            }
            graphics.text(font, Component.literal(fit(suggestions.get(index), width - 8)),
                    left + 4, rowTop + 2, color);
        }
    }

    private void cycle(int step) {
        int size = suggestions.size();
        if (size == 0) return;
        int next = highlighted + step;
        if (next < 0) next = size - 1;
        if (next >= size) next = 0;
        highlighted = next;
    }

    private boolean accept(int index) {
        if (index < 0 || index >= suggestions.size()) return false;
        String chosen = suggestions.get(index);
        setValue(chosen);
        cachedValue = chosen;
        suggestions = List.of();
        highlighted = -1;
        return true;
    }

    private int visibleRows() {
        return Math.min(suggestions.size(), MAX_VISIBLE_ROWS);
    }

    private int firstVisible() {
        if (suggestions.size() <= MAX_VISIBLE_ROWS || highlighted < 0) return 0;
        int first = highlighted - MAX_VISIBLE_ROWS + 1;
        return Math.max(0, Math.min(first, suggestions.size() - MAX_VISIBLE_ROWS));
    }

    private int headerHeight() {
        return font.lineHeight + 3;
    }

    private int firstRowTop(int overlayTop) {
        return overlayTop + headerHeight();
    }

    private int overlayHeight() {
        return headerHeight() + visibleRows() * ROW_HEIGHT + 2;
    }

    private int overlayWidth() {
        return Math.max(getWidth(), 170);
    }

    private int overlayTop(int viewportHeight) {
        int below = getY() + getHeight();
        int height = overlayHeight();
        if (below + height <= viewportHeight - 2) return below;
        int above = getY() - height;
        if (above >= 2) return above;
        return Math.max(2, viewportHeight - 2 - height);
    }

    private int indexAt(double mouseX, double mouseY, int viewportHeight) {
        int left = getX();
        int width = overlayWidth();
        if (mouseX < left || mouseX > left + width) return -1;
        int relative = (int) (mouseY - firstRowTop(overlayTop(viewportHeight)));
        if (relative < 0) return -1;
        int row = relative / ROW_HEIGHT;
        if (row < 0 || row >= visibleRows()) return -1;
        return firstVisible() + row;
    }

    private String fit(String text, int availableWidth) {
        if (font.width(text) <= availableWidth) return text;
        String result = text;
        while (result.length() > 1 && font.width(result) > availableWidth) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }
}
