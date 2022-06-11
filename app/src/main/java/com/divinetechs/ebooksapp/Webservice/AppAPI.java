package com.divinetechs.ebooksapp.Webservice;

import com.divinetechs.ebooksapp.Model.AuthorModel.AuthorModel;
import com.divinetechs.ebooksapp.Model.BannerModel.BannerModel;
import com.divinetechs.ebooksapp.Model.BookModel.BookModel;
import com.divinetechs.ebooksapp.Model.BookmarkModel.BookmarkModel;
import com.divinetechs.ebooksapp.Model.CategoryModel.CategoryModel;
import com.divinetechs.ebooksapp.Model.CommentModel.CommentModel;
import com.divinetechs.ebooksapp.Model.DownloadModel.DownloadModel;
import com.divinetechs.ebooksapp.Model.GeneralSettings.GeneralSettings;
import com.divinetechs.ebooksapp.Model.LoginRegister.LoginRegiModel;
import com.divinetechs.ebooksapp.Model.MagazineModel.MagazineModel;
import com.divinetechs.ebooksapp.Model.NotificationModel.NotificationModel;
import com.divinetechs.ebooksapp.Model.PackageModel.PackageModel;
import com.divinetechs.ebooksapp.Model.PayTmModel.PayTmModel;
import com.divinetechs.ebooksapp.Model.PointSystemModel.PointSystemModel;
import com.divinetechs.ebooksapp.Model.ProfileModel.ProfileModel;
import com.divinetechs.ebooksapp.Model.ReadDowncntModel.ReadDowncntModel;
import com.divinetechs.ebooksapp.Model.SuccessModel.SuccessModel;
import com.divinetechs.ebooksapp.Model.TransactionModel.TransactionModel;
import com.divinetechs.ebooksapp.Model.VoucherModel.VoucherModel;
import com.divinetechs.ebooksapp.Model.WalletHistoryModel.WalletHistoryModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface AppAPI {

    @GET("general_setting")
    Call<GeneralSettings> general_settings();

    @FormUrlEncoded
    @POST("checkStatus")
    Call<SuccessModel> checkStatus(@Field("purchase_code") String purchase_code,
                                   @Field("package_name") String package_name);

    @FormUrlEncoded
    @POST("login")
    Call<LoginRegiModel> login(@Field("email") String email_id,
                               @Field("password") String password,
                               @Field("type") String type);

    /*Type for Login : 1-normal, 2-facebook, 3-mobile otp, 4-gmail*/
    @Multipart
    @POST("login")
    Call<LoginRegiModel> login(@Part("fullname") RequestBody fullname,
                               @Part("last_name") RequestBody last_name,
                               @Part("email") RequestBody email,
                               @Part("type") RequestBody type,
                               @Part("mobile_number") RequestBody mobile_number,
                               @Part("password") RequestBody password,
                               @Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("loginwithotp")
    Call<LoginRegiModel> loginwithotp(@Field("mobile") String mobile);

    @FormUrlEncoded
    @POST("registration")
    Call<SuccessModel> Registration(@Field("fullname") String full_name,
                                    @Field("email") String email_id,
                                    @Field("password") String password,
                                    @Field("mobile") String phone);

    @FormUrlEncoded
    @POST("forgotpassword")
    Call<SuccessModel> forgotpassword(@Field("email") String email_id);

    @FormUrlEncoded
    @POST("get_notification")
    Call<NotificationModel> get_notification(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("read_notification")
    Call<SuccessModel> read_notification(@Field("user_id") String user_id,
                                         @Field("notification_id") String notification_id);

    @FormUrlEncoded
    @POST("profile")
    Call<ProfileModel> profile(@Field("user_id") String user_id);

    @Multipart
    @POST("update_profile")
    Call<SuccessModel> add_profile_img(@Part("user_id") RequestBody user_id,
                                       @Part("fullname") RequestBody fullname,
                                       @Part("email") RequestBody email,
                                       @Part("password") RequestBody password,
                                       @Part("mobile") RequestBody mobile,
                                       @Part MultipartBody.Part profile_img);

    @FormUrlEncoded
    @POST("update_profile")
    Call<SuccessModel> updateprofile(@Field("user_id") String user_id,
                                     @Field("fullname") String fullname,
                                     @Field("email") String email,
                                     @Field("password") String password,
                                     @Field("mobile") String mobile);

    @FormUrlEncoded
    @POST("update_profile")
    Call<SuccessModel> updateMissingData(@Field("user_id") String user_id,
                                         @Field("fullname") String fullname,
                                         @Field("email") String email,
                                         @Field("mobile") String mobile);

    @GET("get_ads_banner")
    Call<BannerModel> get_ads_banner();

    @FormUrlEncoded
    @POST("categorylist")
    Call<CategoryModel> categorylist(@Field("page_no") String page_no);

    @FormUrlEncoded
    @POST("book_by_category")
    Call<BookModel> books_by_category(@Field("category_id") String cat_id,
                                      @Field("page_no") String page_no);

    @FormUrlEncoded
    @POST("newarriaval")
    Call<BookModel> newarriaval(@Field("page_no") String page_no);

    @FormUrlEncoded
    @POST("alsolike")
    Call<BookModel> alsolike(@Field("page_no") String page_no);

    @FormUrlEncoded
    @POST("popularbooklist")
    Call<BookModel> popularbooklist(@Field("page_no") String page_no);

    @FormUrlEncoded
    @POST("autherlist")
    Call<AuthorModel> autherlist(@Field("page_no") String page_no);

    @FormUrlEncoded
    @POST("booksearch")
    Call<BookModel> booksearch(@Field("name") String name,
                               @Field("page_no") String page_no);

    @FormUrlEncoded
    @POST("book_by_author")
    Call<BookModel> books_by_author(@Field("author_id") String a_id,
                                    @Field("page_no") String page_no);

    @FormUrlEncoded
    @POST("bookdetails")
    Call<BookModel> bookdetails(@Field("book_id") String b_id,
                                @Field("user_id") String user_id);

    //============= Magazine START ==============//

    @FormUrlEncoded
    @POST("popular_magazine")
    Call<MagazineModel> popular_magazine(@Field("page_no") String page_no);

    @FormUrlEncoded
    @POST("top_download_magazine")
    Call<MagazineModel> top_download_magazine(@Field("page_no") String page_no);

    @FormUrlEncoded
    @POST("top_magazine")
    Call<MagazineModel> top_magazine(@Field("page_no") String page_no);

    @FormUrlEncoded
    @POST("magazinesearch")
    Call<MagazineModel> magazinesearch(@Field("name") String name,
                                       @Field("page_no") String page_no);

    @FormUrlEncoded
    @POST("magazine_by_category")
    Call<MagazineModel> magazine_by_category(@Field("category_id") String category_id,
                                             @Field("page_no") String page_no);

    @FormUrlEncoded
    @POST("magazinedetails")
    Call<MagazineModel> magazinedetails(@Field("magazine_id") String magazine_id,
                                        @Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("add_comment")
    Call<SuccessModel> add_magazine_comment(@Field("magazine_id") String magazine_id,
                                            @Field("user_id") String user_id,
                                            @Field("comment") String comment);

    @FormUrlEncoded
    @POST("view_comment")
    Call<CommentModel> view_magazine_comment(@Field("magazine_id") String magazine_id,
                                             @Field("page_no") String page_no);

    @FormUrlEncoded
    @POST("add_bookmark")
    Call<SuccessModel> add_magazine_bookmark(@Field("user_id") String user_id,
                                             @Field("magazine_id") String magazine_id);

    @FormUrlEncoded
    @POST("checkbookmark")
    Call<SuccessModel> check_magazine_bookmark(@Field("user_id") String user_id,
                                               @Field("magazine_id") String magazine_id);

    @FormUrlEncoded
    @POST("add_magazine_rating")
    Call<SuccessModel> add_magazine_rating(@Field("user_id") String user_id,
                                           @Field("magazine_id") String magazine_id,
                                           @Field("rating") String rating);

    @FormUrlEncoded
    @POST("add_download")
    Call<SuccessModel> add_magazine_download(@Field("user_id") String user_id,
                                             @Field("magazine_id") String magazine_id);

    @FormUrlEncoded
    @POST("alldownload")
    Call<DownloadModel> alldownload(@Field("user_id") String user_id,
                                    @Field("type") String type,
                                    @Field("page_no") String page_no);

    @FormUrlEncoded
    @POST("add_transaction")
    Call<SuccessModel> add_magazine_purchase(@Field("magazine_id") String magazine_id,
                                             @Field("user_id") String user_id,
                                             @Field("amount") String amount,
                                             @Field("currency_code") String currency_code,
                                             @Field("description") String short_description,
                                             @Field("state") String state,
                                             @Field("author_id") String author_id,
                                             @Field("payment_id") String payment_id,
                                             @Field("type") String type,  // 1-payment getway,  2-wallet amount
                                             @Field("wallet_amount") String wallet_amount,
                                             @Field("transcation_amount") String transcation_amount);

    @FormUrlEncoded
    @POST("purchaselist")
    Call<DownloadModel> purchaseMagazineList(@Field("user_id") String user_id,
                                             @Field("type") String type,
                                             @Field("page_no") String page_no);

    //============= Magazine END ==============//

    @FormUrlEncoded
    @POST("add_transaction")
    Call<SuccessModel> add_chapter_transaction(@Field("author_id") String author_id,
                                               @Field("user_id") String user_id,
                                               @Field("amount") String amount,
                                               @Field("book_chapter_id") String book_chapter_id,
                                               @Field("book_id") String book_id);

    @FormUrlEncoded
    @POST("add_transaction")
    Call<SuccessModel> add_purchase(@Field("book_id") String book_id,
                                    @Field("user_id") String user_id,
                                    @Field("amount") String amount,
                                    @Field("currency_code") String currency_code,
                                    @Field("description") String short_description,
                                    @Field("state") String state,
                                    @Field("author_id") String author_id,
                                    @Field("payment_id") String payment_id,
                                    @Field("type") String type,  // 1-payment getway,  2-wallet amount
                                    @Field("wallet_amount") String wallet_amount,
                                    @Field("transcation_amount") String transcation_amount);

    @FormUrlEncoded
    @POST("purchaselist")
    Call<DownloadModel> purchaseBookList(@Field("user_id") String user_id,
                                         @Field("type") String type,
                                         @Field("page_no") String page_no);

    @FormUrlEncoded
    @POST("get_transaction")
    Call<TransactionModel> get_transaction(@Field("user_id") String user_id,
                                           @Field("page_no") String page_no);

    @FormUrlEncoded
    @POST("related_item")
    Call<BookModel> related_item(@Field("category_id") String fcat_id,
                                 @Field("page_no") String page_no);

    @FormUrlEncoded
    @POST("add_download")
    Call<SuccessModel> add_download(@Field("user_id") String user_id,
                                    @Field("book_id") String b_id);

    @FormUrlEncoded
    @POST("add_continue_read")
    Call<SuccessModel> add_continue_read(@Field("user_id") String user_id,
                                         @Field("book_id") String book_id);

    @FormUrlEncoded
    @POST("continue_read")
    Call<BookModel> continue_read(@Field("user_id") String user_id,
                                  @Field("page_no") String page_no);

    @FormUrlEncoded
    @POST("add_comment")
    Call<SuccessModel> add_comment(@Field("book_id") String book_id,
                                   @Field("user_id") String user_id,
                                   @Field("comment") String comment);

    @FormUrlEncoded
    @POST("view_comment")
    Call<CommentModel> view_comment(@Field("book_id") String book_id,
                                    @Field("page_no") String page_no);

    @FormUrlEncoded
    @POST("add_bookmark")
    Call<SuccessModel> add_bookmark(@Field("user_id") String user_id,
                                    @Field("book_id") String book_id);

    @FormUrlEncoded
    @POST("all_bookmark")
    Call<BookmarkModel> allBookmark(@Field("user_id") String user_id,
                                    @Field("type") String type,
                                    @Field("page_no") String page_no);

    @FormUrlEncoded
    @POST("checkbookmark")
    Call<SuccessModel> checkbookmark(@Field("user_id") String user_id,
                                     @Field("book_id") String book_id);

    @FormUrlEncoded
    @POST("add_rating")
    Call<SuccessModel> give_rating(@Field("user_id") String user_id,
                                   @Field("book_id") String book_id,
                                   @Field("rating") String rating);

    @FormUrlEncoded
    @POST("readcount_by_author")
    Call<ReadDowncntModel> readcnt_by_author(@Field("author_id") String a_id);

    @FormUrlEncoded
    @POST("free_paid_booklist")
    Call<BookModel> free_paid_booklist(@Field("is_paid") String is_paid,
                                       @Field("page_no") String page_no);

    @GET("get_package")
    Call<PackageModel> get_package();

    @FormUrlEncoded
    @POST("get_wallet_transaction")
    Call<WalletHistoryModel> get_wallet_transaction(@Field("user_id") String user_id,
                                                    @Field("page_no") String page_no);

    @FormUrlEncoded
    @POST("add_package_transaction")
    Call<SuccessModel> add_package_transaction(@Field("user_id") String user_id,
                                               @Field("amount") String amount,
                                               @Field("package_id") String package_id,
                                               @Field("payment_id") String payment_id,
                                               @Field("state") String state);

    @FormUrlEncoded
    @POST("add_voucher")
    Call<SuccessModel> add_voucher(@Field("user_id") String user_id,
                                   @Field("title") String title,
                                   @Field("points") String points);

    @FormUrlEncoded
    @POST("list_voucher")
    Call<VoucherModel> list_voucher(@Field("user_id") String user_id);

    @GET("earn_point")
    Call<PointSystemModel> earn_point();

    @FormUrlEncoded
    @POST("getPaymentToken")
    Call<PayTmModel> getPaymentToken(
            @Field("MID") String mId,
            @Field("order_id") String orderId,
            @Field("CUST_ID") String custId,
            @Field("CHANNEL_ID") String channelId,
            @Field("TXN_AMOUNT") String txnAmount,
            @Field("WEBSITE") String website,
            @Field("CALLBACK_URL") String callbackUrl,
            @Field("INDUSTRY_TYPE_ID") String industryTypeId);

}
