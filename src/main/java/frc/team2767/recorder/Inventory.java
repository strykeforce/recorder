package frc.team2767.recorder;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Inventory {

  private static final OkHttpClient client = new OkHttpClient();
  List<Item> items;
  List<Device> measures;

  static Inventory get() throws Exception {
    Request request =
        new Request.Builder().url("http://10.27.67.2:5800/v1/grapher/inventory").build();

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) throw new IOException("unexpected code " + response);

      return parse(Objects.requireNonNull(response.body(), "null inventory response").string());
    }
  }

  static Inventory parse(String json) throws IOException {
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Inventory> jsonAdapter = moshi.adapter(Inventory.class);
    return jsonAdapter.fromJson(json);
  }

  void listItems() {
    for (Item item : items) {
      System.out.println(item.description + " (" + item.id + ")");
      Device device =
          measures.stream().filter(d -> d.deviceType.equals(item.type)).findFirst().get();
      for (Measure measure : device.deviceMeasures)
        System.out.println("    " + measure.description + " (" + measure.id + ")");
    }
  }

  static class Item {
    int id;
    String type;
    String description;
  }

  static class Device {
    String deviceType;
    List<Measure> deviceMeasures;
  }

  static class Measure {
    String id;
    String description;
  }
}
