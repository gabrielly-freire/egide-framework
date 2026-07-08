import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ManifestationService, ManifestationSummary } from '../../services/manifestation/manifestation.service';

const EMPTY_SUMMARY: ManifestationSummary = {
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
  summary = signal<ManifestationSummary>(EMPTY_SUMMARY);

  statusEntries = computed(() => Object.entries(this.summary().byStatus ?? {}));
  typeEntries = computed(() => Object.entries(this.summary().byType ?? {}));

  constructor(private readonly manifestationService: ManifestationService) {}

  ngOnInit(): void {
    this.manifestationService.summary().subscribe({
      next: data => this.summary.set(data),
      error: err => console.error('Erro ao buscar dashboard:', err)
    });
  }

  count(status: string): number {
    return this.summary().byStatus?.[status] ?? 0;
  }
}
