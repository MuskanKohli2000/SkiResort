package org.example.client;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import com.google.gson.Gson;
import org.example.schema.LiftRide;

public class JavaClient {
    private final static Random ran = new Random();

    public static void main(String[] args) {
        String endpoint = "http://68.233.127.218:8080/dss/skiers/1/seasons/2022/days/5/skiers/1234";
        //String endpoint = "http://localhost:8080/dss/skiers/1/seasons/2022/days/5/skiers/1234";
        int numClients = 32;
        ExecutorService executor = Executors.newFixedThreadPool(numClients);
        String csvFilePath = "src/main/resources/ClientThroughput.csv"; // Specify the local file path
        try (FileWriter writer = new FileWriter(csvFilePath)) {
            writer.append("Start Time,Request Type,Latency (ms),Response Code\n");

            for (int i = 0; i < numClients; i++) {
                int liftId = ran.nextInt(40) + 1;   // Generate a lift ID between 1 and 40
                int time = ran.nextInt(360) + 1;    // Generate a time between 1 minute and 360 minutes


                LiftRide liftRide = new LiftRide(liftId, time);

                Gson gson = new Gson();
                String json = gson.toJson(liftRide);

                long startTime = System.currentTimeMillis();

                ClientInterface client = new ClientInterface(endpoint, json);
                executor.submit(client);
                client.awaitResponse();

                long endTime = System.currentTimeMillis();
                long latency = endTime - startTime;
                int responseCode = client.getResponseCode();

                // Write out the record to the CSV file
                writer.append(String.format("%d,%d,POST,%d,%d\n", startTime, endTime ,latency, responseCode));

                System.out.println("Client " + i + " = LiftID= " + liftId + " in " + time);
                System.out.println("Elapsed Request time: " + latency + " milliseconds");
                System.out.println("Client " + i + ",LiftID= " + liftId + ",ride in " + time + "Sec");
                System.out.println("Start Time (UMT Standard): " + startTime);
                System.out.println("Request Latency: " + latency + " milliseconds");
            }

            // Wait for all clients to finish
            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (IOException | InterruptedException e) {
            System.err.println("Error writing to CSV: " + e.getMessage());
        }
    }
}
