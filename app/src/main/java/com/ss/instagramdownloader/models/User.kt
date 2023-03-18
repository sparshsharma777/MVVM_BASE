package com.ss.instagramdownloader.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
 data class User( @PrimaryKey(autoGenerate = true)
                   var id: Int = 0,val name: String) {


}