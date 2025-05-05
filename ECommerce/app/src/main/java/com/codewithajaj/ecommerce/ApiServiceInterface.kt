package com.codewithajaj.ecommerce

import com.codewithajaj.ecommerce.ModelsClasses.AddCartQty
import com.codewithajaj.ecommerce.ModelsClasses.AllProductModel
import com.codewithajaj.ecommerce.ModelsClasses.CartModel
import com.codewithajaj.ecommerce.ModelsClasses.CartProductShowModel
import com.codewithajaj.ecommerce.ModelsClasses.CategoryModel
import com.codewithajaj.ecommerce.ModelsClasses.ChangePasswordModel
import com.codewithajaj.ecommerce.ModelsClasses.LoginModel
import com.codewithajaj.ecommerce.ModelsClasses.OrderModel
import com.codewithajaj.ecommerce.ModelsClasses.ParticularProductModel
import com.codewithajaj.ecommerce.ModelsClasses.PlaceOrderModel
import com.codewithajaj.ecommerce.ModelsClasses.RegisterModel
import com.codewithajaj.ecommerce.ModelsClasses.SearchProductModel
import com.codewithajaj.ecommerce.ModelsClasses.SentEmailModel
import com.codewithajaj.ecommerce.ModelsClasses.SliderImageModel
import com.codewithajaj.ecommerce.ModelsClasses.SpecificProductModel
import com.codewithajaj.ecommerce.ModelsClasses.SubCategoryModel
import com.codewithajaj.ecommerce.ModelsClasses.VerifyOtpModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


interface ApiServiceInterface {

//    @POST("registration_api.php")
//    fun registerUser(@Body registerRequest: RegisterResponse) : Response<RegisterResponse>

    @FormUrlEncoded
    @POST("registration_api.php")
    fun registerUser(
        @Field("method") method: String,
        @Field("user_name") userName: String,
        @Field("user_email") userEmail: String,
        @Field("user_phone") userPhone: String,
        @Field("user_dob") userDob: String,
        @Field("user_password") userPassword: String,
    ): Call<RegisterModel>

    @FormUrlEncoded
    @POST("login_api.php")
    fun loginUser(
        @Field("method") method: String,
        @Field("user_email") userEmail: String,
        @Field("user_password") userPassword: String
    ): Call<LoginModel>


    @FormUrlEncoded
    @POST("fetch_user_api.php")
    fun fetchImage(
        @Field("method") method: String,
        @Field("user_id") userId: String
    ): Call<FetchImageModel>

    @Multipart
    @POST("update_profiles_api.php")
    fun updateProfile(
        @Part("method") method: RequestBody,
        @Part("user_id") userId: RequestBody,
        @Part userImage: MultipartBody.Part?,
        @Part("user_name") userName: RequestBody,
        @Part("user_email") userEmail: RequestBody,
        @Part("user_phone") userPhone: RequestBody,
        @Part("user_dob") userDob: RequestBody

    ): Call<UPdateModel>

    // for save shipping address
    @FormUrlEncoded
    @POST("add_shipping_address_api.php")
    fun SaveShippingaddress(
        @Field("method") method: String,
        @Field("user_id") userId: String,
        @Field("recipient_name") fullName: String,
        @Field("phone_number") phoneNumber: String,
        @Field("postal_code") pincode: String,
        @Field("state") state: String,
        @Field("city") city: String,
        @Field("address_line1") houseNo: String,
        @Field("country") country: String,
        @Field("address_line2") areaColony: String
    ): Call<ShippingAddressResponse>


    @FormUrlEncoded
    @POST("get_shipping_address_api.php")
    fun getshippingaddress(
        @Field("method") method: String,
        // @Field("get_user_shipping_address") method: String,
        @Field("user_id") userId: String
    ): Call<GetAddressResponse>

