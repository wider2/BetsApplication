package march.marchappl.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import static butterknife.internal.Utils.arrayOf;

@Entity
public class Results {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;

    @ColumnInfo(name = "team_id1")
    private int teamId1;

    @ColumnInfo(name = "team_id2")
    private int teamId2;

    @ColumnInfo(name = "team_points1")
    private int team_points1;

    @ColumnInfo(name = "team_points2")
    private int team_points2;


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


    public int getTeam_points1() {
        return team_points1;
    }

    public void setTeam_points1(int team_points1) {
        this.team_points1 = team_points1;
    }

    public int getTeam_points2() {
        return team_points2;
    }

    public void setTeam_points2(int team_points2) {
        this.team_points2 = team_points2;
    }

}