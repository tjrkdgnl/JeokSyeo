package com.room

import androidx.room.*
import androidx.room.Dao
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface Dao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertToken(entity: Entity): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExpire(entity: Entity):Completable

    @Query("SELECT accessToken from UserEntity")
    fun getAccessToken() : Single<String>

    @Query("SELECT refreshToken from UserEntity")
    fun getRefreshToken():Single<String>


}