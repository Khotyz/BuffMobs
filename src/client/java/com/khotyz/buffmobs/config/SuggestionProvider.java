package com.khotyz.buffmobs.config;

import java.util.List;

public interface SuggestionProvider {

    List<String> suggestions(String query);
}
