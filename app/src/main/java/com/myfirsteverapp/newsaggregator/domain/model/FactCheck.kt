package com.myfirsteverapp.newsaggregator.domain.model

import androidx.compose.ui.graphics.Color

data class FactCheck(
    val id: String = "",
    val claim: String = "",
    val claimant: String = "",
    val claimDate: String = "",
    val status: VerificationStatus = VerificationStatus.PENDING,
    val explanation: String = "",
    val sources: List<String> = emptyList(),
    val articleUrl: String = "",
    val imageUrl: String = "",
    val checkedBy: String = "",
    val checkedDate: String = "",
    val category: String = ""
)

enum class VerificationStatus(
    val displayName: String,
    val color: Color,
    val badgeText: String
) {
    VERIFIED(
        displayName = "Verified True",
        color = Color(0xFF4CAF50),
        badgeText = "✓ Verified"
    ),
    DISPUTED(
        displayName = "Disputed",
        color = Color(0xFFFF9800),
        badgeText = "⚠ Disputed"
    ),
    MISLEADING(
        displayName = "Misleading",
        color = Color(0xFFF44336),
        badgeText = "✗ Misleading"
    ),
    FALSE(
        displayName = "False",
        color = Color(0xFFD32F2F),
        badgeText = "✗ False"
    ),
    PARTIALLY_TRUE(
        displayName = "Partially True",
        color = Color(0xFF2196F3),
        badgeText = "◐ Partial"
    ),
    PENDING(
        displayName = "Pending Review",
        color = Color(0xFF9E9E9E),
        badgeText = "⋯ Pending"
    )
}

// Sample data generator for demo purposes
object FactCheckSamples {
    fun getSampleFactChecks(): List<FactCheck> {
        return listOf(
            FactCheck(
                id = "1",
                claim = "New transit plan will reduce average commute times by 20%",
                claimant = "City Transportation Department",
                claimDate = "2025-11-05",
                status = VerificationStatus.VERIFIED,
                explanation = "Independent traffic studies confirm that the proposed transit improvements, including dedicated bus lanes and optimized signal timing, are projected to reduce average commute times by 18-22% during peak hours.",
                sources = listOf(
                    "Urban Planning Institute Report 2025",
                    "City Traffic Analysis Data"
                ),
                checkedBy = "Transportation Analysis Team",
                checkedDate = "2025-11-07",
                category = "Politics"
            ),
            FactCheck(
                id = "2",
                claim = "Edge AI processing chips double smartphone battery life",
                claimant = "TechCorp CEO",
                claimDate = "2025-11-06",
                status = VerificationStatus.DISPUTED,
                explanation = "While edge AI chips do improve power efficiency for AI tasks, independent testing shows battery life improvements of 30-40% for AI-specific operations, not overall battery life. The claim of 'doubling' battery life is disputed by multiple tech reviewers.",
                sources = listOf(
                    "TechReview Lab Tests",
                    "Consumer Electronics Testing Board"
                ),
                checkedBy = "Tech Fact Check Division",
                checkedDate = "2025-11-07",
                category = "Technology"
            ),
            FactCheck(
                id = "3",
                claim = "Team guaranteed easy path to finals after latest victory",
                claimant = "Sports Commentator",
                claimDate = "2025-11-04",
                status = VerificationStatus.MISLEADING,
                explanation = "While the team's recent victory improves their playoff position, calling the path 'guaranteed' and 'easy' is misleading. They still face three top-ranked opponents and need to win 75% of remaining games to secure a finals spot.",
                sources = listOf(
                    "League Standings Analysis",
                    "Historical Playoff Data"
                ),
                checkedBy = "Sports Analytics Team",
                checkedDate = "2025-11-06",
                category = "Sports"
            ),
            FactCheck(
                id = "4",
                claim = "Quarterly revenue increased by 150% year-over-year",
                claimant = "Company Press Release",
                claimDate = "2025-11-03",
                status = VerificationStatus.PARTIALLY_TRUE,
                explanation = "Revenue did increase significantly, but the 150% figure includes one-time asset sales. Recurring revenue growth was 45% year-over-year, which is still substantial but significantly different from the headline figure.",
                sources = listOf(
                    "SEC Filing Analysis",
                    "Financial Analyst Reports"
                ),
                checkedBy = "Business Fact Checkers",
                checkedDate = "2025-11-05",
                category = "Business"
            ),
            FactCheck(
                id = "5",
                claim = "New vaccine prevents all strains of seasonal flu",
                claimant = "Pharmaceutical Company",
                claimDate = "2025-11-01",
                status = VerificationStatus.FALSE,
                explanation = "Clinical trials show the vaccine is effective against 4 major flu strains (approximately 85% of circulating variants) but does not provide protection against 'all strains.' The claim is scientifically inaccurate.",
                sources = listOf(
                    "FDA Clinical Trial Data",
                    "Independent Medical Review"
                ),
                checkedBy = "Medical Fact Check Team",
                checkedDate = "2025-11-06",
                category = "Health"
            ),
            FactCheck(
                id = "6",
                claim = "Electric vehicles produce zero lifetime emissions",
                claimant = "Environmental Group",
                claimDate = "2025-10-30",
                status = VerificationStatus.MISLEADING,
                explanation = "While EVs produce zero direct emissions during operation, the full lifecycle includes manufacturing emissions (particularly battery production) and indirect emissions from electricity generation. Total lifecycle emissions are 40-60% lower than conventional vehicles, not zero.",
                sources = listOf(
                    "EPA Lifecycle Analysis",
                    "International Energy Agency Report"
                ),
                checkedBy = "Environmental Fact Check Division",
                checkedDate = "2025-11-04",
                category = "Environment"
            )
        )
    }
}