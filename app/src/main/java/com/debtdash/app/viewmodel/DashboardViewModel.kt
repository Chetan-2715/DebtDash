package com.debtdash.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.debtdash.app.data.local.dao.FriendDebtSummary
import com.debtdash.app.data.local.entity.TransactionEntity
import com.debtdash.app.data.repository.DebtRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: DebtRepository
) : ViewModel() {

    /** All transactions with friend details (latest first) */
    val transactions: StateFlow<List<com.debtdash.app.data.local.dao.TransactionWithFriend>> =
        repository.getAllTransactionsWithFriends()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Cumulative debt summary per friend */
    val debtSummary: StateFlow<List<FriendDebtSummary>> =
        repository.getDebtSummary()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Cumulative summary per business */
    val businessSummary: StateFlow<List<FriendDebtSummary>> =
        repository.getBusinessSummary()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Total ledger value (sum of all unsettled SENT amounts) */
    val totalLedgerValue: StateFlow<Double>
        get() = MutableStateFlow(0.0) // Computed in the UI from debtSummary

    /** Count of unreasoned transactions */
    val unreasonedTransactions: StateFlow<List<TransactionEntity>> =
        repository.getUnreasonedTransactions()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
