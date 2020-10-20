package com.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UserEntity")
data class Entity(
 @PrimaryKey(autoGenerate = true)
 @ColumnInfo(name = "id") var id: Int,
 @ColumnInfo(name = "accessToken") var accessToken: String?,
 @ColumnInfo(name = "refreshToken") var refreshToken: String?,
 @ColumnInfo(name = "expire") var expire:Long

) {
    constructor() : this(0, null, null,0)
}