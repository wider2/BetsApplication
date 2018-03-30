package march.marchappl.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DownloadResults {

    @SerializedName("team1")
    @Expose
    private String team1;

    @SerializedName("team2")
    @Expose
    private String team2;

    @SerializedName("team1_points")
    @Expose
    private int team1_points;

    @SerializedName("team2_points")
    @Expose
    private int team2_points;


    public DownloadResults(String team1, String team2) {
        this.team1 = team1;
        this.team2 = team2;
    }

    public String getTeam1() {
        return team1;
    }

    public void setTeam1(String team1) {
        this.team1 = team1;
    }

    public String getTeam2() {
        return team2;
    }

    public void setTeam2(String team2) {
        this.team2 = team2;
    }


    public int getTeam1_points() {
        return team1_points;
    }

    public void setTeam1_points(int team1_points) {
        this.team1_points = team1_points;
    }

    public int getTeam2_points() {
        return team2_points;
    }

    public void setTeam2_points(int team2_points) {
        this.team2_points = team2_points;
    }

}