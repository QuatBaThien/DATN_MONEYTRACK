package com.thienhd.noteapp.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class FilterCriteria(
    val startDate: Date? = null,
    val endDate: Date? = null,
    val type: Int? = 0, // 0: All, 1: Income, 2: Expense
    val order: Int? = 0 // 0: Newest, 1: Oldest
) : Parcelable
