package jfoucault.mqttaudioswitcher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import jfoucault.mqttaudioswitcher.R;


public class MqttNewConnectionActivity extends Activity {
    public static Intent startActivity(Activity activity) {
        Intent intent = new Intent(activity, MqttNewConnectionActivity.class);
        activity.startActivity(intent);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_connection);
    }
}
