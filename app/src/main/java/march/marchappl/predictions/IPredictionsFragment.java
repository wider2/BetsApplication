package march.marchappl.predictions;

import java.util.List;

import march.marchappl.model.Matches;
import march.marchappl.model.Prediction;

public interface IPredictionsFragment {

    void refreshResult(List<Matches> list, List<Prediction> predictions);

    void showException(Exception ex);

    void showErrorServerResponse(Throwable response);

    void refreshPrediction(List<Prediction> predictions);
}