import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ManifestationService } from '../../services/manifestation/manifestation.service';

@Component({
  selector: 'app-report-registration',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './report-registration.html',
  styleUrl: './report-registration.css'
})
export class ReportRegistration {
  reportForm: FormGroup;
  loading = false;

  constructor(
    private readonly fb: FormBuilder,
    private readonly manifestationService: ManifestationService
  ) {
    this.reportForm = this.fb.group({
      title: ['', [Validators.required]],
      description: ['', [Validators.required]],
      type: ['RECLAMACAO', [Validators.required]],
      anonymous: [false]
    });
  }

  onSubmit(): void {
    if (this.reportForm.invalid) {
      return;
    }
    this.loading = true;

    this.manifestationService.create(this.reportForm.value).subscribe({
      next: manifestation => {
        alert(`Manifestação enviada com sucesso! Protocolo: ${manifestation.protocolNumber}`);
        this.reportForm.reset({ type: 'RECLAMACAO', anonymous: false });
        this.loading = false;
      },
      error: err => {
        console.error('Erro ao enviar:', err);
        alert('Erro ao enviar manifestação. Verifique a conexão com o servidor.');
        this.loading = false;
      }
    });
  }
}
