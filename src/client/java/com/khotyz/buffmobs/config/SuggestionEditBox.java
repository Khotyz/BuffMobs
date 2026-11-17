package com.khotyz.buffmobs.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SuggestionEditBox extends EditBox {

    private static final int MAX_VISIBLE_SUGGESTIONS = 8;
    private static final int SUGGESTION_ROW_HEIGHT = 12;

    private final Font font;
    private final SuggestionProvider suggestionProvider;
    private final List<String> suggestions = new ArrayList<>();
    private Consumer<String> responder;
    private int selectedIndex = -1;
    private boolean showingSuggestions;
    private boolean applyingSuggestion;

    public SuggestionEditBox(Font font, int x, int y, int width, int height,
                            Component narration, SuggestionProvider suggestionProvider) {
        super(font, x, y, width, height, narration);
        this.font = font;
        this.suggestionProvider = suggestionProvider;
        super.setResponder(this::handleTextChanged);
    }

    @Override
    public void setResponder(Consumer<String> responder) {
        this.responder = responder;
    }

    private void handleTextChanged(String text) {
        if (!applyingSuggestion) updateSuggestions(text);
        if (responder != null) responder.accept(text);
    }

    private void updateSuggestions(String text) {
        suggestions.clear();
        selectedIndex = -1;
        showingSuggestions = false;
        setSuggestion(null);
        if (suggestionProvider == null || text.isEmpty() || !isFocused()) return;
        suggestions.addAll(suggestionProvider.suggestions(text));
        if (!suggestions.isEmpty()) {
            showingSuggestions = true;
            selectedIndex = 0;
            updateGhostSuggestion();
        }
    }

    private void clearSuggestions() {
        suggestions.clear();
        selectedIndex = -1;
        showingSuggestions = false;
        setSuggestion(null);
    }

    private void updateGhostSuggestion() {
        if (selectedIndex < 0 || selectedIndex >= suggestions.size()) {
            setSuggestion(null);
            return;
        }
        String current = getValue();
        String candidate = suggestions.get(selectedIndex);
        setSuggestion(candidate.startsWith(current) ? candidate.substring(current.length()) : null);
    }

    private void moveSelection(int delta) {
        if (suggestions.isEmpty()) return;
        selectedIndex = Math.floorMod(selectedIndex + delta, suggestions.size());
        updateGhostSuggestion();
    }

    private void acceptSelected() {
        if (selectedIndex < 0 || selectedIndex >= suggestions.size()) return;
        applyingSuggestion = true;
        setValue(suggestions.get(selectedIndex));
        applyingSuggestion = false;
        clearSuggestions();
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (showingSuggestions) {
            if (event.isUp()) {
                moveSelection(-1);
                return true;
            }
            if (event.isDown()) {
                moveSelection(1);
                return true;
            }
            if (event.isConfirmation() || event.isCycleFocus()) {
                acceptSelected();
                return true;
            }
            if (event.isEscape()) {
                clearSuggestions();
                return true;
            }
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (showingSuggestions && isMouseOverPopup(event.x(), event.y())) {
            int index = popupIndexAt(event.x(), event.y());
            if (index >= 0 && index < suggestions.size()) {
                selectedIndex = index;
                acceptSelected();
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (!focused) clearSuggestions();
    }

    private int popupTop() {
        int below = getY() + getHeight();
        int height = popupHeight();
        int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        return below + height > screenHeight ? getY() - height : below;
    }

    private int popupHeight() {
        return Math.min(suggestions.size(), MAX_VISIBLE_SUGGESTIONS) * SUGGESTION_ROW_HEIGHT + 2;
    }

    private boolean isMouseOverPopup(double mouseX, double mouseY) {
        int top = popupTop();
        return mouseX >= getX() && mouseX < getX() + getWidth()
                && mouseY >= top && mouseY < top + popupHeight();
    }

    private int popupIndexAt(double mouseX, double mouseY) {
        int index = (int) Math.floor((mouseY - popupTop()) / SUGGESTION_ROW_HEIGHT);
        return index >= 0 && index < Math.min(suggestions.size(), MAX_VISIBLE_SUGGESTIONS) ? index : -1;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(graphics, mouseX, mouseY, partialTick);
        if (!showingSuggestions || suggestions.isEmpty()) return;
        int visible = Math.min(suggestions.size(), MAX_VISIBLE_SUGGESTIONS);
        int left = getX();
        int top = popupTop();
        int width = getWidth();
        int height = popupHeight();
        graphics.fill(left, top, left + width, top + height, 0xE0000000);
        for (int i = 0; i < visible; i++) {
            int rowTop = top + 1 + i * SUGGESTION_ROW_HEIGHT;
            if (i == selectedIndex) {
                graphics.fill(left, rowTop, left + width, rowTop + SUGGESTION_ROW_HEIGHT, 0x60FFFFFF);
            }
            graphics.drawString(font, suggestions.get(i), left + 2, rowTop + 2,
                    i == selectedIndex ? 0xFFFFAA00 : 0xFFD0D0D0);
        }
    }
}
