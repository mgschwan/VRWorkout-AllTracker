package org.xrworkout.alltracker;

import android.util.Log;

import com.google.ar.core.Pose;
import com.google.ar.core.TrackingState;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This example demonstrates how to create a websocket connection to a server. Only the most
 * important callbacks are overloaded.
 */
public class GodotARVRControllerClient extends WebSocketClient {
    private final String TAG = "GodotARVRControllerClient";

    enum TrackingSource {
        NONE, CAM, IMU, FULL;
    }

    public GodotARVRControllerClient(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public GodotARVRControllerClient(URI serverURI) {
        super(serverURI);
    }

    public GodotARVRControllerClient(URI serverUri, Map<String, String> httpHeaders) {
        super(serverUri, httpHeaders);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {

        JSONObject config_message = new JSONObject();
        try {
            config_message.put("type", "config");
            config_message.put("tracker_id", "VRWorkoutAlltracker 1");
            config_message.put("tracker_type", "foot/right");
            send (config_message.toString());
        } catch (JSONException e)
        {
            Log.e(TAG, "Could not create config message");
            // do nothing
        }
        System.out.println("opened connection");
        // if you plan to refuse connection based on ip or httpfields overload: onWebsocketHandshakeReceivedAsClient
    }

    @Override
    public void onMessage(String message) {
        System.out.println("received: " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        // The codecodes are documented in class org.java_websocket.framing.CloseFrame
        System.out.println(
                "Connection closed by " + (remote ? "remote peer" : "us") + " Code: " + code + " Reason: "
                        + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
        // if the error is fatal then onClose will be called additionally
    }

    public void sendPosition(Pose p, TrackingSource src) {
        JSONObject position_message = new JSONObject();
        try {
            position_message.put("type", "pos");
            position_message.put("x", p.tx());
            position_message.put("y", p.ty());
            position_message.put("z", p.tz());
            position_message.put("qx", p.qx());
            position_message.put("qy", p.qy());
            position_message.put("qz", p.qz());
            position_message.put("qw", p.qw());
            String tracking_src = "none";
            if (src == TrackingSource.CAM) {
                tracking_src = "cam";
            }
            if (src == TrackingSource.IMU) {
                tracking_src = "imu";
            }
            if (src == TrackingSource.FULL) {
                tracking_src = "full";
            }

            position_message.put("src", tracking_src);
            if (isOpen()) {
                send(position_message.toString());
            }

        } catch (JSONException e)
        {
            Log.e(TAG, "Could not create position message");
            // do nothing
        }
    }


}



