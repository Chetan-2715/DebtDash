package com.debtdash.app.service

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
 * Regex-based parser for Google Pay notification text.
 *
 * Handles real-world GPay notification patterns including:
 *  - "Sent ₹500.00 to arjun@okaxis"
 *  - "Received ₹200.00 from priya@ybl"
 *  - "You sent ₹1,200 to someone@paytm"
 *  - "₹500 received from friend@upi"
 *  - "Payment of ₹500 made to Arjun"
 *  - "You have received ₹200 from Priya via UPI"
 *  - "₹500 paid to arjun@okaxis"
 *  - "Money received! ₹200 from priya@ybl"
 *  - title: "Sent ₹500", text: "To arjun@okaxis"
 */
object TransactionParser {

    // ── SENT Patterns ──

    // "Sent ₹500.00 to arjun@okaxis" / "You sent ₹1,200 to someone"
    private val SENT_STANDARD = Regex(
        """(?:[Yy]ou\s+)?[Ss]ent\s*₹?\s*([\d,]+\.?\d*)\s*to\s+(.+)""",
        RegexOption.IGNORE_CASE
    )

    // "Payment of ₹500 made to Arjun" / "Payment of ₹500 to arjun@okaxis"
    private val SENT_PAYMENT_OF = Regex(
        """[Pp]ayment\s+of\s*₹?\s*([\d,]+\.?\d*)\s*(?:made\s+)?to\s+(.+)""",
        RegexOption.IGNORE_CASE
    )

    // "₹500 paid to arjun@okaxis" / "₹500 sent to Arjun"
    private val SENT_AMOUNT_FIRST = Regex(
        """₹\s*([\d,]+\.?\d*)\s*(?:paid|sent)\s+to\s+(.+)""",
        RegexOption.IGNORE_CASE
    )

    // "Paid ₹500 to arjun@okaxis"
    private val SENT_PAID = Regex(
        """[Pp]aid\s*₹?\s*([\d,]+\.?\d*)\s*to\s+(.+)""",
        RegexOption.IGNORE_CASE
    )

    // "Debited ₹500 for payment to Arjun"
    private val SENT_DEBITED = Regex(
        """[Dd]ebited\s*₹?\s*([\d,]+\.?\d*).*?(?:to|for\s+payment\s+to)\s+(.+)""",
        RegexOption.IGNORE_CASE
    )

    // ── RECEIVED Patterns ──

    // "Received ₹200.00 from priya@ybl"
    private val RECEIVED_STANDARD = Regex(
        """[Rr]eceived\s*₹?\s*([\d,]+\.?\d*)\s*from\s+(.+)""",
        RegexOption.IGNORE_CASE
    )

    // "₹200 received from priya@ybl"
    private val RECEIVED_AMOUNT_FIRST = Regex(
        """₹\s*([\d,]+\.?\d*)\s*received\s*from\s+(.+)""",
        RegexOption.IGNORE_CASE
    )

    // "You have received ₹200 from Priya"
    private val RECEIVED_FORMAL = Regex(
        """[Yy]ou\s+(?:have\s+)?received\s*₹?\s*([\d,]+\.?\d*)\s*from\s+(.+)""",
        RegexOption.IGNORE_CASE
    )

    // "Credited ₹200 from Priya" / "₹200 credited"
    private val RECEIVED_CREDITED = Regex(
        """(?:₹\s*([\d,]+\.?\d*)\s*)?[Cc]redited\s*₹?\s*([\d,]+\.?\d*)?\s*(?:from\s+(.+))?""",
        RegexOption.IGNORE_CASE
    )

    // ── Amount extraction fallback ──
    private val AMOUNT_ANYWHERE = Regex("""₹\s*([\d,]+\.?\d*)""")

    // ── UPI ID extraction ──
    private val UPI_ID_REGEX = Regex("""[\w.\-]+@[\w]+""")

