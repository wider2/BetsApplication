package march.marchappl.results;

import android.content.Context;
import android.util.Log;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import march.marchappl.BetsDatabase;
import march.marchappl.BuildConfig;
import march.marchappl.R;
import march.marchappl.dagger.DaggerLibComponent;
import march.marchappl.dagger.LibModule;
import march.marchappl.dagger.LibRepository;
import march.marchappl.model.DownloadMatches;
import march.marchappl.model.DownloadResults;
import march.marchappl.model.JSONResponse;
import march.marchappl.model.JSONResults;
import march.marchappl.model.Match;
import march.marchappl.model.Matches;
import march.marchappl.model.Prediction;
import march.marchappl.model.Results;
import march.marchappl.model.Session;
import march.marchappl.model.Team;
import march.marchappl.utils.GlobalConstants;
import march.marchappl.utils.RetrofitApi;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static march.marchappl.utils.GlobalConstants.SERVER_SSL_URL;


public class ResultsFragmentPresenter {

    private static final String TAG = "BETS_RESULTS";
    private List<DownloadResults> data;
    private List<Prediction> predictions;
    private List<Results> results;

    private IResultsFragment mainView;
    private Context mContext;
    private BetsDatabase mDb;
    private Retrofit retrofit;

    @Inject
    LibRepository libRepository;

    public ResultsFragmentPresenter(IResultsFragment mainView, Context ctx) {
        this.mainView = mainView;
        this.mContext = ctx;
        mDb = BetsDatabase.getInstance(ctx);

        DaggerLibComponent.builder().libModule(new LibModule()).build();
        this.libRepository = DaggerLibComponent.create().getLibRepository();
    }


    public void checkLastSession() {

        Observable.fromCallable(new Callable<Session>() {
            @Override
            public Session call() {
                results = mDb.betsDao().getAllResults();

                Session session = mDb.betsDao().checkLastVisit(GlobalConstants.addMinuteTimestamp());
                return (session != null) ? session : new Session();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Session>() {
                    @Override
                    public void accept(Session session) throws Exception {
                        if (results.isEmpty()) {
                            getResultsList();
                        } else {
                            //if data has 1 minute old
                            if (session.getLastVisit() == 0 || session.getLastVisit() < GlobalConstants.addMinuteTimestamp()) {
                                getResultsList();
                            } else {
                                getMatchesFromDatabase();
                            }
                        }
                    }
                });
    }

    private void getMatchesFromDatabase() {

        Observable.fromCallable(new Callable<List<Matches>>() {
            @Override
            public List<Matches> call() {
                results = mDb.betsDao().getAllResults();
                predictions = mDb.betsDao().getPredictions();
                return mDb.betsDao().loadAllMatches();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Matches>>() {
                    @Override
                    public void accept(List<Matches> matches) throws Exception {
                        mainView.refreshResult(matches, predictions, results);
                    }
                });
    }

    public void getResultsList() {
        try {
            retrofit = libRepository.provideRetrofit();
            RetrofitApi retrofitApi = retrofit.create(RetrofitApi.class);

            retrofitApi.getResults(new Random().nextInt(99999))
                    .filter(new Predicate<JSONResults>() {
                        @Override
                        public boolean test(@NonNull JSONResults jsonResults) throws Exception {
                            return (jsonResults.getMatches().length > 0);
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<JSONResults>() {
                        @Override
                        public void accept(JSONResults jsonResults) throws Exception {
                            data = new ArrayList<>(Arrays.asList(jsonResults.getMatches()));
                            Log.d(TAG, "Data received: " + String.valueOf(data.size()));
                            createResultsInDatabase(mDb, data);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            mainView.showErrorServerResponse(throwable);
                        }
                    });
        } catch (Exception ex) {
            mainView.showException(ex);
        }
    }


    private void createResultsInDatabase(final BetsDatabase db, final List<DownloadResults> matches) {
        Observable.fromCallable(new Callable<List<Matches>>() {
            @Override
            public List<Matches> call() {

                Calendar now = Calendar.getInstance();
                Session session = new Session();
                session.setLastVisit(now.getTimeInMillis());
                db.betsDao().insertNewVisit(session);

                for (DownloadResults item : matches) {
                    Team team = null;
                    String teamName = item.getTeam1();
                    if (teamName != null) {
                        team = db.betsDao().loadByTeamName(teamName);

                        //if team absent, create new one
                        if (team == null) {
                            team = new Team();
                            team.setTeamName(teamName);
                            db.betsDao().insertTeam(team);

                            //get id of the team for Matches
                            team = db.betsDao().loadByTeamName(teamName);
                        }
                    }

                    Team team2 = null;
                    String teamName2 = item.getTeam2();
                    if (teamName2 != null) {
                        team2 = db.betsDao().loadByTeamName(teamName2);
                        if (team2 == null) {
                            team2 = new Team();
                            team2.setTeamName(teamName2);
                            db.betsDao().insertTeam(team2);
                            team2 = db.betsDao().loadByTeamName(teamName2);
                        }
                    }
                    Results results = db.betsDao().loadResultByIds(team.getTeam_id(), team2.getTeam_id());
                    if (results == null) {
                        Results results1 = new Results();
                        results1.setTeamId1(team.getTeam_id());
                        results1.setTeamId2(team2.getTeam_id());
                        results1.setTeam_points1(item.getTeam1_points());
                        results1.setTeam_points2(item.getTeam2_points());
                        db.betsDao().insertRealResults(results1);
                    }
                }
                results = db.betsDao().getAllResults();

                predictions = db.betsDao().getPredictions();

                return db.betsDao().loadAllMatches();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Matches>>() {
                    @Override
                    public void accept(List<Matches> matches) throws Exception {
                        mainView.refreshResult(matches, predictions, results);
                    }
                });
    }


    public void deleteMatchesPredictions() {

        Observable.fromCallable(new Callable<List<Prediction>>() {
            @Override
            public List<Prediction> call() {
                mDb.betsDao().deletePredictions();
                return mDb.betsDao().getPredictions();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Prediction>>() {
                    @Override
                    public void accept(List<Prediction> predictions) throws Exception {
                        if (predictions.isEmpty()) {
                            mainView.refreshPrediction();
                        } else {
                            mainView.showException(new Exception(mContext.getString(R.string.db_operation_failed)));
                        }
                    }
                });
    }

}