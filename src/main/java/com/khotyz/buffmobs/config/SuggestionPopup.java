package com.khotyz.buffmobs.config;

import com.khotyz.buffmobs.util.Guard;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public final class SuggestionPopup {

    private static final int ROW_HEIGHT = 12;
    private static final int VISIBLE_ROWS = 6;
    private static final int HINT_HEIGHT = 12;
    private static final int MIN_WIDTH = 170;
    private static final int MAX_WIDTH = 300;
    private static final int BACKGROUND = 0xF0101010;
    private static final int BORDER = 0xFF55FFFF;
    private static final int SELECTED_COLOR = 0x60FFAA00;
    private static final int HOVERED_COLOR = 0x40FFFFFF;
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int HINT_COLOR = 0xFF9A9A9A;

    private static final int TAB_KEY = BuffMobsConfigScreen.resolveInputConstant("KEY_TAB", 258);
    private static final int ENTER_KEY = BuffMobsConfigScreen.resolveInputConstant("KEY_ENTER", 257);
    private static final int NUMPAD_ENTER_KEY = BuffMobsConfigScreen.resolveInputConstant("KEY_KP_ENTER", 335);
    private static final int UP_KEY = BuffMobsConfigScreen.resolveInputConstant("KEY_UP", 265);
    private static final int DOWN_KEY = BuffMobsConfigScreen.resolveInputConstant("KEY_DOWN", 264);
    private static final int ESCAPE_KEY = BuffMobsConfigScreen.resolveInputConstant("KEY_ESCAPE", 256);

    private final List<String> matches = new ArrayList<>();
    private int selected;
    private int left;
    private int top;
    private int width;
    private int height;
    private boolean visible;
    private String hintKey;

    public void hide() {
        visible = false;
        matches.clear();
        selected = 0;
        hintKey = null;
    }

    public boolean isVisible() {
        return visible;
    }

    public void refresh(AutocompleteEditBox box, int screenWidth, int screenHeight) {
        if (box == null || !box.suggestsValues()) {
            hide();
            return;
        }
        List<String> found = IdSuggestions.matches(box.suggestionType(), box.getValue());
        if (found.isEmpty()) {
            hide();
            return;
        }
        matches.clear();
        matches.addAll(found.subList(0, Math.min(VISIBLE_ROWS, found.size())));
        if (selected >= matches.size()) selected = 0;
        hintKey = box.hintKey();
        width = Math.max(MIN_WIDTH, Math.min(MAX_WIDTH, box.getWidth() + 80));
        left = box.getX();
        if (left + width > screenWidth - 4) left = Math.max(4, screenWidth - 4 - width);
        top = box.getY() + box.getHeight();
        height = matches.size() * ROW_HEIGHT + (hintKey == null ? 0 : HINT_HEIGHT) + 2;
        if (top + height > screenHeight - 4) top = Math.max(4, box.getY() - height - 2);
        visible = true;
    }

    public void render(GuiGraphicsExtractor graphics, Font font, int mouseX, int mouseY) {
        if (!visible || matches.isEmpty()) return;
        try {
            graphics.fill(left - 1, top - 1, left + width + 1, top + height + 1, BACKGROUND);
            graphics.fill(left - 1, top - 1, left + width + 1, top, BORDER);
            int rowTop = top;
            if (hintKey != null) {
                graphics.text(font, Component.translatable(hintKey), left + 3, rowTop + 2, HINT_COLOR);
                rowTop += HINT_HEIGHT;
            }
            for (int index = 0; index < matches.size(); index++) {
                boolean hovered = mouseX >= left && mouseX < left + width
                        && mouseY >= rowTop && mouseY < rowTop + ROW_HEIGHT;
                if (index == selected) {
                    graphics.fill(left, rowTop, left + width, rowTop + ROW_HEIGHT, SELECTED_COLOR);
                } else if (hovered) {
                    graphics.fill(left, rowTop, left + width, rowTop + ROW_HEIGHT, HOVERED_COLOR);
                }
                graphics.text(font, Component.literal(matches.get(index)), left + 3, rowTop + 2, TEXT_COLOR);
                rowTop += ROW_HEIGHT;
            }
        } catch (Throwable throwable) {
            Guard.report("BuffMobs suggestion popup", throwable);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, AutocompleteEditBox box) {
        if (!visible || box == null || matches.isEmpty()) return false;
        int rowTop = top + (hintKey == null ? 0 : HINT_HEIGHT);
        for (int index = 0; index < matches.size(); index++) {
            boolean inside = mouseX >= left && mouseX < left + width
                    && mouseY >= rowTop && mouseY < rowTop + ROW_HEIGHT;
            if (inside) {
                accept(box, matches.get(index));
                return true;
            }
            rowTop += ROW_HEIGHT;
        }
        return false;
    }

    public boolean keyPressed(KeyEvent event, AutocompleteEditBox box) {
        if (!visible || box == null || matches.isEmpty()) return false;
        int key = event.key();
        if (key == DOWN_KEY) {
            selected = (selected + 1) % matches.size();
            return true;
        }
        if (key == UP_KEY) {
            selected = (selected - 1 + matches.size()) % matches.size();
            return true;
        }
        if (key == TAB_KEY || key == ENTER_KEY || key == NUMPAD_ENTER_KEY) {
            accept(box, matches.get(selected));
            return true;
        }
        if (key == ESCAPE_KEY) {
            hide();
            return true;
        }
        return false;
    }

    private void accept(AutocompleteEditBox box, String suggestion) {
        String value = IdSuggestions.completion(box.suggestionType(), suggestion, box.getValue());
        box.withoutActivation(() -> box.setValue(value));
        hide();
    }
}
