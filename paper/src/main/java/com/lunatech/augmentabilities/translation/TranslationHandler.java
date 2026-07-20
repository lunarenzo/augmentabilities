package com.lunatech.augmentabilities.translation;

import com.lunatech.augmentabilities.AbstractAugmentAbilities;
import com.lunatech.augmentabilities.Reloadable;
import com.lunatech.augmentabilities.config.ConfigHandler;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import io.github.milkdrinkers.wordweaver.Translation;
import io.github.milkdrinkers.wordweaver.config.TranslationConfig;

import java.nio.file.Path;

/**
 * A wrapper handler class for handling WordWeaver lifecycle.
 */
public class TranslationHandler implements Reloadable {
    private final ConfigHandler configHandler;

    public TranslationHandler(ConfigHandler configHandler) {
        this.configHandler = configHandler;
    }

    @Override
    public void onEnable(AbstractAugmentAbilities plugin) {
        if (!Translation.isInitialized()) {
            Translation.initialize(TranslationConfig.builder()
                .translationDirectory(plugin.getDataPath().resolve("lang"))
                .resourcesDirectory(Path.of("lang"))
                .extractLanguages(true)
                .updateLanguages(true)
                .language(configHandler.getConfig().language)
                .defaultLanguage("en_US")
                .componentConverter(s -> ColorParser.of(s).build())
                .build()
            );
        } else {
            Translation.reload();
        }
    }
}
