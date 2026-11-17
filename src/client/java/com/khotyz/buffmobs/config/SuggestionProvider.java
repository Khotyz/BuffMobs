package com.khotyz.buffmobs.config;

import java.util.List;

@FunctionalInterface
public interface SuggestionProvider {

    List<String> suggestions(String currentValue);
}
