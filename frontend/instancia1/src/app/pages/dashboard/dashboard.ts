import { Component, OnInit, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReportSummaryService } from '../../services/report-summary/report-summary.service';
import { ManifestationSummaryReport } from '../../models/report-summary.model';
import { manifestationStatusLabel } from '../../models/manifestation.model';

interface BreakdownRow {
  key: string;
  label: string;
  value: number;
  percent: number;
}

const EMPTY_SUMMARY: ManifestationSummaryReport = {
  totalManifestations: 0,
  byStatus: {},
  byType: {},
  totalEvaluations: 0,
  averageRating: null,
  totalDecisions: 0,
  totalOpinions: 0
};

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard implements OnInit {
  loading = signal(false);
  error = signal<string | null>(null);
  summary = signal<ManifestationSummaryReport>(EMPTY_SUMMARY);

  byStatusRows = computed<BreakdownRow[]>(() => this.toRows(this.summary().byStatus, manifestationStatusLabel));
  byTypeRows = computed<BreakdownRow[]>(() => this.toRows(this.summary().byType));

  constructor(private readonly reportSummaryService: ReportSummaryService) {}

  ngOnInit(): void {
    this.loading.set(true);
    this.reportSummaryService.summary().subscribe({
      next: data => {
        this.summary.set(data);
        this.loading.set(false);
      },
      error: err => {
        console.error('Erro ao buscar resumo:', err);
        this.error.set('Não foi possível carregar os indicadores.');
        this.loading.set(false);
      }
    });
  }

  private toRows(map: Record<string, number>, labelFn?: (key: string) => string): BreakdownRow[] {
    const total = this.summary().totalManifestations || 0;
    return Object.entries(map || {}).map(([key, value]) => ({
      key,
      label: labelFn ? labelFn(key) : key,
      value,
      percent: total > 0 ? (value / total) * 100 : 0
    }));
  }
}
