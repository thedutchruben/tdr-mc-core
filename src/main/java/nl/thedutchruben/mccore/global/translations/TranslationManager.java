package nl.thedutchruben.mccore.global.translations;

import lombok.Getter;
import nl.thedutchruben.mccore.Mccore;
import nl.thedutchruben.mccore.utils.CoreLogger;
import nl.thedutchruben.mccore.utils.GsonUtil;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Manages translations for the MC Core library
 */
public class TranslationManager {
    private static TranslationManager instance;

    @Getter
    private final Map<String, Map<String, String>> translations = new HashMap<>();
    @Getter
    private String defaultLanguage = "en_US";
    private final File translationsFolder;

    private TranslationManager() {
        // Create translations folder in plugin data directory
        translationsFolder = new File(Mccore.getInstance().getJavaPlugin().getDataFolder(), "translations");
        if (!translationsFolder.exists()) {
            boolean created = translationsFolder.mkdirs();
            if (!created) {
                CoreLogger.warning("Failed to create translations directory");
            }
        }

        // Load default translations from resources
        loadDefaultTranslations();

        // Load any custom translations from the translations folder
        loadCustomTranslations();
    }

    /**
     * Get the translation manager instance
     * @return The TranslationManager instance
     */
    public static TranslationManager getInstance() {
        if (instance == null) {
            instance = new TranslationManager();
        }
        return instance;
    }

    /**
     * Set the default language
     * @param language The language code (e.g., "en_US")
     */
    public void setDefaultLanguage(String language) {
        if (translations.containsKey(language)) {
            this.defaultLanguage = language;
        } else {
            CoreLogger.warning("Attempted to set default language to non-existent language: " + language);
        }
    }

    /**
     * Load built-in translations from resources
     */
    private void loadDefaultTranslations() {
        try {
            // Load the English language file as default
            Reader reader = new InputStreamReader(
                    Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("translations/en_US.json")),
                    StandardCharsets.UTF_8);

            Map<String, String> defaultTranslations = GsonUtil.getGson().fromJson(reader, HashMap.class);
            translations.put("en_US", defaultTranslations);

            // Save default language file to translations folder for reference/editing
            saveDefaultLanguageFile();

        } catch (Exception e) {
            CoreLogger.severe("Failed to load default translations: " + e.getMessage());
            CoreLogger.debug("Stack trace: " + e.toString());

            // Fallback to empty translation map
            translations.put("en_US", new HashMap<>());
        }
    }

    /**
     * Save the default language file to the translations folder
     */
    private void saveDefaultLanguageFile() {
        File defaultFile = new File(translationsFolder, "en_US.json");
        if (!defaultFile.exists()) {
            try {
                boolean created = defaultFile.createNewFile();
                if (!created) {
                    CoreLogger.warning("Failed to create default language file");
                    return;
                }
                Files.write(defaultFile.toPath(),
                        GsonUtil.getPrettyGson().toJson(translations.get("en_US")).getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                CoreLogger.warning("Failed to save default language file: " + e.getMessage());
            }
        }
    }

    /**
     * Load custom translations from the translations folder
     */
    private void loadCustomTranslations() {
        File[] files = translationsFolder.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) return;

        for (File file : files) {
            String langCode = file.getName().replace(".json", "");
            try {
                Reader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8);
                Map<String, String> langTranslations = GsonUtil.getGson().fromJson(reader, HashMap.class);

                // Don't override the built-in English translations
                if (langCode.equals("en_US") && translations.containsKey("en_US")) {
                    Map<String, String> existing = translations.get("en_US");
                    for (Map.Entry<String, String> entry : langTranslations.entrySet()) {
                        existing.putIfAbsent(entry.getKey(), entry.getValue());
                    }
                } else {
                    translations.put(langCode, langTranslations);
                }

                CoreLogger.info("Loaded translations for language: " + langCode);
            } catch (Exception e) {
                CoreLogger.warning("Failed to load translations for " + langCode + ": " + e.getMessage());
            }
        }
    }

    /**
     * Get a translation for the specified key in the default language
     * @param key The translation key
     * @return The translated string, or the key if no translation exists
     */
    public String get(String key) {
        return get(key, defaultLanguage);
    }

    /**
     * Get a translation for the specified key in the specified language
     * @param key The translation key
     * @param language The language code
     * @return The translated string, or the key if no translation exists
     */
    public String get(String key, String language) {
        // Try to get the translation from the specified language
        if (translations.containsKey(language)) {
            String translation = translations.get(language).get(key);
            if (translation != null) {
                return ChatColor.translateAlternateColorCodes('&', translation);
            }
        }

        // Fallback to default language
        if (!language.equals(defaultLanguage) && translations.containsKey(defaultLanguage)) {
            String translation = translations.get(defaultLanguage).get(key);
            if (translation != null) {
                return ChatColor.translateAlternateColorCodes('&', translation);
            }
        }

        // Fallback to key
        return key;
    }

    /**
     * Add a translation for a key in a specific language
     * @param language The language code
     * @param key The translation key
     * @param value The translated value
     */
    public void addTranslation(String language, String key, String value) {
        translations.computeIfAbsent(language, k -> new HashMap<>()).put(key, value);
    }
}