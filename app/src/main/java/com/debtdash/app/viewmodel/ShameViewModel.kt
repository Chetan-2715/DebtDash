package com.debtdash.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.debtdash.app.data.local.dao.FriendDebtSummary
import com.debtdash.app.data.repository.DebtRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel for the "Shame" screen — friends who owe you money,
 * sorted by outstanding debt.
 */
@HiltViewModel
class ShameViewModel @Inject constructor(
    repository: DebtRepository
) : ViewModel() {

    /** Friends with positive net debt (they owe you), sorted highest first */
    val debtors: StateFlow<List<FriendDebtSummary>> =
        repository.getDebtSummary()
            .map { list -> list.filter { it.netDebt > 0 }.sortedByDescending { it.netDebt } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Friends you owe money to */
    val creditors: StateFlow<List<FriendDebtSummary>> =
        repository.getDebtSummary()
            .map { list -> list.filter { it.netDebt < 0 }.sortedBy { it.netDebt } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
