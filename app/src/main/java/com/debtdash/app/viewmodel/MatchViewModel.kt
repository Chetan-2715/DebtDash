package com.debtdash.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.debtdash.app.data.local.entity.TransactionEntity
import com.debtdash.app.data.repository.DebtRepository
import com.debtdash.app.matching.ReverseMatchEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the "Match" screen — reverse matching incoming payments.
 */
@HiltViewModel
class MatchViewModel @Inject constructor(
    private val repository: DebtRepository,
    private val matchEngine: ReverseMatchEngine
) : ViewModel() {

    /** Unmatched received transactions */
    val unmatchedReceived: StateFlow<List<TransactionEntity>> =
        repository.getUnmatchedReceived()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Unsettled sent transactions */
    val unsettledSent: StateFlow<List<TransactionEntity>> =
        repository.getUnsettledTransactions()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Finds matching sent transactions for a received payment.
     */
    suspend fun findMatchesForReceived(upiId: String): List<TransactionEntity> {
        return matchEngine.findMatches(upiId)
    }

    /**
     * Settles a specific received+sent pair.
     */
    fun settleMatch(receivedId: Long, sentId: Long) {
        viewModelScope.launch {
            matchEngine.settleMatch(receivedId, sentId)
        }
    }

    /**
     * Auto-settles all matching debts for a received payment.
     */
    fun autoSettleAll(receivedId: Long, upiId: String, receivedAmount: Double) {
        viewModelScope.launch {
            val matches = matchEngine.findMatches(upiId)
            if (matches.isNotEmpty()) {
                matchEngine.settleAll(receivedId, matches, receivedAmount)
            }
        }
    }
}
