/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package jfoucault.mqttaudioswitcher;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.Presenter;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;


public class MainFragment extends BrowseFragment {
    private static final String TAG = "MainFragment";

    private static final int GRID_ITEM_WIDTH = 200;
    private static final int GRID_ITEM_HEIGHT = 200;

    private String clientHandle = null;
    private ArrayObjectAdapter mRowsAdapter;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        clientHandle = "androidTVHandle";

        super.onActivityCreated(savedInstanceState);

        setupUIElements();

        loadRows();

        startConnection();

        setupEventListeners();
    }


    private void loadRows() {
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());

        // Output switcher
        HeaderItem gridHeader = new HeaderItem(0, getString(R.string.switch_output));
        GridItemPresenter mGridPresenter = new GridItemPresenter();
        ArrayObjectAdapter gridRowAdapter = new ArrayObjectAdapter(mGridPresenter);
        gridRowAdapter.add(getString(R.string.speakers_output));
        gridRowAdapter.add(getString(R.string.headphones_output));
        mRowsAdapter.add(new ListRow(gridHeader, gridRowAdapter));

        // Params
        gridHeader = new HeaderItem(0, getString(R.string.parameters));
        mGridPresenter = new GridItemPresenter();
        gridRowAdapter = new ArrayObjectAdapter(mGridPresenter);
        gridRowAdapter.add(getString(R.string.reconnect_broker));
        gridRowAdapter.add(getString(R.string.disconnect_broker));
        mRowsAdapter.add(new ListRow(gridHeader, gridRowAdapter));

        setAdapter(mRowsAdapter);
    }

    /**
     * Create the mqtt connection
     */
    private void startConnection() {
        Connection c = Connection.getInstance(this.getActivity());
        if (c != null) {
            if (c.isConnected()) {
                return;
            }

            c.changeConnectionStatus(Connection.ConnectionStatus.CONNECTING);

            try {
                c.getClient().connect(c.getConnectionOptions(), null, new ActionListener(getActivity(), ActionListener.Action.CONNECT, clientHandle, null));
            } catch (MqttSecurityException e) {
                Log.e(this.getClass().getCanonicalName(), "Failed to connect the client with the handle " + clientHandle, e);
                c.addAction("Client failed to connect");
            } catch (MqttException e) {
                Log.e(this.getClass().getCanonicalName(), "Failed to connect the client with the handle " + clientHandle, e);
                c.addAction("Client failed to connect");
            }
        } else {
            String actionTaken = getString(R.string.toast_no_connection);
            Notify.toast(getActivity(), actionTaken, Toast.LENGTH_SHORT);
        }
    }



    private void setupUIElements() {
        // setBadgeDrawable(getActivity().getResources().getDrawable(
        // R.drawable.videos_by_google_banner));
        setTitle(getString(R.string.browse_title)); // Badge, when set, takes precedent
        // over title
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);

        // set fastLane (or headers) background color
        setBrandColor(getResources().getColor(R.color.fastlane_background));
    }

    private void setupEventListeners() {
        setOnItemViewClickedListener(new ItemViewClickedListener(this.getActivity(), clientHandle));
    }


    private class GridItemPresenter extends Presenter {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            TextView view = new TextView(parent.getContext());
            view.setLayoutParams(new ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT));
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.setBackgroundColor(getResources().getColor(R.color.default_background));
            view.setTextColor(Color.WHITE);
            view.setGravity(Gravity.CENTER);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item) {
            ((TextView) viewHolder.view).setText((String) item);
        }

        @Override
        public void onUnbindViewHolder(ViewHolder viewHolder) {
        }
    }

}
