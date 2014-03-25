package ua.promotion.util;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.PropertyKey;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Internationalization support.
 *
 * @author aldanchenko
 */
final public class I18Support {

    /**
     * Name of localization bundle.
     */
    @NonNls
    public static final String LOCALIZATION_BUNDLE_NAME = "localization";

    /**
     * Resource bundle object of {@link #LOCALIZATION_BUNDLE_NAME}.
     */
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle(LOCALIZATION_BUNDLE_NAME);

    /**
     * Util method to get localization strings from one properties file.
     *
     * @param key           - key of localization string
     * @param parameters    - parameters for string
     *
     * @return String
     */
    public static String getMessage(@PropertyKey(resourceBundle = "localization") String key,
                                    Object... parameters) {
        String value = resourceBundle.getString(key);

        if (parameters.length > 0) {
            return MessageFormat.format(value, parameters);
        }

        return value;
    }
}
