package me.lightningreflex.lightningutils.utils;

import net.kyori.adventure.text.Component;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Placeholder {
    // this is ass (someone please remake it or make a pr for a better implementation, perhaps MiniPlaceholders?)

    public static class Builder {
        // builder class to build a Placeholder object
        // with the placeholders with .addPlaceholder(key, value)
        // and a method to fill the placeholders with .fill(string)
        private final HashMap<String, String> placeholders = new HashMap<>();

        public Builder addPlaceholder(String key, String value) {
            // add a placeholder to the builder
            placeholders.put(key, value);
            return this;
        }

        public String fill(String string) {
            // fill the placeholders in the string
            String filled = string;
            for (String key : placeholders.keySet()) {
                filled = filled.replace("{" + key + "}", placeholders.get(key));
            }
            return filled;
        }

        // component replace, so after minimessage has been applied (to prevent minimessage injection)
        public Component fill(Component component) {
            // regex pattern for {key}
            Pattern pattern = Pattern.compile("\\{([^}]*)\\}");
            return component.replaceText(compBuilder -> {
                // builder.match(pattern)
//                // doesn't support gradient/multi-colored variables cause it's ass :(
                compBuilder.match(pattern).replacement(matchResult -> {
                    System.out.println(matchResult.content());
                    // get the key from the match
                    String key = matchResult.content();
                    // strip brackets off
                    key = key.substring(1, key.length() - 1);
                    // get the value from the key
                    String value = placeholders.get(key);
                    // return the value if it exists, otherwise return the key
                    return value != null ? Component.text(value) : Component.text(key);
                });
            });
        }
    }

    public static Builder builder() {
        // create a new builder
        return new Builder();
    }
}

