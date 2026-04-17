package com.gbc.dormio_mobile_app.network

import android.content.Context
import com.gbc.dormio_mobile_app.network.ApiServices.AuthApiService
import com.gbc.dormio_mobile_app.network.ApiServices.ChoresApiService
import com.gbc.dormio_mobile_app.network.ApiServices.MaintenanceApiService
import com.gbc.dormio_mobile_app.network.ApiServices.MealPlanApiService
import com.gbc.dormio_mobile_app.network.ApiServices.NotificationApiService
import com.gbc.dormio_mobile_app.network.ApiServices.ProfileApiService
import com.gbc.dormio_mobile_app.network.ApiServices.BillApiService
import com.gbc.dormio_mobile_app.network.ApiServices.ExpenseApiService
import com.gbc.dormio_mobile_app.network.ApiServices.ScheduleApiService
import com.gbc.dormio_mobile_app.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        @ApplicationContext context: Context
    ): Interceptor {
        return Interceptor { chain ->
            val token = TokenManager.getToken(context)
            val requestBuilder = chain.request().newBuilder()
            if (token != null && !TokenManager.isTokenExpired(context)) {
                requestBuilder.header("Authorization", "Bearer $token")
            }
            chain.proceed(requestBuilder.build())
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: Interceptor,
        @ApplicationContext context: Context
    ): OkHttpClient {
        val authResponseInterceptor = Interceptor { chain ->
            val response = chain.proceed(chain.request())
            if (response.code == 401 || response.code == 403) {
                TokenManager.notifySessionExpired(context)
            }
            response
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .addInterceptor(authResponseInterceptor)
            .connectTimeout(Constants.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(Constants.READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(Constants.WRITE_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // AuthApiService provider
    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    // MaintenanceApiService provider
    @Provides
    @Singleton
    fun provideMaintenanceApiService(retrofit: Retrofit): MaintenanceApiService {
        return retrofit.create(MaintenanceApiService::class.java)
    }

    // FcmTokenManager provider
    @Provides
    @Singleton
    fun provideFcmTokenManager(authApiService: AuthApiService): FcmTokenManager {
        return FcmTokenManager(authApiService)
    }

    //meal plan api service provider
    @Provides
    @Singleton
    fun provideMealPlanApiService(retrofit: Retrofit): MealPlanApiService {
        return retrofit.create(MealPlanApiService::class.java)
    }

    //profile api service provider
    @Provides
    @Singleton
    fun provideProfileApiService(retrofit: Retrofit): ProfileApiService {
        return retrofit.create(ProfileApiService::class.java)
    }

    //chores api service provider
    @Provides
    @Singleton
    fun provideChoresApiService(retrofit: Retrofit): ChoresApiService {
        return retrofit.create(ChoresApiService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideScheduleApiService(retrofit: Retrofit): ScheduleApiService {
        return retrofit.create(ScheduleApiService::class.java)
    }

    //bill api service provider
    @Provides
    @Singleton
    fun provideBillApiService(retrofit: Retrofit): BillApiService {
        return retrofit.create(BillApiService::class.java)
    }

    //expense api service provider
    @Provides
    @Singleton
    fun provideExpenseApiService(retrofit: Retrofit): ExpenseApiService {
        return retrofit.create(ExpenseApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideNotificationApiService(retrofit: Retrofit): NotificationApiService {
        return retrofit.create(NotificationApiService::class.java)
    }

}
