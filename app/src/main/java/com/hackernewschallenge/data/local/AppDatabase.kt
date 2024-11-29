package com.hackernewschallenge.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hackernewschallenge.data.local.entities.ArticlesEntity

@Database(entities = [ArticlesEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun articleDao(): HitsDao
}