package march.marchappl.dagger;

import javax.inject.Singleton;

import dagger.Component;
import march.marchappl.MainActivity;
import march.marchappl.predictions.PredictionsFragment;
import march.marchappl.results.ResultsFragment;

@Singleton
@Component(modules={AppModule.class})
public interface AppComponent {

    void inject(MainActivity activity);
    void inject(PredictionsFragment predictionsFragment);
    void inject(ResultsFragment resultsFragment);

}