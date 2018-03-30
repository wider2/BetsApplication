package march.marchappl;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import march.marchappl.dagger.DaggerLibComponent;
import march.marchappl.dagger.LibRepository;
import march.marchappl.predictions.PredictionsFragment;
import march.marchappl.results.ResultsFragment;

public class MainActivity extends AppCompatActivity
        implements ConnectivityReceiver.ConnectivityReceiverListener {

    private Unbinder unbinder;
    Snackbar snackBar;

    @BindView(R.id.tv_output)
    TextView tvOutput;

    @Inject
    SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        unbinder = ButterKnife.bind(this);
        checkConnection();

        ((BetsApplication) getApplication()).getAppComponent().inject(this);
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        if (savedInstanceState == null) { //check if we change orientation

            if (prefs.getString("layout_state", "0").equals("0")) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .add(R.id.home_fragment_container, new PredictionsFragment(), PredictionsFragment.class.getName())
                        .addToBackStack(null)
                        .commit();
            } else {
                getSupportFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .add(R.id.home_fragment_container, new ResultsFragment(), ResultsFragment.class.getName())
                        .addToBackStack(null)
                        .commit();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    //internet connection to check offline mode
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) checkConnection();

        BetsApplication.getApp().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {
        if (!isConnected) {
            snackBar = Snackbar
                    .make(tvOutput, getString(R.string.no_connection), Snackbar.LENGTH_INDEFINITE)
                    .setActionTextColor(getResources().getColor(R.color.yellow))
                    .setAction(getString(R.string.network_settings), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    });

            View sbView = snackBar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackBar.show();
        } else {
            if (snackBar != null) snackBar.dismiss();
        }
    }

}
