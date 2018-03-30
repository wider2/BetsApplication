package march.marchappl.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.Date;
import java.util.List;

import march.marchappl.model.Match;
import march.marchappl.model.Matches;
import march.marchappl.model.Prediction;
import march.marchappl.model.Results;
import march.marchappl.model.Session;
import march.marchappl.model.Team;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;


@Dao
public interface BetsDao {

    @Query("SELECT * FROM match WHERE id = :matchId")
    Match loadMatchById(int matchId);

    @Query("SELECT * FROM match WHERE team_id1 = :teamId1 and team_id2 = :teamId2")
    Match loadMatchByIds(int teamId1, int teamId2);

    @Query("SELECT * FROM match")
    List<Match> loadMatches();

    @Query("SELECT match.id, match.team_id1, t1.team_name as team_name1, match.team_id2, t2.team_name as team_name2 FROM match" +
            " INNER JOIN team as t1 ON match.team_id1 = t1.team_id" +
            " INNER JOIN team as t2 ON match.team_id2 = t2.team_id"
    )
    List<Matches> loadAllMatches();


    @Query("SELECT * FROM team WHERE team_name = :teamName")
    Team loadByTeamName(String teamName);

    @Insert(onConflict = IGNORE)
    public abstract void insertTeam(Team team);

    @Insert
    public abstract void insertMatch(Match match);


    //cache data
    @Query("SELECT * FROM session WHERE last_visit > :lastVisit")
    Session checkLastVisit(long lastVisit);

    @Insert(onConflict = IGNORE)
    public abstract void insertNewVisit(Session session);

    //@Query("SELECT * FROM session WHERE last_visit_results > :lastVisitResults")
    //Session checkLastVisitResults(long lastVisitResults);


    //work with Scores
    @Query("SELECT * FROM prediction")
    List<Prediction> getPredictions();

    @Query("SELECT * FROM prediction WHERE team_id1 = :teamId1 and team_id2 = :teamId2")
    Prediction loadPredictionById(int teamId1, int teamId2);

    @Insert
    public abstract void insertPrediction(Prediction prediction);

    @Query("DELETE FROM Prediction")
    void deletePredictions();

    @Query("UPDATE prediction SET score1 = :score1, score2 = :score2 WHERE team_id1 = :teamId1 and team_id2 = :teamId2")
    void updatePrediction(int teamId1, int teamId2, int score1, int score2);


    //real results
    @Query("SELECT * FROM results WHERE team_id1 = :teamId1 and team_id2 = :teamId2")
    Results loadResultByIds(int teamId1, int teamId2);

    @Query("SELECT * FROM results")
    List<Results> getAllResults();

    @Insert(onConflict = IGNORE)
    public abstract void insertRealResults(Results results);



}
