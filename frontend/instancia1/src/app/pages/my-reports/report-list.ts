import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs/operators';
import { ManifestationService } from '../../services/manifestation/manifestation.service';
import {
  ManifestationHistoryEntry,
  ManifestationHistoryService
} from '../../services/manifestation/manifestation-history.service';
import { EvaluationService } from '../../services/evaluation/evaluation.service';
import { ManifestationResponse, manifestationStatusLabel } from '../../models/manifestation.model';
import { ServiceEvaluationResponse } from '../../models/evaluation.model';

@Component({
  selector: 'app-report-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './report-list.html',
  styleUrl: './report-list.css'
})
export class MyReports {
  protocolInput = '';
  loading = signal(false);
  notFound = signal(false);
  manifestation = signal<ManifestationResponse | null>(null);

  evaluation = signal<ServiceEvaluationResponse | null>(null);
  evaluationForm = { rating: 0, comment: '' };
  submittingEvaluation = signal(false);
  evaluationSubmitted = signal(false);

  readonly history = signal<ManifestationHistoryEntry[]>([]);

  constructor(
    private readonly manifestationService: ManifestationService,
    private readonly historyService: ManifestationHistoryService,
    private readonly evaluationService: EvaluationService
  ) {
    this.history.set(this.historyService.list());
  }

  search(protocolNumber?: string): void {
    const protocol = (protocolNumber ?? this.protocolInput).trim();
    if (!protocol) return;

    this.protocolInput = protocol;
    this.loading.set(true);
    this.notFound.set(false);
    this.manifestation.set(null);
    this.evaluation.set(null);
    this.evaluationSubmitted.set(false);

    this.manifestationService
      .getByProtocol(protocol)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: manifestation => {
          this.manifestation.set(manifestation);
          if (manifestation.status === 'RESOLVED') {
            this.loadEvaluation(manifestation.id);
          }
        },
        error: () => this.notFound.set(true)
      });
  }

  private loadEvaluation(manifestationId: number): void {
    this.evaluationService.getByManifestation(manifestationId).subscribe({
      next: evaluation => this.evaluation.set(evaluation),
      error: () => this.evaluation.set(null)
    });
  }

  submitEvaluation(): void {
    const manifestation = this.manifestation();
    if (!manifestation || this.evaluationForm.rating < 1) return;

    this.submittingEvaluation.set(true);
    this.evaluationService
      .create({
        manifestationId: manifestation.id,
        rating: this.evaluationForm.rating,
        comment: this.evaluationForm.comment || null
      })
      .pipe(finalize(() => this.submittingEvaluation.set(false)))
      .subscribe({
        next: evaluation => {
          this.evaluation.set(evaluation);
          this.evaluationSubmitted.set(true);
        },
        error: err => console.error('Erro ao enviar avaliação:', err)
      });
  }

  statusLabel(status: string | null | undefined): string {
    return manifestationStatusLabel(status);
  }

  statusClass(status: string | null | undefined): string {
    switch (status) {
      case 'RESOLVED':
      case 'CLOSED':
        return 'status-completed';
      default:
        return 'status-analysis';
    }
  }
}
