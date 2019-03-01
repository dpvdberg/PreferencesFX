package com.dlsc.preferencesfx.model;

import com.dlsc.preferencesfx.formsfx.view.renderer.PreferencesFxGroup;
import com.dlsc.preferencesfx.util.Constants;
import java.util.Arrays;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a group, which is used to structure one to multiple settings in a category.
 *
 * @author François Martin
 * @author Marco Sanfratello
 */
public class Group {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(Group.class.getName());

  private static final String MARKED_STYLE_CLASS = "group-marked";
  private String title;
  private List<Setting> settings;
  private PreferencesFxGroup preferencesGroup;
  private boolean marked = false;
  private final EventHandler<MouseEvent> unmarker = event -> unmark();
  private final StringProperty breadcrumb = new SimpleStringProperty("");

  private Group(String title, Setting... settings) {
    this.title = title;
    this.settings = Arrays.asList(settings);
  }

  /**
   * Constructs a new group with a {@code title} and {@code settings}.
   *
   * @param title the title of this group
   * @param settings    the settings that belong to this group
   * @return this object for chaining with the fluent API
   */
  public static Group of(String title, Setting... settings) {
    return new Group(title, settings);
  }

  /**
   * Constructs a new group with {@code settings}, without a {@code title}.
   *
   * @param settings the settings that belong to this group
   * @return this object for chaining with the fluent API
   */
  public static Group of(Setting... settings) {
    return new Group(null, settings);
  }

  /**
   * Sets a {@code title} for this group.
   *
   * @param title the title of this group
   * @return this object for chaining with the fluent API
   */
  public Group title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Returns the title of this group or if i18n is used, it will return the translated
   * title in the current locale.
   */
  public String getTitle() {
    if (preferencesGroup != null) {
      return preferencesGroup.getTitle();
    }
    return title;
  }

  public List<Setting> getSettings() {
    return settings;
  }

  public PreferencesFxGroup getPreferencesGroup() {
    return preferencesGroup;
  }

  public void setPreferencesGroup(PreferencesFxGroup preferencesGroup) {
    this.preferencesGroup = preferencesGroup;
  }

  /**
   * Marks this group in the GUI.
   * Is used for the search, which marks and unmarks items depending on the match as a form of
   * visual feedback.
   */
  public void mark() {
    // ensure it's not marked yet - so a control doesn't contain the same styleClass multiple times
    if (!marked) {
      preferencesGroup.getRenderer().addStyleClass(MARKED_STYLE_CLASS);
      preferencesGroup.getRenderer().getTitleLabel().setOnMouseExited(unmarker);
      marked = !marked;
    }
  }

  /**
   * Unmarks this group in the GUI.
   * Is used for the search, which marks and unmarks items depending on the match as a form of
   * visual feedback.
   */
  public void unmark() {
    // check if it's marked before removing the style class
    if (marked) {
      preferencesGroup.getRenderer().removeStyleClass(MARKED_STYLE_CLASS);
      preferencesGroup.getRenderer().getTitleLabel().removeEventHandler(
          MouseEvent.MOUSE_EXITED, unmarker
      );
      marked = !marked;
    }
  }

  /**
   * Adds the {@code breadCrumb} to this breadcrumb and updates all of its settings accordingly.
   *
   * @param breadCrumb the breadcrumb to add to this group's breadcrumb
   */
  public void addToBreadcrumb(String breadCrumb) {
    setBreadcrumb(breadCrumb + Constants.BREADCRUMB_DELIMITER + title);
    settings.forEach(setting -> setting.addToBreadcrumb(getBreadcrumb()));
  }

  public String getBreadcrumb() {
    return breadcrumb.get();
  }

  public StringProperty breadcrumbProperty() {
    return breadcrumb;
  }

  public void setBreadcrumb(String breadcrumb) {
    this.breadcrumb.set(breadcrumb);
  }
}
