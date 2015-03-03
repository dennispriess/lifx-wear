import android.app.Activity;
import android.os.Bundle;

import app.dpriess.de.myapplication.R;

/**
 * Created by dennispriess on 03/03/15.
 */
public class MasterActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);
        LightFragment lightsFragment = new LightFragment();
        lightsFragment.setArguments(getIntent().getExtras());

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, lightsFragment)
                    .commit();
        }

    }

}