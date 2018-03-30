package march.marchappl;

import android.app.Application;
import march.marchappl.dagger.AppComponent;
import march.marchappl.dagger.AppModule;
import march.marchappl.dagger.DaggerAppComponent;
import march.marchappl.dagger.DaggerLibComponent;
import march.marchappl.dagger.LibComponent;
import march.marchappl.dagger.LibModule;

/**
 * Created by Aleksei Jegorov on 3/27/2018.
 */
public class BetsApplication extends Application {

    private static final String TAG = "BETS_MATCHES";
    private static BetsApplication sApp;
    public BetsDatabase betsDatabase;

    public BetsDatabase getInstanceDatabase() {
        return betsDatabase;
    }

    private LibComponent mLibComponent;

    public LibComponent getLibComponent() {
        return mLibComponent;
    }

    private AppComponent mAppComponent;

    public AppComponent getAppComponent() {
        return mAppComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();

        mLibComponent = DaggerLibComponent.builder()
                .libModule(new LibModule())
                .build();

        sApp = this;

        betsDatabase = mLibComponent.getLibRepository().provideDatabase(sApp);
    }

    public static BetsApplication getApp() {
        return sApp;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

}