package guru.qa.niffler.api;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface SpendApi {
  @POST("internal/spends/add")
  @Nonnull
  Call<SpendJson> createSpend(@Body SpendJson spend);

  @PATCH("internal/spends/edit")
  @Nonnull
  Call<SpendJson> editSpend(@Body SpendJson spend);

  @GET("internal/spends/{id}")
  @Nonnull
  Call<SpendJson> getSpend(@Path("id") String id);

  @GET("internal/spends/all")
  @Nonnull
  Call<List<SpendJson>> allSpends(@Query("username") String username,
                                  @Query("filterCurrency") CurrencyValues filterCurrency,
                                  @Query("from") String from,
                                  @Query("to") String to);

  @DELETE("internal/spends/remove")
  @Nonnull
  Call<Void> removeSpends(@Query("username") String username, @Query("ids") List<String> ids);

  @POST("internal/categories/add")
  @Nonnull
  Call<CategoryJson> addCategory(@Body CategoryJson category);

  @PATCH("internal/categories/update")
  @Nonnull
  Call<CategoryJson> updateCategory(@Body CategoryJson category);

  @GET("internal/categories/all")
  @Nonnull
  Call<List<CategoryJson>> allCategories(@Query("username") String username);
}