    @FormUrlEncoded
    @POST("update_&_delete_shipping_address_api.php")
    fun deleteaddress(
        @Field("method") method: String,
        @Field("shipping_address_id") shippingAddressId: String,
        @Field("user_id") userId: String,

    ): Call<DeletingAddressResponseModel>


    @FormUrlEncoded
    @POST("update_&_delete_shipping_address_api.php")
    fun updateShippingAddress(
        @Field("method") method: String,
        @Field("shipping_address_id") shippingAddressId: String,
        @Field("user_id") userId: String,
        @Field("recipient_name") fullname : String,
        @Field("address_line1") address1: String,
        @Field("address_line2") address2: String,
        @Field("city") city : String,
        @Field("state") state : String,
        @Field("postal_code") pincode : String,
        @Field("country") country : String,
        @Field("phone_number") phonenumber : String,

    ): Call<UpdateShippingAddressModel>

    @FormUrlEncoded
    @POST("get_all_category_api.php")
    fun getCategory(
        @Field("method") method: String,
    ): Call<CategoryModel>

    @FormUrlEncoded
    @POST("get_subcategory_api.php")
    fun getSubCategory(
        @Field("method") method : String,
        @Field("category_id") categoryId: String
    ): Call<SubCategoryModel>

    @FormUrlEncoded
    @POST("get_product_api.php")
    fun getAllProduct(
        @Field("method") method: String,
    ) : Call<AllProductModel>

    @FormUrlEncoded
    @POST("get_specify_product_api.php")
    fun getSpecificProduct(
        @Field("method") method : String,
        @Field("subcategory_id") subCategoryId : String,
    ) : Call<SpecificProductModel>

    @FormUrlEncoded
    @POST("get_particular_product_api.php")
    fun getParticularProduct(
        @Field("method") method : String,
        @Field("product_id") productId : String
    ) : Call<ParticularProductModel>

    @FormUrlEncoded
    @POST("cart_api.php")
    fun getAddToCartProduct(
        @Field("method") method : String,
        @Field("user_id") userID : String,
        @Field("product_id") productID : String
    ) : Call<CartModel>

    @FormUrlEncoded
    @POST("show_cart_api.php")
    fun getAddToCartProductShow(
        @Field("method") method: String,
        @Field("user_id") userID : String
    ) : Call<CartProductShowModel>

    @FormUrlEncoded
    @POST("cart_api.php")
    fun addCartProductQty(
        @Field("method") method: String,
        @Field("user_id") userID: String,
        @Field("product_id") productID: String
    ) : Call<AddCartQty>

    @FormUrlEncoded
    @POST("search_api.php")
    fun searchProduct(
        @Field("search_keyword") search : String
    ) : Call<SearchProductModel>

    @FormUrlEncoded
    @POST("otp_email.php")
    fun sentOtp(
        @Field("method") method: String,
        @Field("email") email : String
    ) : Call<SentEmailModel>

    @FormUrlEncoded
    @POST("verify_otp.php")
    fun verifyOtp(
        @Field("method") method : String,
        @Field("otp") otp : String,
        @Field("user_email") email : String
    ) :Call<VerifyOtpModel>

    @FormUrlEncoded
    @POST("new_password_api.php")
    fun changePassword(
        @Field("method") method: String,
        @Field("user_email") userEmail: String,
        @Field("user_password") userPassword: String
    ) : Call<ChangePasswordModel>

    @FormUrlEncoded
    @POST("placeorder_api.php")
    fun placeOrder(
        @Field("placeOrder") placeOrder : String,
        @Field("user_id") user_id: String,
        @Field("shipping_address_id") shipping_address_id: String
    ) : Call<PlaceOrderModel>

    @FormUrlEncoded
    @POST("sliding_card_api.php")
    fun sliderImage(
        @Field("method") method: String,
    ): Call<SliderImageModel>



    // order history api
    @FormUrlEncoded
    @POST("fetch_order_api.php")
    fun getOrderHistory(
        @Field("method") method: String,
        @Field("user_id") userID: String
    ) : Call<OrderModel>
}