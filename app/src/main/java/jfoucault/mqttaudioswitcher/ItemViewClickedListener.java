/*******************************************************************************
 * Copyright (c) 1999, 2014 IBM Corp.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution. 
 *
 * The Eclipse Public License is available at 
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 *   http://www.eclipse.org/org/documents/edl-v10.php.
 */
package jfoucault.mqttaudioswitcher;

import android.content.Context;
import android.content.Intent;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.Log;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import jfoucault.mqttaudioswitcher.ActionListener.Action;
import jfoucault.mqttaudioswitcher.Connection.ConnectionStatus;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.LogManager;


public class ItemViewClickedListener implements OnItemViewClickedListener {

  /** The handle to a {@link Connection} object which contains the {@link MqttAndroidClient} associated with this object **/
  private String clientHandle = null;


  /** {@link Context} used to load and format strings **/
  private Context context = null;


  /**
   * Constructs a listener object for use with {@link jfoucault.mqttaudioswitcher.MainActivity} activity and
   * associated fragments.
   * @param context The instance of {@link jfoucault.mqttaudioswitcher.MainActivity}
   * @param clientHandle The handle to the client that the actions are to be performed on
   */
  public ItemViewClickedListener(Context context, String clientHandle)
  {
    this.clientHandle = clientHandle;
    this.context = context;

  }

    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                          RowPresenter.ViewHolder rowViewHolder, Row row) {

        /*if (item instanceof Movie) {
            Movie movie = (Movie) item;
            Log.d(TAG, "Item: " + item.toString());
            Intent intent = new Intent(getActivity(), DetailsActivity.class);
            intent.putExtra(DetailsActivity.MOVIE, movie);

            Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    getActivity(),
                    ((ImageCardView) itemViewHolder.view).getMainImageView(),
                    DetailsActivity.SHARED_ELEMENT_NAME).toBundle();
            getActivity().startActivity(intent, bundle);
        } else */
        if (item instanceof String) {
            if (((String) item).indexOf(context.getString(R.string.headphones_output)) >= 0) {
                publishHeadphones();
                //Intent intent = new Intent(context, BrowseErrorActivity.class);
                //context.startActivity(intent);
            } else if (((String) item).indexOf(context.getString(R.string.speakers_output)) >= 0) {
                publishSpeakers();
            } else {
                Toast.makeText(context, ((String) item), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }




  /**
   * Disconnect the client
   */
  private void disconnect() {

    Connection c = Connections.getInstance(context).getConnection(clientHandle);

    //if the client is not connected, process the disconnect
    if (!c.isConnected()) {
      return;
    }

    try {
      c.getClient().disconnect(null, new ActionListener(context, Action.DISCONNECT, clientHandle, null));
      c.changeConnectionStatus(ConnectionStatus.DISCONNECTING);
    }
    catch (MqttException e) {
      Log.e(this.getClass().getCanonicalName(), "Failed to disconnect the client with the handle " + clientHandle, e);
      c.addAction("Client failed to disconnect");
    }

  }


   private  void publishSpeakers() {
       String topic = "/audio/switch/to/speakers";
       String message = "SwitchToSpeakers";
       publish(topic, message, 0, false);
   }


    private  void publishHeadphones() {
        String topic = "/audio/switch/to/headphones";
        String message = "SwitchToHeadphones";
        publish(topic, message, 0, false);
    }


    /**
   * Publish the message the user has specified
   */
  private void publish(String topic, String message, int qos, boolean retained)
  {
    String[] args = new String[2];
    args[0] = message;
    args[1] = topic+";qos:"+qos+";retained:"+retained;

    try {
      Connections.getInstance(context).getConnection(clientHandle).getClient()
          .publish(topic, message.getBytes(), qos, retained, null, new ActionListener(context, Action.PUBLISH, clientHandle, args));
    }
    catch (MqttSecurityException e) {
      Log.e(this.getClass().getCanonicalName(), "Failed to publish a message from the client with the handle " + clientHandle, e);
    }
    catch (MqttException e) {
      Log.e(this.getClass().getCanonicalName(), "Failed to publish a message from the client with the handle " + clientHandle, e);
    }

  }

}
