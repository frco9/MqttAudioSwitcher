package jfoucault.mqttaudioswitcher;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

import java.util.List;


public class MqttNewConnectionFragment extends GuidedStepFragment {

    @NonNull
    @Override
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        return new GuidanceStylist.Guidance("New Connection", "Information to connect to the mqtt broker", null, null);
    }

    @Override
    public GuidanceStylist onCreateGuidanceStylist() {
        return new TermsGuidanceStylist();
    }

    @Override
    public void onStart() {
        super.onStart();
        Connection connection = Connection.getInstance(getActivity());
        if (connection != null) {
            populateForm(connection);
        }

    }

    @Override
    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        GuidedAction saveAction = new GuidedAction.Builder().id(R.id.action_save).hasNext(true).title(getString(R.string.action_save)).build();
        GuidedAction cancelAction = new GuidedAction.Builder().id(R.id.action_cancel).hasNext(true).title(getString(R.string.action_cancel)).build();
        actions.add(saveAction);
        actions.add(cancelAction);
        super.onCreateActions(actions, savedInstanceState);

        //setSelectedActionPosition(0);
    }

    @Override
    public void onGuidedActionClicked(GuidedAction action) {
        final EditText clientIdField = (EditText) getActivity().findViewById(R.id.EditTextClientID);
        String clientId = clientIdField.getText().toString();
        final EditText hostField = (EditText) getActivity().findViewById(R.id.EditTextHost);
        String host = hostField.getText().toString();
        final EditText portField = (EditText) getActivity().findViewById(R.id.EditTextPort);
        String rawPort = portField.getText().toString();
        int port = rawPort.length()>0 ? Integer.parseInt(rawPort) : 0;

        switch ((int) action.getId()) {
            case R.id.action_save:
                Connection connection = Connection.getInstance(getActivity());
                // Quick form validation
                if (clientId.isEmpty()) {
                    String noticeMsg = getActivity().getString(R.string.toast_form_valid_clientId);
                    Notify.toast(getActivity(), noticeMsg, Toast.LENGTH_SHORT);
                    return;
                } else if (host.isEmpty()) {
                    String noticeMsg = getActivity().getString(R.string.toast_form_valid_host);
                    Notify.toast(getActivity(), noticeMsg, Toast.LENGTH_SHORT);
                    return;
                } else if (port <= 0) {
                    String noticeMsg = getActivity().getString(R.string.toast_form_valid_port);
                    Notify.toast(getActivity(), noticeMsg, Toast.LENGTH_SHORT);
                    return;
                }
                if (connection != null) {
                    // TODO : Get connection options from the connection object
                    ConnectionDBData data = new ConnectionDBData(clientId, host, port, false, null, null);
                    connection.updateConnection(data);
                } else {
                    Persistence persistence = new Persistence(getActivity());
                    // TODO : Get proper default values for connection options
                    ConnectionDBData data = new ConnectionDBData(clientId, host, port, false, null, null);
                    try {
                        persistence.persistConnection(data);
                    } catch (PersistenceException e) {
                        e.printStackTrace();
                    }
                }
                disconnect();
                reconnect();
                getActivity().finish();
                return;
            case R.id.action_cancel:
                getActivity().finish();
                return;
        }
        super.onGuidedActionClicked(action);
    }

    public static class TermsGuidanceStylist extends GuidanceStylist {

        @Override
        public int onProvideLayoutId() {
            return R.layout.form_guidance;
        }
    }

    private void disconnect() {

        Connection c = Connection.getInstance(getActivity());

        //if the client is not connected, process the disconnect
        if (!c.isConnected()) {
            return;
        }

        try {
            c.getClient().disconnect(null, new ActionListener(getActivity(), ActionListener.Action.DISCONNECT, c.handle(), null));
            c.changeConnectionStatus(Connection.ConnectionStatus.DISCONNECTING);
        } catch (MqttException e) {
            Log.e(this.getClass().getCanonicalName(), "Failed to disconnect the client", e);
            c.addAction("Client failed to disconnect");
        }
    }

    private void reconnect() {
        Connection c = Connection.getInstance(getActivity());
        if (c != null) {
            //if the client is not connected, process the disconnect
            if (c.isConnected()) {
                return;
            }

            c.changeConnectionStatus(Connection.ConnectionStatus.CONNECTING);

            try {
                c.getClient().connect(c.getConnectionOptions(), null, new ActionListener(getActivity(), ActionListener.Action.CONNECT, c.handle(), null));
            } catch (MqttSecurityException e) {
                Log.e(this.getClass().getCanonicalName(), "Failed to reconnect the client", e);
                c.addAction("Client failed to connect");
            } catch (MqttException e) {
                Log.e(this.getClass().getCanonicalName(), "Failed to reconnect the client", e);
                c.addAction("Client failed to connect");
            }
        }
    }

    private void populateForm(Connection connection) {
        final EditText clientIdField = (EditText) getActivity().findViewById(R.id.EditTextClientID);
        clientIdField.setText(connection.getId());
        final EditText hostField = (EditText) getActivity().findViewById(R.id.EditTextHost);
        hostField.setText(connection.getHostName());
        final EditText portField = (EditText) getActivity().findViewById(R.id.EditTextPort);
        portField.setText(String.valueOf(connection.getPort()));
    }
}
