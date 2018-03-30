package march.marchappl;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Random;

import march.marchappl.model.Match;
import march.marchappl.model.Matches;
import march.marchappl.model.Prediction;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Instrumentation test, which will execute on an Android device.
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    BetsDatabase db;

    @Before
    public void setup() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        db = BetsDatabase.getInstance(appContext);
    }

    @After
    public void complete() {
        if (db != null) {
            if (db.isOpen()) db.close();
        }
    }

    @Test
    public void checkDatabase() throws Exception {

        List<Matches> list = db.betsDao().loadAllMatches();

        assertNotNull(list);
        assertEquals(8, list.size());
    }

    @Test
    public void addToDatabase() throws Exception {

        Match match = new Match();
        match.setTeamId1(1);
        match.setTeamId2(16);
        db.betsDao().insertMatch(match);

        Match result = db.betsDao().loadMatchByIds(1, 16);
        assertThat(true, is(result.getTeamId2() == 16));
    }

    @Test
    public void updateToDatabase() throws Exception {
        int teamId1 = 1, teamId2 = 16;

        Prediction prediction = new Prediction();
        prediction.setTeamId1(teamId1);
        prediction.setTeamId2(teamId2);
        prediction.setScore1(3);
        prediction.setScore2(1);
        db.betsDao().insertPrediction(prediction);

        Prediction result = db.betsDao().loadPredictionById(teamId1, teamId2);
        assertNotNull(result);

        db.betsDao().updatePrediction(teamId1, teamId2, 10, 0);
        Prediction score = db.betsDao().loadPredictionById(teamId1, teamId2);
        assertNotEquals(1, score.getScore2());
    }

    @Test
    public void hasInternet() throws Exception {
        boolean isConnected = ConnectivityReceiver.isConnected();
        assertTrue(isConnected);
    }

}
