package frc.team2767.recorder;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.FileWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class Main {

  public static void main(String[] args) throws Exception {
    //    Inventory inventory = Inventory.get();
    //    inventory.listItems();

    Subscription message = new Subscription();
    System.out.println(message.json());

    MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    RequestBody body = RequestBody.create(JSON, message.json());
    Request request =
        new Request.Builder()
            .url("http://10.27.67.2:5800/v1/grapher/subscription")
            .post(body)
            .build();
    Response response = client.newCall(request).execute();
    System.out.println(response.body().string());

    DatagramSocket socket = new DatagramSocket(5555);
    byte[] data = new byte[1024];

    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<GrapherData> jsonAdapter =
        moshi.adapter(GrapherData.class);

    try (FileWriter out = new FileWriter("lidar.csv")) {
      CSVPrinter printer =
          CSVFormat.DEFAULT
              .withHeader(
                  "timestamp",
                  "intake_lidar",
                  "left_lidar",
                  "right_lidar",
                  "drive_0",
                  "drive_1",
                  "drive_2",
                  "drive_3")
              .print(out);

      while (true) {
        DatagramPacket packet = new DatagramPacket(data, data.length);
        socket.receive(packet);
        String json = new String(packet.getData());
        GrapherData grapherData = jsonAdapter.fromJson(json);
        double[] d = grapherData.data;
        printer.printRecord(grapherData.timestamp, d[0], d[1], d[2], d[3], d[4], d[5], d[6]);
      }
    }
  }
}
