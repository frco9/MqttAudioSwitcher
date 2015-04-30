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
        return new GuidanceStylist.Guidance("Preferences", null, getString(R.string.app_name), null);
    }

    @Override
    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {



        actions.add(new GuidedAction.Builder().id(11).infoOnly(true).title("Test").build());

        actions.add(new GuidedAction.Builder()
                .id(12)
                .title("Test")
                .description("Description")
                .checked(true)
                .iconResourceId(R.drawable.ic_play_playcontrol_normal, getActivity()).build());

        actions.add(new GuidedAction.Builder()
                .id(13)
                .title("Test 2")
                .description("Description")
                .iconResourceId(R.drawable.ic_pause_playcontrol_normal, getActivity()).build());

        super.onCreateActions(actions, savedInstanceState);
    }

}
