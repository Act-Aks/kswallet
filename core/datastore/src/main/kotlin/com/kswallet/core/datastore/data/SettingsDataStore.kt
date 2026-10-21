package com.kswallet.core.datastore.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

/** One DataStore file for the whole process; the delegate must stay a top-level property. */
val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "kswallet_settings")
