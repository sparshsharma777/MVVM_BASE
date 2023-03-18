package com.ss.instagramdownloader.db

import androidx.room.Dao
import androidx.room.Insert
import com.ss.instagramdownloader.models.User


@Dao
interface AppDAO {
    @Insert
    suspend fun addProducts(images:List<User>)

}