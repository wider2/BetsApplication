package march.marchappl.dagger;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class LibModule {

    @Provides
    public LibRepository getLibRepository(){
        return new LibRepository();
    }

}