    /**
     * Attempts to parse a GPay notification string.
     * Returns null only if absolutely no amount can be extracted.
     */
    fun parse(text: String): ParsedTransaction? {
        // Try all SENT patterns
        tryParseSent(text, SENT_STANDARD)?.let { return it }
        tryParseSent(text, SENT_PAYMENT_OF)?.let { return it }
        tryParseSent(text, SENT_AMOUNT_FIRST)?.let { return it }
        tryParseSent(text, SENT_PAID)?.let { return it }
        tryParseSent(text, SENT_DEBITED)?.let { return it }

        // Try all RECEIVED patterns
        tryParseReceived(text, RECEIVED_STANDARD)?.let { return it }
        tryParseReceived(text, RECEIVED_AMOUNT_FIRST)?.let { return it }
        tryParseReceived(text, RECEIVED_FORMAL)?.let { return it }

        // Credited pattern (special handling)
        RECEIVED_CREDITED.find(text)?.let { match ->
            val amount = parseAmount(match.groupValues[1].ifBlank { match.groupValues[2] })
            val contact = match.groupValues.getOrNull(3)?.takeIf { it.isNotBlank() }
            if (amount != null && amount > 0) {
                return ParsedTransaction(
                    amount = amount,
                    type = TransactionType.RECEIVED,
                    upiId = contact?.let { extractUpiId(it) },
                    contactName = contact?.let { cleanContact(it) }
                )
            }
        }

        // ── Keyword-based fallback ──
        val amount = AMOUNT_ANYWHERE.find(text)?.let { parseAmount(it.groupValues[1]) }
        if (amount != null && amount > 0) {
            val type = when {
                text.contains("sent", ignoreCase = true) ||
                text.contains("paid", ignoreCase = true) ||
                text.contains("debited", ignoreCase = true) ||
                text.contains("payment", ignoreCase = true) -> TransactionType.SENT

                text.contains("received", ignoreCase = true) ||
                text.contains("credited", ignoreCase = true) -> TransactionType.RECEIVED

                else -> TransactionType.SENT // Default assumption
            }

            val upiId = UPI_ID_REGEX.find(text)?.value
            val contactName = extractContactName(text)

            return ParsedTransaction(
                amount = amount,
                type = type,
                upiId = upiId,
                contactName = contactName
            )
        }

        return null
    }

    /**
     * Parse from both title and text of a notification.
     * Some GPay notifications split info: title="Sent ₹500", text="To arjun@okaxis"
     */
    fun parseFromTitleAndText(title: String, text: String, bigText: String): ParsedTransaction? {
        // Try the most detailed text first
        val fullText = bigText.ifBlank { text }
        parse(fullText)?.let { return it }

        // Try combining title + text
        val combined = "$title $text".trim()
        parse(combined)?.let { return it }

        // Try title alone
        parse(title)?.let { return it }

        return null
    }

    private fun tryParseSent(text: String, regex: Regex): ParsedTransaction? {
        return regex.find(text)?.let { match ->
            val amount = parseAmount(match.groupValues[1])
            val contact = match.groupValues[2]
            if (amount != null && amount > 0) {
                ParsedTransaction(
                    amount = amount,
                    type = TransactionType.SENT,
                    upiId = extractUpiId(contact),
                    contactName = cleanContact(contact)
                )
            } else null
        }
    }

    private fun tryParseReceived(text: String, regex: Regex): ParsedTransaction? {
        return regex.find(text)?.let { match ->
            val amount = parseAmount(match.groupValues[1])
            val contact = match.groupValues[2]
            if (amount != null && amount > 0) {
                ParsedTransaction(
                    amount = amount,
                    type = TransactionType.RECEIVED,
                    upiId = extractUpiId(contact),
                    contactName = cleanContact(contact)
                )
            } else null
        }
    }

    /**
     * Parses "1,200.50" → 1200.50
     */
    private fun parseAmount(raw: String): Double? {
        if (raw.isBlank()) return null
        return try {
            raw.replace(",", "").trim().toDouble()
        } catch (e: NumberFormatException) {
            null
        }
    }

    /**
     * Extracts a UPI ID (user@bank) from a contact string.
     */
    private fun extractUpiId(contact: String): String? {
        return UPI_ID_REGEX.find(contact)?.value
    }

    /**
     * Tries to extract a human-readable name from notification text.
     */
    private fun extractContactName(text: String): String? {
        // Look for "to <name>" or "from <name>"
        val toFrom = Regex("""(?:to|from)\s+(.+?)(?:\s+on|\s+via|\.|$)""", RegexOption.IGNORE_CASE)
        return toFrom.find(text)?.groupValues?.get(1)?.let { cleanContact(it) }
    }

    /**
     * Cleans up trailing punctuation and suffixes from parsed contact strings.
     */
    private fun cleanContact(raw: String): String {
        return raw.trim()
            .removeSuffix(".")
            .removeSuffix(",")
            .removeSuffix(" on UPI")
            .removeSuffix(" via UPI")
            .removeSuffix(" on Google Pay")
            .removeSuffix(" via Google Pay")
            .trim()
    }
}
