package com.debtdash.app.service

import android.util.Log
import com.debtdash.app.data.local.entity.TransactionType

/**
 * Parsed result from a GPay notification.
 */
data class ParsedTransaction(
    val amount: Double,
    val type: TransactionType,
    val upiId: String?,
    val contactName: String? = null
)

/**
 * Enhanced Regex-based parser for Google Pay notification text.
 * Strictly adheres to "Deep Space" performance standards.
 */
object TransactionParser {
    private const val TAG = "DebtDashParser"

    // ── SENT Patterns (Unified) ──
    private val SENT_PATTERNS = listOf(
        // "Sent ₹500 to arjun@okaxis", "You sent ₹1,200 to someone"
        Regex("""(?:You\s+)?sent\s*₹?\s*([\d,]+\.?\d*)\s*to\s+(.+)""", RegexOption.IGNORE_CASE),
        // "Paid ₹500 to arjun@okaxis"
        Regex("""Paid\s*₹?\s*([\d,]+\.?\d*)\s*to\s+(.+)""", RegexOption.IGNORE_CASE),
        // "₹500 paid to arjun@okaxis"
        Regex("""₹\s*([\d,]+\.?\d*)\s*(?:paid|sent)\s+to\s+(.+)""", RegexOption.IGNORE_CASE),
        // "Debited ₹500 for payment to Arjun"
        Regex("""Debited\s*₹?\s*([\d,]+\.?\d*).*?(?:to|for\s+payment\s+to)\s+(.+)""", RegexOption.IGNORE_CASE)
    )

    // ── RECEIVED Patterns (Unified) ──
    private val RECEIVED_PATTERNS = listOf(
        // "Received ₹200 from priya@ybl", "You have received ₹200 from Priya"
        Regex("""(?:You\s+(?:have\s+)?)?Received\s*₹?\s*([\d,]+\.?\d*)\s*from\s+(.+)""", RegexOption.IGNORE_CASE),
        // "₹200 received from priya@ybl"
        Regex("""₹\s*([\d,]+\.?\d*)\s*received\s*from\s+(.+)""", RegexOption.IGNORE_CASE),
        // "Credited ₹200 from Priya"
        Regex("""Credited\s*₹?\s*([\d,]+\.?\d*)\s*from\s+(.+)""", RegexOption.IGNORE_CASE)
    )

    private val AMOUNT_ANYWHERE = Regex("""₹\s*([\d,]+\.?\d*)""")
    private val UPI_ID_REGEX = Regex("""[\w.\-]+@[\w]+""")

    /**
     * Attempts to parse a GPay notification string.
     */
    fun parse(text: String): ParsedTransaction? {
        Log.d(TAG, "[RAW_INTERCEPT]: $text")

        // Try SENT
        for (pattern in SENT_PATTERNS) {
            pattern.find(text)?.let { match ->
                val amount = parseAmount(match.groupValues[1])
                val contact = match.groupValues[2]
                if (amount != null && amount > 0) {
                    return ParsedTransaction(amount, TransactionType.SENT, extractUpiId(contact), cleanContact(contact))
                }
            }
        }

        // Try RECEIVED
        for (pattern in RECEIVED_PATTERNS) {
            pattern.find(text)?.let { match ->
                val amount = parseAmount(match.groupValues[1])
                val contact = match.groupValues[2]
                if (amount != null && amount > 0) {
                    return ParsedTransaction(amount, TransactionType.RECEIVED, extractUpiId(contact), cleanContact(contact))
                }
            }
        }

        // Fallback for amounts if keywords match
        val amount = AMOUNT_ANYWHERE.find(text)?.let { parseAmount(it.groupValues[1]) }
        if (amount != null && amount > 0) {
            val isSent = text.contains("sent", true) || text.contains("paid", true) || text.contains("debited", true)
            val isReceived = text.contains("received", true) || text.contains("credited", true)

            if (isSent || isReceived) {
                return ParsedTransaction(
                    amount = amount,
                    type = if (isSent) TransactionType.SENT else TransactionType.RECEIVED,
                    upiId = extractUpiId(text),
                    contactName = extractContactName(text)
                )
            }
        }

        return null
    }

    fun parseFromTitleAndText(title: String, text: String, bigText: String): ParsedTransaction? {
        return parse(bigText.ifBlank { text }) 
            ?: parse("$title $text".trim()) 
            ?: parse(title)
    }

    private fun parseAmount(raw: String): Double? = try {
        raw.replace(",", "").trim().toDouble()
    } catch (e: Exception) { null }

    private fun extractUpiId(contact: String): String? = UPI_ID_REGEX.find(contact)?.value

    private fun extractContactName(text: String): String? {
        val toFrom = Regex("""(?:to|from)\s+(.+?)(?:\s+on|\s+via|\.|$)""", RegexOption.IGNORE_CASE)
        return toFrom.find(text)?.groupValues?.get(1)?.let { cleanContact(it) }
    }

    private fun cleanContact(raw: String): String = raw.trim()
        .removeSuffix(".")
        .removeSuffix(",")
        .replace(Regex("""\s+on\s+UPI.*""", RegexOption.IGNORE_CASE), "")
        .replace(Regex("""\s+via\s+UPI.*""", RegexOption.IGNORE_CASE), "")
        .trim()
}
