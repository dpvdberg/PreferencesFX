package com.dlsc.preferencesfx.standard;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.IntegerField;
import com.dlsc.formsfx.model.validators.CustomValidator;
import com.dlsc.formsfx.model.validators.DoubleRangeValidator;
import com.dlsc.formsfx.model.validators.Validator;
import com.dlsc.preferencesfx.AppStarter;
import com.dlsc.preferencesfx.PreferencesFx;
import com.dlsc.preferencesfx.formsfx.view.controls.IntegerSliderControl;
import com.dlsc.preferencesfx.model.Category;
import com.dlsc.preferencesfx.model.Group;
import com.dlsc.preferencesfx.model.Setting;
import java.util.Arrays;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.StackPane;

public class StandardExample extends StackPane {

  public PreferencesFx preferencesFx;

  // General
  StringProperty welcomeText = new SimpleStringProperty("Hello World");
  IntegerProperty brightness = new SimpleIntegerProperty(50);
  BooleanProperty nightMode = new SimpleBooleanProperty(true);

  // Screen
  DoubleProperty scaling = new SimpleDoubleProperty(1);
  StringProperty screenName = new SimpleStringProperty("PreferencesFx Monitor");

  ObservableList<String> resolutionItems = FXCollections.observableArrayList(Arrays.asList(
      "1024x768", "1280x1024", "1440x900", "1920x1080")
  );
  ObjectProperty<String> resolutionSelection = new SimpleObjectProperty<>("1024x768");

  ListProperty<String> orientationItems = new SimpleListProperty<>(
      FXCollections.observableArrayList(Arrays.asList("Vertical", "Horizontal"))
  );
  ObjectProperty<String> orientationSelection = new SimpleObjectProperty<>("Vertical");

  IntegerProperty fontSize = new SimpleIntegerProperty(12);
  DoubleProperty lineSpacing = new SimpleDoubleProperty(1.5);

  // Favorites
  ListProperty<String> favoritesItems = new SimpleListProperty<>(
      FXCollections.observableArrayList(Arrays.asList(
          "eMovie", "Eboda Phot-O-Shop", "Mikesoft Text",
          "Mikesoft Numbers", "Mikesoft Present", "IntelliG"
          )
      )
  );
  ListProperty<String> favoritesSelection = new SimpleListProperty<>(
      FXCollections.observableArrayList(Arrays.asList(
          "Eboda Phot-O-Shop", "Mikesoft Text"))
  );

  // Custom Control
  IntegerProperty customControlProperty = new SimpleIntegerProperty(42);
  IntegerField customControl = setupCustomControl();

  public StandardExample() {
    preferencesFx = createPreferences();
    getChildren().add(new DemoView(preferencesFx, this));
  }

  private IntegerField setupCustomControl() {
    return Field.ofIntegerType(customControlProperty).render(
        new IntegerSliderControl(0, 42));
  }

  private PreferencesFx createPreferences() {
    // asciidoctor Documentation - tag::setupPreferences[]
    return PreferencesFx.of(AppStarter.class,
        Category.of("General",
            Group.of("Greeting",
                Setting.of("Welcome Text", welcomeText)
                        .validate(CustomValidator.forPredicate(s -> !s.equals("ok"), "Cannot be ok"))
                        .withDescription("test")
            ),
            Group.of("Display",
                Setting.of("Brightness", brightness)
                        .withDescription("Don't make it hurt your eyes."),
                Setting.of("Night mode", nightMode)
            )
        ),
        Category.of("Screen")
            .subCategories(
                Category.of("Scaling & Ordering",
                    Group.of(
                        Setting.of("Scaling", scaling)
                            .validate(DoubleRangeValidator
                                .atLeast(1, "Scaling needs to be at least 1")
                            ),
                        Setting.of("Screen name", screenName),
                        Setting.of("Resolution", resolutionItems, resolutionSelection),
                        Setting.of("Orientation", orientationItems, orientationSelection)
                    ).title("Screen Options"),
                    Group.of(
                        Setting.of("Font Size", fontSize, 6, 36),
                        Setting.of("Line Spacing", lineSpacing, 0, 3, 1)
                    )
                )
            ),
        Category.of("Favorites",
            Setting.of("Favorites", favoritesItems, favoritesSelection),
            Setting.of("Favorite Number", customControl, customControlProperty)
        )
    ).persistWindowState(false).saveSettings(true).debugHistoryMode(false).buttonsVisibility(true);
    // asciidoctor Documentation - end::setupPreferences[]
  }
}
