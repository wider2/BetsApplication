package march.marchappl.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

@Entity
public class Session {
 
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "visit_id")
    private int visit_id;

    @ColumnInfo(name = "last_visit")
    private long lastVisit;


    public int getVisit_id() {
        return visit_id;
    }

    public void setVisit_id(int visit_id) {
        this.visit_id = visit_id;
    }

    public long getLastVisit() {
        return lastVisit;
    }

    public void setLastVisit(long lastVisit) {
        this.lastVisit = lastVisit;
    }

}