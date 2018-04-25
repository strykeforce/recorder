package frc.team2767.recorder;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.FileWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class Main {

  private static final String ENDPOINT = "http://10.27.67.2:5800/v1/grapher/subscription";
  private static final int PORT = 5801;

  private static final OkHttpClient client = new OkHttpClient();

  public static void main(String[] args) throws Exception {
    //    Inventory inventory = Inventory.get();
    //    inventory.listItems();

    Subscription message = new Subscription();

    Request request = new Request.Builder().url(ENDPOINT).delete().build();
    Response response = client.newCall(request).execute();

    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    RequestBody body = RequestBody.create(JSON, message.json());
    request = new Request.Builder().url(ENDPOINT).post(body).build();
    response = client.newCall(request).execute();
    System.out.println("robot subscription acknowledgement: " + response.body().string());

    DatagramSocket socket = new DatagramSocket(PORT);
    System.out.println("listening for data on port " + PORT);
    byte[] data = new byte[1024];

    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<GrapherData> jsonAdapter = moshi.adapter(GrapherData.class);

    DateFormat dateFormat = new SimpleDateFormat("MMdd-HHmmss");

    try (FileWriter out = new FileWriter("lidar-" + dateFormat.format(new Date()) + ".csv")) {
      CSVPrinter printer =
          CSVFormat.DEFAULT
              .withHeader("timestamp", "intake_lidar", "drive_0", "drive_1", "drive_2", "drive_3")
              .print(out);

      boolean reply_seen = false;

      while (true) {
        DatagramPacket packet = new DatagramPacket(data, data.length);
        socket.receive(packet);
        if (!reply_seen) {
          System.out.println("receiving data");
          reply_seen = true;
        }
        String json = new String(packet.getData());
        GrapherData grapherData = jsonAdapter.fromJson(json);
        double[] d = grapherData.data;
        printer.printRecord(grapherData.timestamp, d[0], d[1], d[2], d[3], d[4]);
      }
    }
  }
}
