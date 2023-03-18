package com.ss.instagramdownloader.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ss.instagramdownloader.models.User


@Database(entities = [User::class], version = 1 )
abstract class AppDB : RoomDatabase(){

    abstract fun getAppDB():AppDAO
}