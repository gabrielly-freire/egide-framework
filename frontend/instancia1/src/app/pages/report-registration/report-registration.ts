import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { ManifestationService } from '../../services/manifestation/manifestation.service';
import { ManifestationHistoryService } from '../../services/manifestation/manifestation-history.service';
import { AttachmentService } from '../../services/attachment/attachment.service';
import { ManifestationResponse, MANIFESTATION_TYPE_OPTIONS } from '../../models/manifestation.model';

@Component({
  selector: 'app-report-registration',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './report-registration.html',
  styleUrl: './report-registration.css'
})
export class ReportRegistration {
  readonly typeOptions = MANIFESTATION_TYPE_OPTIONS;

  reportForm;
  loading = signal(false);
  selectedFile: File | null = null;

  created = signal<ManifestationResponse | null>(null);
  uploadingAttachment = signal(false);
  attachmentUploaded = signal(false);
  serverError = signal<string | null>(null);

  constructor(
    private readonly fb: FormBuilder,
    private readonly manifestationService: ManifestationService,
    private readonly historyService: ManifestationHistoryService,
    private readonly attachmentService: AttachmentService
  ) {
    this.reportForm = this.fb.nonNullable.group({
      title: ['', [Validators.required]],
      description: ['', [Validators.required]],
      type: [this.typeOptions[0].value, [Validators.required]],
      anonymous: [false]
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0] ?? null;
    this.selectedFile = file;
  }

  onSubmit(): void {
    if (this.reportForm.invalid) {
      this.reportForm.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.serverError.set(null);

    this.manifestationService
      .create(this.reportForm.getRawValue())
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: manifestation => {
          this.created.set(manifestation);
          this.historyService.add({
            protocolNumber: manifestation.protocolNumber,
            title: manifestation.title,
            createdAt: manifestation.createdAt
          });
        },
        error: err => {
          const msg = typeof err === 'string' ? err : 'Erro ao enviar manifestação. Verifique a conexão com o servidor.';
          this.serverError.set(msg);
        }
      });
  }

  uploadAttachment(): void {
    const manifestation = this.created();
    if (!manifestation || !this.selectedFile) return;

    this.uploadingAttachment.set(true);
    this.attachmentService
      .upload(manifestation.id, this.selectedFile)
      .pipe(finalize(() => this.uploadingAttachment.set(false)))
      .subscribe({
        next: () => this.attachmentUploaded.set(true),
        error: () => this.serverError.set('Não foi possível anexar o arquivo.')
      });
  }

  newManifestation(): void {
    this.created.set(null);
    this.selectedFile = null;
    this.attachmentUploaded.set(false);
    this.serverError.set(null);
    this.reportForm.reset({
      title: '',
      description: '',
      type: this.typeOptions[0].value,
      anonymous: false
    });
  }
}
