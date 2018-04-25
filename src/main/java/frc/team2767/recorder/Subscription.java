package frc.team2767.recorder;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.util.ArrayList;
import java.util.List;

public class Subscription {

  private static final int SENSORS = 0;
  private static final int DRIVE_TALON_0 = 6;
  private static final int DRIVE_TALON_1 = 7;
  private static final int DRIVE_TALON_2 = 8;
  private static final int DRIVE_TALON_3 = 9;

  private static final Moshi moshi = new Moshi.Builder().build();
  private static final JsonAdapter<Subscription> jsonAdapter =
      moshi.adapter(Subscription.class);

  final String type = "start";
  final List<Item> subscription;

  public Subscription() {
    subscription = new ArrayList<>();
    subscription.add(new Item(SENSORS, "POSITION"));

    subscription.add(new Item(DRIVE_TALON_0, "SELECTED_SENSOR_POSITION"));
    subscription.add(new Item(DRIVE_TALON_1, "SELECTED_SENSOR_POSITION"));
    subscription.add(new Item(DRIVE_TALON_2, "SELECTED_SENSOR_POSITION"));
    subscription.add(new Item(DRIVE_TALON_3, "SELECTED_SENSOR_POSITION"));
  }

  public String json() {
    return jsonAdapter.toJson(this);
  }

  public static class Item {

    private final int itemId;
    private final String measurementId;

    public Item(int itemId, String measurementId) {
      this.itemId = itemId;
      this.measurementId = measurementId;
    }
  }
}
