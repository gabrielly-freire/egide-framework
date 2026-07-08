export interface ManifestationSummaryReport {
  totalManifestations: number;
  byStatus: Record<string, number>;
  byType: Record<string, number>;
  totalEvaluations: number;
  averageRating: number | null;
  totalDecisions: number;
  totalOpinions: number;
}
