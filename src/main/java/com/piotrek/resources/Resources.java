package com.piotrek.resources;

import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class Resources {

    public static final Preferences PREFERENCES = Preferences.userNodeForPackage(Resources.class);

    public static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("string.stringResources");

}
