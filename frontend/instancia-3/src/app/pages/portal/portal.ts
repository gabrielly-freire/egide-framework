import { Component, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ManifestationService } from '../../services/manifestation.service';
import { ManifestationResponse, ManifestationStatus, STATUS_LABELS, TYPE_LABELS } from '../../models/manifestation.model';

@Component({
  selector: 'app-portal',
  imports: [ReactiveFormsModule, RouterLink, DatePipe],
  templateUrl: './portal.html',
  styleUrl: './portal.css',
})
export class PortalPage {
  private readonly fb = inject(FormBuilder);
  private readonly manifestationService = inject(ManifestationService);

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

    const protocol = this.trackForm.getRawValue().protocol!.trim();
    this.manifestationService.findByProtocol(protocol).subscribe({
      next: res => {
        this.tracked.set(res);
        this.tracking.set(false);
      },
      error: () => {
        this.trackError.set('Protocolo não encontrado.');
        this.tracking.set(false);
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
