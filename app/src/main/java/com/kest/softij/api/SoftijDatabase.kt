package com.kest.softij.api

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kest.softij.api.model.CartItem
import com.kest.softij.api.model.User

@Database(entities = [User::class,CartItem::class],version = 1)
abstract class SoftijDatabase:RoomDatabase() {
    abstract fun softijDao():SoftijDao
}