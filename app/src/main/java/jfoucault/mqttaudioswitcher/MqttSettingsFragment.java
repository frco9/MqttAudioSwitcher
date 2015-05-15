package jfoucault.mqttaudioswitcher;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;

import java.util.List;


public class MqttSettingsFragment extends GuidedStepFragment {

    @NonNull
    @Override
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        return new GuidanceStylist.Guidance(getString(R.string.settings), null, getString(R.string.app_name), null);
    }

    @Override
    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {

        actions.add(new GuidedAction.Builder()
                .id(R.id.action_new_connection)
                .title(getString(R.string.action_edit_connection))
                .description(getString(R.string.action_edit_connection_desc))
                .checked(true)
                .build());

        super.onCreateActions(actions, savedInstanceState);
    }

    @Override
    public void onGuidedActionClicked(GuidedAction action) {
        switch ((int) action.getId()) {
            case R.id.action_new_connection:
                MqttNewConnectionActivity.startActivity(getActivity());
                return;
        }
        super.onGuidedActionClicked(action);
    }


}
