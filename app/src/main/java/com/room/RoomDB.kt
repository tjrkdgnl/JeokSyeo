package com.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(entities = [Entity::class],version = 1)
abstract class RoomDB:RoomDatabase() {
    abstract fun roomDao():Dao

    companion object{
        var instance:RoomDB? = null

        const val DB_NAME = "USER_ROOM"

        fun getInstance(context: Context):RoomDB {
            return instance ?: buildDataBase(context)
        }

        private fun buildDataBase(context: Context):RoomDB{
            return Room.databaseBuilder(context,RoomDB::class.java, DB_NAME)
                .fallbackToDestructiveMigration()
                .addCallback(object :RoomDatabase.Callback(){
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                    }
                }).build()
        }
    }
}