package com.dlsc.preferencesfx.model;

import static com.dlsc.preferencesfx.util.Constants.BREADCRUMB_DELIMITER;

import com.dlsc.formsfx.model.util.TranslationService;
import com.dlsc.preferencesfx.util.PreferencesFxUtils;
import com.dlsc.preferencesfx.view.CategoryView;
import com.google.common.base.Strings;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a category, which is used to structure one to multiple groups with settings in a page.
 *
 * @author François Martin
 * @author Marco Sanfratello
 */
public class Category {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(Category.class.getName());

  private StringProperty title = new SimpleStringProperty();
  private StringProperty titleKey = new SimpleStringProperty();
  private List<Group> groups;
  private List<Category> children;
  private final StringProperty breadcrumb = new SimpleStringProperty("");
  private Node itemIcon;

  /**
   * Creates a category without groups, for top-level categories without any settings.
   *
   * @param title Category name, for display in {@link CategoryView}
   */
  private Category(String title) {
    titleKey.setValue(title);
    translate(null);
    setBreadcrumb(title);
  }

  private Category(String title, Group... groups) {
    this(title);
    this.groups = Arrays.asList(groups);
  }

  private Category(String title, Node itemIcon) {
    this(title);
    this.itemIcon = itemIcon;
  }

  private Category(String title, Node itemIcon, Group... groups) {
    this(title, groups);
    this.itemIcon = itemIcon;
  }

  /**
   * Creates an empty category.
   * Can be used for top-level categories without {@link Setting}.
   *
   * @param title Category name, for display in {@link CategoryView}
   * @return initialized Category object
   */
  public static Category of(String title) {
    return new Category(title);
  }

  /**
   * Creates a new category from groups.
   *
   * @param title Category name, for display in {@link CategoryView}
   * @param groups      {@link Group} with {@link Setting} to be shown in the {@link CategoryView}
   * @return initialized Category object
   */
  public static Category of(String title, Group... groups) {
    return new Category(title, groups);
  }

  /**
   * Creates a new category from settings, if the settings shouldn't be individually grouped.
   *
   * @param title Category name, for display in {@link CategoryView}
   * @param settings    {@link Setting} to be shown in the {@link CategoryView}
   * @return initialized Category object
   */
  public static Category of(String title, Setting... settings) {
    return new Category(title, Group.of(settings));
  }

  /**
   * Creates an empty category.
   * Can be used for top-level categories without {@link Setting}.
   *
   * @param title Category name, for display in {@link CategoryView}
   * @param itemIcon    Icon to be shown next to the category name
   * @return initialized Category object
   */
  public static Category of(String title, Node itemIcon) {
    return new Category(title, itemIcon);
  }

  /**
   * Creates a new category from groups.
   *
   * @param title Category name, for display in {@link CategoryView}
   * @param itemIcon    Icon to be shown next to the category name
   * @param groups      {@link Group} with {@link Setting} to be shown in the {@link CategoryView}
   * @return initialized Category object
   */
  public static Category of(String title, Node itemIcon, Group... groups) {
    return new Category(title, itemIcon, groups);
  }

  /**
   * Creates a new category from settings, if the settings shouldn't be individually grouped.
   *
   * @param title Category name, for display in {@link CategoryView}
   * @param itemIcon    Icon to be shown next to the category name
   * @param settings    {@link Setting} to be shown in the {@link CategoryView}
   * @return initialized Category object
   */
  public static Category of(String title, Node itemIcon, Setting... settings) {
    return new Category(title, itemIcon, Group.of(settings));
  }

  /**
   * Adds subcategories to this category. Can be used to build up a hierarchical tree of Categories.
   *
   * @param children the subcategories to assign to this category
   * @return this object for chaining with the fluent API
   */
  public Category subCategories(Category... children) {
    this.children = Arrays.asList(children);
    return this;
  }

  /**
   * Creates and defines all of the breadcrumbs for all of the categories.
   *
   * @param categories the categories to create breadcrumbs for
   */
  public void createBreadcrumbs(List<Category> categories) {
    categories.forEach(category -> {
      category.setBreadcrumb(getBreadcrumb() + BREADCRUMB_DELIMITER + category.getTitle());
      if (!Objects.equals(category.getGroups(), null)) {
        category.getGroups().forEach(group -> group.addToBreadcrumb(getBreadcrumb()));
      }
      if (!Objects.equals(category.getChildren(), null)) {
        category.createBreadcrumbs(category.getChildren());
      }
    });
  }

  /**
   * Unmarks all settings.
   * Is used for the search, which marks and unmarks items depending on the match as a form of
   * visual feedback.
   */
  public void unmarkSettings() {
    if (getGroups() != null) {
      PreferencesFxUtils.groupsToSettings(getGroups())
          .forEach(Setting::unmark);
    }
  }

  /**
   * Unmarks all groups.
   * Is used for the search, which marks and unmarks items depending on the match as a form of
   * visual feedback.
   */
  public void unmarkGroups() {
    if (getGroups() != null) {
      getGroups().forEach(Group::unmark);
    }
  }

  /**
   * Unmarks all settings and groups.
   * Is used for the search, which marks and unmarks items depending on the match as a form of
   * visual feedback.
   */
  public void unmarkAll() {
    unmarkGroups();
    unmarkSettings();
  }

  /**
   * This internal method is used as a callback for when the translation
   * service or its locale changes. Also applies the translation to all
   * contained sections.
   *
   * @see com.dlsc.formsfx.model.structure.Group ::translate
   */
  public void translate(TranslationService translationService) {
    if (translationService == null) {
      title.setValue(titleKey.getValue());
      return;
    }

    if (!Strings.isNullOrEmpty(titleKey.get())) {
      title.setValue(translationService.translate(titleKey.get()));
    }
  }

  /**
   * Updates the group titles when translation changes.
   */
  public void updateGroupTitles() {
    if (groups != null) {
      groups.forEach(group -> group.getPreferencesGroup().translate());
    }
  }

  public String getTitle() {
    return title.get();
  }

  public List<Group> getGroups() {
    return groups;
  }

  public List<Category> getChildren() {
    return children;
  }

  @Override
  public String toString() {
    return title.get();
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

  public ReadOnlyStringProperty titleProperty() {
    return title;
  }

  public Node getItemIcon() {
    return itemIcon;
  }
}
