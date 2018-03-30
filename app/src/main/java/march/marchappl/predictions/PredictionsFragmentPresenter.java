package march.marchappl.predictions;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import march.marchappl.BetsDatabase;
import march.marchappl.dagger.DaggerLibComponent;
import march.marchappl.dagger.LibModule;
import march.marchappl.dagger.LibRepository;
import march.marchappl.model.DownloadMatches;
import march.marchappl.model.JSONResponse;
import march.marchappl.model.Match;
import march.marchappl.model.Matches;
import march.marchappl.model.Prediction;
import march.marchappl.model.Session;
import march.marchappl.model.Team;
import march.marchappl.utils.GlobalConstants;
import march.marchappl.utils.RetrofitApi;
import retrofit2.Retrofit;

public class PredictionsFragmentPresenter {

    private static final String TAG = "BETS_MATCHES";
    private List<DownloadMatches> data;
    List<Prediction> predictions;

    private IPredictionsFragment mainView;
    Context mContext;
    BetsDatabase mDb;
    private Retrofit retrofit;

    @Inject
    LibRepository libRepository;

    public PredictionsFragmentPresenter(IPredictionsFragment mainView, Context ctx) {
        this.mainView = mainView;
        this.mContext = ctx;
        mDb = BetsDatabase.getInstance(ctx);

        DaggerLibComponent.builder().libModule(new LibModule()).build();
        this.libRepository = DaggerLibComponent.create().getLibRepository();
    }

    //we download from Internet or from local SQLlite db
    public void checkLastSession() {

        Observable.fromCallable(new Callable<Session>() {
            @Override
            public Session call() {
                Session session = mDb.betsDao().checkLastVisit(GlobalConstants.addMinuteTimestamp());
                return (session != null) ? session : new Session();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Session>() {
                    @Override
                    public void accept(Session session) throws Exception {
                        //if data has 1 minute old
                        if (session.getLastVisit() == 0 || session.getLastVisit() < GlobalConstants.addMinuteTimestamp()) {
                            getMatchesList();
                        } else {
                            getMatchesFromDatabase();
                        }
                    }
                });
    }

    private void getMatchesFromDatabase() {

        Observable.fromCallable(new Callable<List<Matches>>() {
            @Override
            public List<Matches> call() {
                predictions = mDb.betsDao().getPredictions();
                return mDb.betsDao().loadAllMatches();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Matches>>() {
                    @Override
                    public void accept(List<Matches> matches) throws Exception {
                        mainView.refreshResult(matches, predictions);
                    }
                });
    }

    public void getMatchesList() {
        try {
            //dagger call Singleton instance of retrofit
            retrofit = libRepository.provideRetrofit();
            RetrofitApi retrofitApi = retrofit.create(RetrofitApi.class);

            retrofitApi.getMatches(new Random().nextInt(99999))
                    .filter(new Predicate<JSONResponse>() {
                        @Override
                        public boolean test(@NonNull JSONResponse jsonResponse) throws Exception {
                            return (jsonResponse.getMatches().length > 0);
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<JSONResponse>() {
                        @Override
                        public void accept(JSONResponse jsonResponse) throws Exception {
                            data = new ArrayList<>(Arrays.asList(jsonResponse.getMatches()));
                            Log.d(TAG, "Data received: " + String.valueOf(data.size()));
                            createTeamInDatabase(mDb, data);
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


    //check for teams in database
    private void createTeamInDatabase(final BetsDatabase db, final List<DownloadMatches> matches) {
        Observable.fromCallable(new Callable<List<Matches>>() {
            @Override
            public List<Matches> call() {

                Calendar now = Calendar.getInstance();
                Session session = new Session();
                session.setLastVisit(now.getTimeInMillis());
                db.betsDao().insertNewVisit(session);

                for (DownloadMatches item : matches) {
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

                    //check if match exist in db
                    Match mmm = db.betsDao().loadMatchByIds(team.getTeam_id(), team2.getTeam_id());
                    if (mmm == null) {
                        Match match = new Match();
                        match.setTeamId1(team.getTeam_id());
                        match.setTeamId2(team2.getTeam_id());
                        db.betsDao().insertMatch(match);
                    }
                }

                predictions = db.betsDao().getPredictions();

                return db.betsDao().loadAllMatches();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Matches>>() {
                    @Override
                    public void accept(List<Matches> matches) throws Exception {
                        mainView.refreshResult(matches, predictions);
                    }
                });
    }


    public void saveMatchPrediction(final Prediction prediction) {

        if (prediction != null) {
            Observable.fromCallable(new Callable<List<Prediction>>() {
                @Override
                public List<Prediction> call() {
                    Prediction selection = mDb.betsDao().loadPredictionById(prediction.getTeamId1(), prediction.getTeamId2());
                    if (selection == null) {
                        mDb.betsDao().insertPrediction(prediction);
                    } else {
                        mDb.betsDao().updatePrediction(prediction.getTeamId1(), prediction.getTeamId2(), prediction.getScore1(), prediction.getScore2());
                    }

                    return mDb.betsDao().getPredictions();
                }
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<List<Prediction>>() {
                        @Override
                        public void accept(List<Prediction> predictions) throws Exception {
                            mainView.refreshPrediction(predictions);
                        }
                    });
        }
    }


}