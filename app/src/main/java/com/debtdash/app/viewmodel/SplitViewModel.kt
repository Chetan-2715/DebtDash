package com.debtdash.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.debtdash.app.data.local.entity.FriendEntity
import com.debtdash.app.data.local.entity.TransactionEntity
import com.debtdash.app.data.local.entity.TransactionType
import com.debtdash.app.data.repository.DebtRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, kotlinx.coroutines.FlowPreview::class)
@HiltViewModel
class SplitViewModel @Inject constructor(
    private val repository: DebtRepository
) : ViewModel() {

    // ── Search ──
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val searchResults: StateFlow<List<FriendEntity>> =
        _searchQuery
            .debounce(300)
            .flatMapLatest { query ->
                if (query.isBlank()) repository.getAllFriends()
                else repository.searchFriends(query)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ── Split Form State ──
    private val _amount = MutableStateFlow("")
    val amount: StateFlow<String> = _amount.asStateFlow()

    private val _reason = MutableStateFlow("")
    val reason: StateFlow<String> = _reason.asStateFlow()

    private val _selectedFriends = MutableStateFlow<Set<Long>>(emptySet())
    val selectedFriends: StateFlow<Set<Long>> = _selectedFriends.asStateFlow()

    private val _isEqualSplit = MutableStateFlow(true)
    val isEqualSplit: StateFlow<Boolean> = _isEqualSplit.asStateFlow()

    // ── Linked transaction (when navigating from dashboard) ──
    private var linkedTransactionId: Long = -1L

    fun setLinkedTransaction(id: Long) {
        linkedTransactionId = id
    }

    // ── Actions ──

    fun updateSearch(query: String) {
        _searchQuery.value = query
    }

    fun updateAmount(value: String) {
        _amount.value = value
    }

    fun updateReason(value: String) {
        _reason.value = value
    }

    fun toggleFriend(friendId: Long) {
        _selectedFriends.value = _selectedFriends.value.toMutableSet().apply {
            if (contains(friendId)) remove(friendId) else add(friendId)
        }
    }

    fun setSplitMode(isEqual: Boolean) {
        _isEqualSplit.value = isEqual
    }

    fun setQuickReason(tag: String) {
        _reason.value = tag
    }

    /**
     * Creates the transaction and splits.
     * If linked to an existing transaction (from dashboard), updates that instead.
     */
    fun initializeSplit() {
        viewModelScope.launch {
            val amountValue = _amount.value.toDoubleOrNull() ?: return@launch
            val friends = _selectedFriends.value.toList()
            if (friends.isEmpty()) return@launch

            val transactionId: Long

            if (linkedTransactionId != -1L) {
                // Update the existing transaction with reason
                repository.updateTransactionReason(
                    linkedTransactionId,
                    _reason.value.ifBlank { null }
                )
                transactionId = linkedTransactionId
            } else {
                // Create a new manual transaction
                transactionId = repository.insertTransaction(
                    TransactionEntity(
                        rawNotificationText = "Manual: Sent ₹$amountValue",
                        amount = amountValue,
                        type = TransactionType.SENT,
                        reason = _reason.value.ifBlank { null }
                    )
                )
            }

            // Create splits
            if (_isEqualSplit.value) {
                repository.createEqualSplit(transactionId, amountValue, friends)
            }

            // Link ALL selected friends to the transaction
            friends.forEach { friendId ->
                repository.linkTransactionToFriend(transactionId, friendId)
            }

            // Reset form
            resetForm()
        }
    }

    /**
     * Adds a new friend to the database.
     */
    fun addFriend(name: String, phone: String? = null, upiId: String? = null) {
        viewModelScope.launch {
            val initials = name.split(" ")
                .take(2)
                .joinToString("") { it.firstOrNull()?.uppercase() ?: "" }

            repository.insertFriend(
                FriendEntity(
                    name = name,
                    phone = phone,
                    avatarInitials = initials.ifEmpty { "??" },
                    upiId = upiId
                )
            )
        }
    }

    fun resetForm() {
        _amount.value = ""
        _reason.value = ""
        _selectedFriends.value = emptySet()
        _isEqualSplit.value = true
    }
}
