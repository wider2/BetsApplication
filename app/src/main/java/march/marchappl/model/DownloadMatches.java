package march.marchappl.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DownloadMatches {

    @SerializedName("team1")
    @Expose
    private String team1;

    @SerializedName("team2")
    @Expose
    private String team2;


    public DownloadMatches(String team1, String team2) {
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

}