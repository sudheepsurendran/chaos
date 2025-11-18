package chaos;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.util.Map;
import java.util.Random;
import java.util.Collections;

public class chaosHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    /**
     * @param apiGatewayProxyRequestEvent
     * @param context
     * @return
     */
    // Helper class for simulating random outcomes
    private final Random random = new Random();

    // Define probabilities for each outcome (can be adjusted for true chaos testing)
    private static final double PROBABILITY_200 = 0.60; // 60% chance of success
    private static final double PROBABILITY_400 = 0.25; // 25% chance of client error
    // PROBABILITY_500 will be the remainder: 1.00 - 0.60 - 0.25 = 0.15 (15%)

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
       context.getLogger().log("Invoking Chaos Handler Request Path: " + requestEvent.getPath());
       double outcome = random.nextDouble();

       int statusCode=0;
       String message;

       if (outcome < PROBABILITY_200) {
           statusCode = 200;
           message = "SUCCESS: The service responded with 200 OK. Chaos averted.";
       } else if (outcome < PROBABILITY_200 + PROBABILITY_400) {
           // 400 Client Error outcome (e.g., 25% of the time)
           statusCode = 400;
           message = "CLIENT_ERROR: The service simulated a 400 Bad Request. Check your request body.";
       } else {
           // 500 Server Error outcome (e.g., 15% of the time)
           statusCode = 500;
           message = "SERVER_ERROR: The service simulated a 500 Internal Server Error. Chaos reigns!";
       }
        context.getLogger().log("Simulated Status Code: " + statusCode + ", Message: " + message);

           // OPTIONAL: Throw a runtime exception for a true 500 simulation
           // This is good for testing monitoring/error handling, but here
           // we'll return a 500 status code via the API Gateway object.
           // throw new RuntimeException(message);
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withStatusCode(statusCode)
                .withHeaders(Collections.singletonMap("Content-Type", "application/json"))
                .withBody("{\"status\": \"" + statusCode + "\", \"chaos_message\": \"" + message + "\"}");

        return response;

    }
}
