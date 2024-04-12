package org.example.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.schema.LiftRide;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@WebServlet(name = "skiers", value = "skiers/*")
public class MainServlet extends HttpServlet {

    private final ConcurrentHashMap<String, List<LiftRide>> records = new ConcurrentHashMap<>();
    private final int MAX_RETRY = 5;
    public class RequestLogger {
        private static final String ERROR_LOG_FILE = "src/main/resources/error_log.csv";
        private static final Lock lock = new ReentrantLock();

        public static void logErrorInjection(String message) {
            lock.lock();
            try (PrintWriter out = new PrintWriter(new FileWriter(ERROR_LOG_FILE, true))) {
                String timestamp = new java.sql.Timestamp(System.currentTimeMillis()).toString();
                out.printf("%s,%s\n", timestamp, message);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public void init() throws ServletException {
        // Add some dummy data to the records map
        List<LiftRide> liftRides1 = new ArrayList<>();
        liftRides1.add(new LiftRide(1, 1));
        liftRides1.add(new LiftRide(2, 5));
        liftRides1.add(new LiftRide(3, 12));
        records.put("123:1", liftRides1);

        List<LiftRide> liftRides2 = new ArrayList<>();
        liftRides2.add(new LiftRide(1, 2));
        liftRides2.add(new LiftRide(2, 8));
        liftRides2.add(new LiftRide(3, 14));
        records.put("456:2", liftRides2);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {

        int retryCount = 0;
        while (retryCount < MAX_RETRY) {
            try {
                handlePost(request, response);
                return; // Exit the loop if handled successfully
            } catch (Exception e) {
                retryCount++;
                if (retryCount == MAX_RETRY) {
                    // Last retry, return failure
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    PrintWriter out = response.getWriter();
                    out.println("Server error occurred after multiple retries.");
                    out.close();
                    return;
                }
            }
        }
    }

    private void handlePost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Inject exceptions with 5% probability
        //Change it for more variable rejections
        if (ThreadLocalRandom.current().nextInt(100) < 5) {

            String errorLogMessage = "Simulated server error.";
            RequestLogger.logErrorInjection("Error injection triggered: " + errorLogMessage); // Log the error injection event
            throw new IOException(errorLogMessage);

        }

        int resortID = Integer.parseInt(request.getPathInfo().split("/")[1]);
        String seasonID = request.getPathInfo().split("/")[3];
        String dayID = request.getPathInfo().split("/")[5];
        String skierID = request.getPathInfo().split("/")[7];
        int ID = Integer.parseInt(skierID);
        int day = Integer.parseInt(dayID);

        if (resortID < 1 || resortID > 10 ||
                !seasonID.equals("2022") || seasonID.isEmpty() || day < 1 || day > 7 || dayID == null ||
                ID < 1 || ID > 100000) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            PrintWriter out = response.getWriter();
            out.println("Invalid parameters");
            out.close();
            return;
        }

        Gson gson = new Gson();
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        LiftRide liftRide = gson.fromJson(requestBody, LiftRide.class);

        // Validate the input
        int liftID = liftRide.getLiftID();
        int time = liftRide.getTime();

        if (liftID < 1 || liftID > 40 || time < 1 || time > 360) {
            JsonObject errorResponse = new JsonObject();
            errorResponse.addProperty("message", "Invalid input: liftID should be in range [1,40] "
                    + "and time should be in range [1,360]");

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.print(gson.toJson(errorResponse));
            out.flush();
            return;
        }

        // Handle the valid input
        response.setStatus(HttpServletResponse.SC_CREATED);
        PrintWriter out = response.getWriter();
        out.println("Status OK! Event has been Created");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int retryCount = 0;
        while (retryCount < MAX_RETRY) {
            try {
                handleGet(request, response);
                return; // Exit the loop if handled successfully
            } catch (Exception e) {
                retryCount++;
                if (retryCount == MAX_RETRY) {
                    // Last retry, return failure
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    PrintWriter out = response.getWriter();
                    out.println("Server error occurred after multiple retries.");
                    out.close();
                    return;
                }
            }
        }
    }

    private void handleGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get the path info
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            PrintWriter out = response.getWriter();
            out.println("Invalid parameters");
            out.close();
            return;
        }

        // Parse the request parameters
        String[] pathParts = pathInfo.split("/");
        if (pathParts.length < 8) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            PrintWriter out = response.getWriter();
            out.println("Invalid parameters");
            out.close();
            return;
        }

        int resortID = Integer.parseInt(pathParts[1]);
        String seasonID = pathParts[3];
        String dayID = pathParts[5];
        String skierID = pathParts[7];
        int skierIDInt = Integer.parseInt(skierID);
        int day = Integer.parseInt(dayID);

        // Validate the parameters
        if ((resortID < 1) || (resortID > 10) ||
                !seasonID.equals("2022") || seasonID.isEmpty() ||
                (day < 1) || (day > 7) || (dayID == null) ||
                (skierIDInt < 1) || (skierIDInt > 100000)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            PrintWriter out = response.getWriter();
            out.println("Invalid parameters");
            out.close();
            return;
        }

        // Get the lift ride records for the skier on the specified day
        String key = skierID + ":" + dayID;
        List<LiftRide> skierDayRecords = records.getOrDefault(key, new ArrayList<>());

        // Serialize the lift ride records to JSON
        Gson gson = new Gson();
        String responseJson = gson.toJson(skierDayRecords);

        // Write the response
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(responseJson + "OK");
        out.flush();
    }
}
