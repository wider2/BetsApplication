package march.marchappl.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Matches {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;

    @ColumnInfo(name = "team_id1")
    private int teamId1;

    @ColumnInfo(name = "team_id2")
    private int teamId2;

    @ColumnInfo(name = "team_name1")
    private String teamName1;

    @ColumnInfo(name = "team_name2")
    private String teamName2;



    public Matches (String teamName1, int teamId1, String teamName2, int teamId2) {
        this.teamName1 = teamName1;
        this.teamId1 = teamId1;
        this.teamName2 = teamName2;
        this.teamId2 = teamId2;
    }

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

    public String getTeamName1() {
        return teamName1;
    }

    public void setTeamName1(String teamName1) {
        this.teamName1 = teamName1;
    }

    public String getTeamName2() {
        return teamName2;
    }

    public void setTeamName2(String teamName2) {
        this.teamName2 = teamName2;
    }

}