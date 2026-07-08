import { Component, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ManifestationService } from '../../services/manifestation.service';
import { ManifestationResponse, ManifestationStatus, MAX_APPEALS, STATUS_LABELS, TYPE_LABELS } from '../../models/manifestation.model';
import { EvaluationService } from '../../services/evaluation.service';
import { ServiceEvaluationResponse } from '../../models/evaluation.model';
import { WorkflowService } from '../../services/workflow.service';

@Component({
  selector: 'app-portal',
  imports: [ReactiveFormsModule, RouterLink, DatePipe],
  templateUrl: './portal.html',
  styleUrl: './portal.css',
})
export class PortalPage {
  private readonly fb = inject(FormBuilder);
  private readonly manifestationService = inject(ManifestationService);
  private readonly evaluationService = inject(EvaluationService);
  private readonly workflowService = inject(WorkflowService);

  protected readonly STATUS_LABELS = STATUS_LABELS;
  protected readonly TYPE_LABELS = TYPE_LABELS;

  protected activeTab = signal<'submit' | 'track'>('submit');

  // Submit form
  protected submitForm = this.fb.group({
    title: ['', [Validators.required, Validators.maxLength(255)]],
    description: ['', [Validators.required, Validators.maxLength(2000)]],
    type: ['', Validators.required],
    affectedRegion: ['', Validators.maxLength(100)],
  });

  protected submitting = signal(false);
  protected submitError = signal('');
  protected submitted = signal<ManifestationResponse | null>(null);

  // Track form
  protected trackForm = this.fb.group({
    protocol: ['', Validators.required],
  });

  protected tracking = signal(false);
  protected trackError = signal('');
  protected tracked = signal<ManifestationResponse | null>(null);

  // Evaluation (o próprio cidadão avalia o atendimento da manifestação acompanhada)
  protected readonly ratings = [1, 2, 3, 4, 5];
  protected evaluation = signal<ServiceEvaluationResponse | null>(null);
  protected loadingEvaluation = signal(false);
  protected evaluationForm = this.fb.group({
    rating: [5, Validators.required],
    comment: [''],
  });
  protected savingEvaluation = signal(false);
  protected evaluationError = signal('');

  // Appeal (o cidadão pede recurso sobre a própria manifestação)
  protected readonly MAX_APPEALS = MAX_APPEALS;
  protected appealing = signal(false);
  protected appealError = signal('');

  submit(): void {
    if (this.submitForm.invalid) {
      this.submitForm.markAllAsTouched();
      return;
    }

    this.submitting.set(true);
    this.submitError.set('');

    const { title, description, type, affectedRegion } = this.submitForm.getRawValue();
    this.manifestationService
      .create({
        title: title!,
        description: description!,
        type: type! as any,
        affectedRegion: affectedRegion || undefined,
      })
      .subscribe({
        next: res => {
          this.submitted.set(res);
          this.submitting.set(false);
          this.submitForm.reset();
        },
        error: () => {
          this.submitError.set('Erro ao registrar manifestação. Tente novamente.');
          this.submitting.set(false);
        },
      });
  }

  track(): void {
    if (this.trackForm.invalid) {
      this.trackForm.markAllAsTouched();
      return;
    }

    this.tracking.set(true);
    this.trackError.set('');
    this.tracked.set(null);

    this.evaluation.set(null);
    this.evaluationError.set('');
    this.evaluationForm.reset({ rating: 5, comment: '' });
    this.appealError.set('');

    const protocol = this.trackForm.getRawValue().protocol!.trim();
    this.manifestationService.findByProtocol(protocol).subscribe({
      next: res => {
        this.tracked.set(res);
        this.tracking.set(false);
        this.loadEvaluation(res.id);
      },
      error: () => {
        this.trackError.set('Protocolo não encontrado.');
        this.tracking.set(false);
      },
    });
  }

  private loadEvaluation(manifestationId: number): void {
    this.loadingEvaluation.set(true);
    this.evaluationService.findByManifestationId(manifestationId).subscribe({
      next: ev => {
        this.evaluation.set(ev);
        this.loadingEvaluation.set(false);
      },
      error: () => {
        this.evaluation.set(null);
        this.loadingEvaluation.set(false);
      },
    });
  }

  createEvaluation(): void {
    if (this.evaluationForm.invalid || !this.tracked()) {
      this.evaluationForm.markAllAsTouched();
      return;
    }

    this.savingEvaluation.set(true);
    this.evaluationError.set('');
    const { rating, comment } = this.evaluationForm.getRawValue();

    this.evaluationService
      .create({
        manifestationId: this.tracked()!.id,
        rating: rating!,
        comment: comment || undefined,
      })
      .subscribe({
        next: ev => {
          this.evaluation.set(ev);
          this.savingEvaluation.set(false);
        },
        error: () => {
          this.evaluationError.set('Não foi possível registrar a avaliação.');
          this.savingEvaluation.set(false);
        },
      });
  }

  canEvaluate(status: string): boolean {
    return status === 'RESOLVED' || status === 'CLOSED';
  }

  canAppeal(): boolean {
    const m = this.tracked();
    if (!m) return false;
    return (m.status === 'IN_REVIEW' || m.status === 'RESOLVED') && m.appealCount < MAX_APPEALS;
  }

  appeal(): void {
    if (!this.tracked()) return;

    this.appealing.set(true);
    this.appealError.set('');
    this.workflowService.appeal(this.tracked()!.id).subscribe({
      next: updated => {
        this.tracked.set(updated);
        this.appealing.set(false);
      },
      error: () => {
        this.appealError.set('Não foi possível registrar o recurso neste momento.');
        this.appealing.set(false);
      },
    });
  }

  newSubmission(): void {
    this.submitted.set(null);
  }

  statusLabel(status: string): string {
    return STATUS_LABELS[status as ManifestationStatus] ?? status;
  }

  statusClass(status: string): string {
    return `badge-status-${status}`;
  }
}
