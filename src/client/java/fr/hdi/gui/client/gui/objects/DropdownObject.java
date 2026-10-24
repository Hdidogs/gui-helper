package fr.hdi.gui.client.gui.objects;

import fr.hdi.gui.client.gui.utils.ObjectType;
import fr.hdi.gui.client.screen.widget.TexturedDropdownWidget;
import fr.hdi.gui.utils.Color;
import fr.hdi.gui.utils.GuiData;
import fr.hdi.gui.utils.TextWithDetail;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DropdownObject extends GuiObject {
    public static final Color DEFAULT_BACKGROUND = new Color("dropdown_background", 0x8B8B8B);
    public static final Color DEFAULT_BORDER = new Color("dropdown_border", 0x373737);
    public static final Color DEFAULT_HOVER = new Color("dropdown_hover", 0x60FFFFFF);

    private List<DropdownOption> options = new ArrayList<>();
    private Color backgroundColor = DEFAULT_BACKGROUND;
    private Color borderColor = DEFAULT_BORDER;
    private Color hoverColor = DEFAULT_HOVER;
    private int optionSizeY;
    private int maxVisibleOptions = 4;
    private double scrollY;
    private boolean searchable;
    private Text searchPlaceholder;
    private String search = "";
    private String defaultValue;
    private String value;

    public DropdownObject(String id, int objectStartX, int objectStartY, int objectSizeX, int objectSizeY) {
        super(id, ObjectType.DROPDOWN, objectStartX, objectStartY, objectSizeX, objectSizeY);
        this.optionSizeY = objectSizeY;
    }

    public DropdownObject addOption(String value, TextWithDetail label) {
        options.add(new DropdownOption(value, label));

        if (this.value == null) setDefaultValue(value);

        return this;
    }

    public List<DropdownOption> getOptions() {
        return options;
    }

    public List<DropdownOption> getFilteredOptions() {
        if (search.isEmpty()) return options;

        String query = search.toLowerCase(Locale.ROOT);

        return options.stream().filter(option -> option.getSearchText().contains(query)).toList();
    }

    public int getSelectedIndex() {
        List<DropdownOption> filtered = getFilteredOptions();

        for (int index = 0; index < filtered.size(); index++) {
            if (filtered.get(index).getValue().equals(value)) return index;
        }

        return -1;
    }

    public DropdownOption getSelectedOption() {
        for (DropdownOption option : options) {
            if (option.getValue().equals(value)) return option;
        }

        return null;
    }

    public int getListSizeY() {
        return Math.min(getFilteredOptions().size(), maxVisibleOptions) * optionSizeY;
    }

    public int getContentSizeY() {
        return getFilteredOptions().size() * optionSizeY;
    }

    public int getSearchSizeY() {
        return searchable ? optionSizeY : 0;
    }

    public boolean isSearchable() {
        return searchable;
    }

    public DropdownObject setSearchable(boolean searchable) {
        this.searchable = searchable;

        return this;
    }

    public Text getSearchPlaceholder() {
        return searchPlaceholder;
    }

    public DropdownObject setSearchPlaceholder(Text searchPlaceholder) {
        this.searchPlaceholder = searchPlaceholder;

        return this;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search == null ? "" : search;
    }

    public int getMaxVisibleOptions() {
        return maxVisibleOptions;
    }

    public DropdownObject setMaxVisibleOptions(int maxVisibleOptions) {
        this.maxVisibleOptions = maxVisibleOptions;

        return this;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public Color getHoverColor() {
        return hoverColor;
    }

    public DropdownObject setColors(Color backgroundColor, Color borderColor, Color hoverColor) {
        this.backgroundColor = backgroundColor;
        this.borderColor = borderColor;
        this.hoverColor = hoverColor;

        return this;
    }

    public int getOptionSizeY() {
        return optionSizeY;
    }

    public DropdownObject setOptionSizeY(int optionSizeY) {
        this.optionSizeY = optionSizeY;

        return this;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public DropdownObject setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        this.value = defaultValue;

        return this;
    }

    public double getScrollY() {
        return scrollY;
    }

    public void setScrollY(double scrollY) {
        this.scrollY = scrollY;
    }

    @Override
    public void resetState() {
        this.value = defaultValue;
        this.scrollY = 0.0D;
        this.search = "";
    }

    @Override
    public void applyData(GuiData data) {
        this.value = data.getString(getId(), value);
    }

    @Override
    public void collectData(GuiData data) {
        if (value != null) data.set(getId(), value);
    }

    @Override
    public ClickableWidget register(int bgX, int bgY) {
        return cache(new TexturedDropdownWidget(this, bgX, bgY));
    }

    public static class DropdownOption {
        private String value;
        private TextWithDetail label;

        public DropdownOption(String value, TextWithDetail label) {
            this.value = value;
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public TextWithDetail getLabel() {
            return label;
        }

        public String getSearchText() {
            String text = label != null && label.getText() != null ? label.getText().getString() : value;

            return text.toLowerCase(Locale.ROOT);
        }
    }
}
