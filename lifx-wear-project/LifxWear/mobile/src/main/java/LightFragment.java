import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import app.dpriess.de.myapplication.R;

/**
 * Created by dennispriess on 03/03/15.
 */
public class LightFragment extends Fragment implements View.OnClickListener {

    private boolean isTimerFinished = true;

    private CountDownTimer mCountDownTimer;

    private EditText mCountdownEditText;

    private Button mLightButton, mSetCountdownButton;

    private LFXNetworkContext mLocalNetworkContext;

    private TextView mTimeLeftText;

    public LightFragment() {
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void createNotification() {

        // prepare intent which is triggered if the
        // notification button is selected

        Intent intent = new Intent(getActivity(), MasterActivity.class);
        intent.putExtra("lifx", true);
        PendingIntent pIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);

        NotificationManager notificationManager =
                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getActivity())
                        .setSmallIcon(R.drawable.common_ic_googleplayservices)
                        .setContentTitle("Control your lifx")
                        .setContentText("turn light on / off")
                        .setAutoCancel(false)
                        .addAction(R.drawable.common_ic_googleplayservices,
                                "On / Off", pIntent);

        notificationManager.notify(0, notificationBuilder.build());


    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            // turn all lights off
            case R.id.button:
                createNotification();
                if (mLocalNetworkContext.getAllLightsCollection().getFuzzyPowerState()
                        == LFXTypes.LFXFuzzyPowerState.OFF) {
                    mLocalNetworkContext.getAllLightsCollection()
                            .setPowerState(LFXTypes.LFXPowerState.ON);

                } else {
                    mLocalNetworkContext.getAllLightsCollection()
                            .setPowerState(LFXTypes.LFXPowerState.OFF);
                }
                break;

            case R.id.countdown_button:

                int minutes = Integer.parseInt(mCountdownEditText.getEditableText().toString());

                int minutesInMillis = minutes * 1000 * 60;

                if (isTimerFinished) {
                    startCountdown(minutesInMillis);
                } else {
                    mCountDownTimer.cancel();
                    startCountdown(minutesInMillis);
                }

                break;
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocalNetworkContext = LFXClient.getSharedInstance(getActivity())
                .getLocalNetworkContext();
        mLocalNetworkContext.connect();

        if (getArguments() != null) {
            getArguments().getBoolean("lifx", false);
            Toast.makeText(getActivity(), "bundle contains boolean", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_master, container, false);
        mLightButton = (Button) rootView.findViewById(R.id.button);
        mCountdownEditText = (EditText) rootView.findViewById(R.id.countdown_setter);
        mSetCountdownButton = (Button) rootView.findViewById(R.id.countdown_button);
        mTimeLeftText = (TextView) rootView.findViewById(R.id.countdown_left);

        mSetCountdownButton.setOnClickListener(this);
        mLightButton.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocalNetworkContext.disconnect();
    }

    private void startCountdown(final int millisMinutes) {
        mCountDownTimer = new CountDownTimer(millisMinutes, 1000) {

            public void onTick(long millisUntilFinished) {
                isTimerFinished = false;
                mTimeLeftText
                        .setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                isTimerFinished = true;
                mTimeLeftText.setText("done!");
                mLocalNetworkContext.getAllLightsCollection()
                        .setPowerState(LFXTypes.LFXPowerState.OFF);
            }
        }.start();
    }
}