package com.jae464.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.jae464.domain.repository.PreferenceRepository
import javax.inject.Inject

class PreferenceRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : PreferenceRepository {
    override fun getThemeMode() {
        Log.d("PreferenceRepositoryImpl", "getThemeMode()")
    }
}