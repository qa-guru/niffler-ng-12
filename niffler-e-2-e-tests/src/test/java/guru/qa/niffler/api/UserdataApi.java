package guru.qa.niffler.api;

import guru.qa.niffler.model.UserJson;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface UserdataApi {

  @GET("internal/users/current")
  @Nonnull
  Call<UserJson> currentUser(@Query("username") String username);

  @POST("internal/users/update")
  @Nonnull
  Call<UserJson> updateUserInfo(@Body UserJson user);

  @GET("internal/users/all")
  @Nonnull
  Call<List<UserJson>> allUsers(@Query("username") String username,
                                @Query("searchQuery") @Nullable String searchQuery);

  @GET("internal/friends/all")
  @Nonnull
  Call<List<UserJson>> friends(@Query("username") String username,
                               @Query("searchQuery") @Nullable String searchQuery);

  @DELETE("internal/friends/remove")
  @Nonnull
  Call<Void> removeFriend(@Query("username") String username,
                          @Query("targetUsername") String targetUsername);

  @GET("internal/invitations/income")
  @Nonnull
  Call<List<UserJson>> incomeInvitations(@Query("username") String username,
                                         @Query("searchQuery") @Nullable String searchQuery);

  @GET("internal/invitations/outcome")
  @Nonnull
  Call<List<UserJson>> outcomeInvitations(@Query("username") String username,
                                          @Query("searchQuery") @Nullable String searchQuery);

  @POST("internal/invitations/accept")
  @Nonnull
  Call<UserJson> acceptInvitation(@Query("username") String username,
                                  @Query("targetUsername") String targetUsername);

  @POST("internal/invitations/decline")
  @Nonnull
  Call<UserJson> declineInvitation(@Query("username") String username,
                                   @Query("targetUsername") String targetUsername);

  @POST("internal/invitations/send")
  @Nonnull
  Call<UserJson> sendInvitation(@Query("username") String username,
                                @Query("targetUsername") String targetUsername);
}
