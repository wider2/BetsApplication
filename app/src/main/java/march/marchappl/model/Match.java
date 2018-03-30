package march.marchappl.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import static butterknife.internal.Utils.arrayOf;


@Entity(
        indices = @Index(value = {"team_id1", "team_id2"}, unique = true),
        foreignKeys = {
        @ForeignKey(entity = Team.class,
                parentColumns = "team_id",
                childColumns = "team_id1"),

        @ForeignKey(entity = Team.class,
                parentColumns = "team_id",
                childColumns = "team_id2")
        })
public class Match {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;

    @ColumnInfo(name = "team_id1")
    private int teamId1;

    @ColumnInfo(name = "team_id2")
    private int teamId2;


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

}