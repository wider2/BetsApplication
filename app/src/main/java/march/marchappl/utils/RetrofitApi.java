package march.marchappl.utils;

import java.util.List;

import io.reactivex.Observable;
import march.marchappl.model.DownloadMatches;
import march.marchappl.model.JSONResponse;
import march.marchappl.model.JSONResults;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitApi {

    @GET("/v2/5aba3513350000500073a4f8")
    Observable<JSONResponse> getMatches(@Query("nocache") int key);

    @GET("/v2/5aba35503500005f0073a4fb")
    Observable<JSONResults> getResults(@Query("nocache") int key);

}