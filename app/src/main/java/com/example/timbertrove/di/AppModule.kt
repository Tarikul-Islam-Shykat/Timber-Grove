package com.example.timbertrove.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.timbertrove.utils.Constants
import com.example.timbertrove.utils.FirebaseCommon
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideFireBaseAuth() : FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFireStoreDatabase() : FirebaseFirestore  = Firebase.firestore

    @Provides
    fun provideIntroductionSharedPref(application: Application): SharedPreferences
        = application.getSharedPreferences(Constants.INTRODUCTION_SHAREDPREF, Context.MODE_PRIVATE)


    @Provides
    @Singleton
    fun provideFirebaseCommon( // used for cart add and update
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore
    ) : FirebaseCommon = FirebaseCommon(firestore, firebaseAuth)

    @Provides
    @Singleton
    fun provideStorage() = FirebaseStorage.getInstance().reference



}