package com.khotyz.buffmobs.config;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class AutocompleteEditBox extends EditBox {

    private final SuggestionType suggestionType;
    private final String hintKey;
    private Runnable activation = () -> {
    };
    private boolean activationSuppressed;

    public AutocompleteEditBox(Font font, int x, int y, int width, int height, Component message,
                               SuggestionType suggestionType, String hintKey) {
        super(font, x, y, width, height, message);
        this.suggestionType = suggestionType == null ? SuggestionType.NONE : suggestionType;
        this.hintKey = hintKey != null ? hintKey : IdSuggestions.hintKey(this.suggestionType);
        if (this.hintKey != null) setTooltip(Tooltip.create(Component.translatable(this.hintKey)));
        setResponder(text -> {
        });
    }

    public SuggestionType suggestionType() {
        return suggestionType;
    }

    public String hintKey() {
        return hintKey;
    }

    public boolean suggestsValues() {
        return suggestionType != SuggestionType.NONE;
    }

    public void onActivation(Runnable activation) {
        this.activation = activation == null ? () -> {
        } : activation;
    }

    public void withoutActivation(Runnable action) {
        boolean previous = activationSuppressed;
        activationSuppressed = true;
        try {
            action.run();
        } finally {
            activationSuppressed = previous;
        }
    }

    @Override
    public void setResponder(Consumer<String> responder) {
        Consumer<String> target = responder == null ? text -> {
        } : responder;
        super.setResponder(text -> {
            target.accept(text);
            if (!activationSuppressed) activation.run();
        });
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        boolean handled = super.mouseClicked(event, doubleClick);
        if (handled && !activationSuppressed) activation.run();
        return handled;
    }
}
