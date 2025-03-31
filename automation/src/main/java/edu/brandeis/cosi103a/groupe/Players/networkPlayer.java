package edu.brandeis.cosi103a.groupe.Players;

import okhttp3.*;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import edu.brandeis.cosi.atg.api.GameObserver;
import edu.brandeis.cosi.atg.api.GameState;
import edu.brandeis.cosi.atg.api.Player;
import edu.brandeis.cosi.atg.api.decisions.Decision;
import edu.brandeis.cosi.atg.api.event.Event;
import edu.brandeis.cosi103a.groupe.*;
import java.util.List;
import java.util.Optional;

public class NetworkPlayer implements Player {
    private final String serverUrl;
    private final OkHttpClient client;
    private final Gson gson = new Gson();

    public NetworkPlayer(String serverUrl) {
        this.serverUrl = serverUrl;
        this.client = new OkHttpClient();
    }

    @Override
    public Decision makeDecision(GameState state, ImmutableList<Decision> options, Optional<Event> reason) {
        DecisionRequest req = new DecisionRequest(state, options.asList(), reason.orElse(null), "network-player-uuid");
        RequestBody body = RequestBody.create(gson.toJson(req), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(serverUrl + "/decide")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful())
                throw new RuntimeException("Failed response: " + response);

            DecisionResponse res = gson.fromJson(response.body().string(), DecisionResponse.class);
            return res.getDecision();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void logEvent(Event event) {
        LogEventRequest req = new LogEventRequest(event, "network-player-uuid");
        RequestBody body = RequestBody.create(gson.toJson(req), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(serverUrl + "/log-event")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful())
                System.err.println("Failed logging event: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "Network Player";
    }

    @Override
    public Optional<GameObserver> getObserver() {
        return Optional.empty();
    }

    // The following method can delegate to makeDecision to maintain compatibility
    public Decision makeDecision(GameState state, List<Decision> options, Event reason) {
        return makeDecision(state, ImmutableList.copyOf(options), Optional.ofNullable(reason));
    }

    // Inner classes for request and response objects

    private static class DecisionRequest {
        private final GameState state;
        private final List<Decision> options;
        private final Event reason;
        private final String player_uuid;

        public DecisionRequest(GameState state, List<Decision> options, Event reason, String player_uuid) {
            this.state = state;
            this.options = options;
            this.reason = reason;
            this.player_uuid = player_uuid;
        }
    }

    private static class DecisionResponse {
        private Decision decision;

        public Decision getDecision() {
            return decision;
        }
    }

    private static class LogEventRequest {
        private final Event decision;
        private final String player_uuid;

        public LogEventRequest(Event decision, String player_uuid) {
            this.decision = decision;
            this.player_uuid = player_uuid;
        }
    }

}