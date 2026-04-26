package artifacts.config.display;

import artifacts.datagen.LangEntry;

import java.util.List;

public record ConfigEntryDisplay(
        LangEntry title,
        List<LangEntry> description,
        int displayPriority
) {

}
