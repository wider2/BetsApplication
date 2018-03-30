package march.marchappl.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import static butterknife.internal.Utils.arrayOf;


@Entity
public class Prediction {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;

    @ColumnInfo(name = "team_id1")
    private int teamId1;

    @ColumnInfo(name = "team_id2")
    private int teamId2;

    @ColumnInfo(name = "score1")
    private int score1;

    @ColumnInfo(name = "score2")
    private int score2;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTeamId1() {
        return teamId1;
    }

    public void setTeamId1(int teamId1) {
        this.teamId1 = teamId1;
    }

    public int getTeamId2() {
        return teamId2;
    }

    public void setTeamId2(int teamId2) {
        this.teamId2 = teamId2;
    }


    public int getScore1() {
        return score1;
    }

    public void setScore1(int score1) {
        this.score1 = score1;
    }

    public int getScore2() {
        return score2;
    }

    public void setScore2(int score2) {
        this.score2 = score2;
    }

}