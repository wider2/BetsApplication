package march.marchappl;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import march.marchappl.dao.BetsDao;
import march.marchappl.model.Match;
import march.marchappl.model.Prediction;
import march.marchappl.model.Results;
import march.marchappl.model.Session;
import march.marchappl.model.Team;


@Database(entities = {Match.class, Team.class, Session.class, Prediction.class, Results.class}, version = 1, exportSchema = false)
public abstract class BetsDatabase extends RoomDatabase {

    private static volatile BetsDatabase INSTANCE;

    public abstract BetsDao betsDao();

    public static BetsDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (BetsDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            BetsDatabase.class, "DatabaseSweepstakes.db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

