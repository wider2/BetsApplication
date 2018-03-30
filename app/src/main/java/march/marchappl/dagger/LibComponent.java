package march.marchappl.dagger;

import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Component;
import march.marchappl.MainActivity;
import march.marchappl.dagger.scope.PerActivityScope;
import march.marchappl.predictions.PredictionsFragmentPresenter;

@PerActivityScope
@Component(modules={LibModule.class})
public interface LibComponent {

    LibRepository getLibRepository();

}