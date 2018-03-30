package march.marchappl.dagger;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Provides;
import dagger.Reusable;
import march.marchappl.BetsDatabase;
import march.marchappl.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static march.marchappl.utils.GlobalConstants.SERVER_SSL_URL;

@Reusable
public class LibRepository {

    @Inject
    public LibRepository() {
    }

    public String getTestIdea() {
        return "here is my test.";
    }

    @Singleton
    public Retrofit provideRetrofit() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .validateEagerly(true)
                .baseUrl(SERVER_SSL_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return retrofit;
    }

    @Singleton
    public BetsDatabase provideDatabase(Application application) {
        return Room.databaseBuilder(application,
                BetsDatabase.class, "DatabaseSweepstakes.db")
                .build();
    }

}
