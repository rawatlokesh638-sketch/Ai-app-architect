package com.example.data.json

import com.example.data.model.ProjectBlueprint
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object BlueprintJsonAdapter {
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val adapter = moshi.adapter(ProjectBlueprint::class.java)

    fun toJson(blueprint: ProjectBlueprint): String {
        return try {
            adapter.toJson(blueprint)
        } catch (e: Exception) {
            "{}"
        }
    }

    fun fromJson(json: String): ProjectBlueprint? {
        return try {
            adapter.fromJson(json)
        } catch (e: Exception) {
            null
        }
    }
}
