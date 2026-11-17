package com.khotyz.buffmobs.config;

import net.minecraft.network.chat.Component;

import java.util.List;

public interface SuggestionProvider {

    List<String> suggest(String input);

    Component hint();
}
