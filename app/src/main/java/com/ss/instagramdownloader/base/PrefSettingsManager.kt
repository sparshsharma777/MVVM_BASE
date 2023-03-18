package com.ss.instagramdownloader.base

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Singleton


@Singleton
class PrefSettingsManager(@ApplicationContext val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("SETTINGS_PREF")
        const val SETTINGS_PREF = "user_prefs"
        val USER_AGE_KEY = intPreferencesKey("USER_AGE")
        val USER_NAME_KEY = stringPreferencesKey("USER_NAME")
        val IS_LOGIN = booleanPreferencesKey("IS_LOGIN")
        val KEY_TOKEN = stringPreferencesKey("ACCESS_TOKEN")
        val IS_FIRST_LOGIN = booleanPreferencesKey("IS_FIRST_LOGIN")
        val LAST_PROMPT_STATUS = booleanPreferencesKey("LAST_PROMPT_STATUS")
        val DEVICE_ID = stringPreferencesKey("DEVICE_ID")
        val PURCHASES = stringPreferencesKey("PURCHASES")
    }
    /*
  One of the primary benefits of DataStore is the asynchronous API, but it may not always be feasible to change your surrounding code to be asynchronous.
  This might be the case if you're working with an existing codebase that uses synchronous disk I/O or if you have a dependency that doesn't provide an asynchronous API.
  Kotlin coroutines provide the runBlocking() coroutine builder to help bridge the gap between synchronous and asynchronous code.
   */

    // to load with asynchronous
    //val exampleData = runBlocking { context.dataStore.data.first() }

    // get the user's age
    val userAgeFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[USER_AGE_KEY] ?: 100
    }

    // get the user's name
    val userNameFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[USER_NAME_KEY] ?: ""
    }
    val tokenFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_TOKEN] ?: ""
    }
    val loginFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_LOGIN] ?: false
    }
    val isFirstLogin: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_FIRST_LOGIN] ?: true
    }
    val lastPromptStatus: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[LAST_PROMPT_STATUS] ?: false
    }

    val purchases: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PURCHASES] ?: ""
    }


    suspend fun storeUserInfo(age: Int, name: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_AGE_KEY] = age
            preferences[USER_NAME_KEY] = name
        }
    }

    suspend fun storeTokenInfo(token: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_TOKEN] = token
            Log.d("TOKEN-S", token)


        }
    }

    suspend fun storeLoginInfo(islogin: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGIN] = islogin
        }
    }

//    suspend fun storeUserInfo(age: Int, name: String, token: String) {
//        context.dataStore.edit { preferences ->
//            preferences[USER_AGE_KEY] = age
//            preferences[USER_NAME_KEY] = name
//        }
//    }

    suspend fun clearPref() {
        context.dataStore.edit { it.clear() }
    }



    suspend fun setIsFirstLogin(isFirstLogin: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_FIRST_LOGIN] = isFirstLogin
        }
    }

    suspend fun saveUserDeviceId(deviceId: String) {
        context.dataStore.edit { preferences ->
            preferences[DEVICE_ID] = deviceId
        }
    }
    suspend fun savePurchases(deviceId: String) {
        context.dataStore.edit { preferences ->
            preferences[PURCHASES] = deviceId
        }
    }

    val deviceIdFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[DEVICE_ID] ?: ""
    }



}