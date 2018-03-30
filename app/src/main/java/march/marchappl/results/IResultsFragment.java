package march.marchappl.results;

import java.util.List;

import march.marchappl.model.Matches;
import march.marchappl.model.Prediction;
import march.marchappl.model.Results;

public interface IResultsFragment {

    void refreshResult(List<Matches> list, List<Prediction> predictions, List<Results> results);

    void showException(Exception ex);

    void showErrorServerResponse(Throwable response);

    void refreshPrediction();
}