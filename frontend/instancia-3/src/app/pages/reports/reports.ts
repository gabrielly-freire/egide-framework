import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { DatePipe } from '@angular/common';
import { ManifestationStatus, STATUS_LABELS, TYPE_LABELS } from '../../models/manifestation.model';

interface SummaryReport {
  totalManifestations: number;
  byStatus: Record<string, number>;
  byType: Record<string, number>;
  from: string;
  to: string;
}

@Component({
  selector: 'app-reports',
  imports: [ReactiveFormsModule, DatePipe],
  templateUrl: './reports.html',
  styleUrl: './reports.css',
})
export class ReportsPage {
  private readonly http = inject(HttpClient);
  private readonly fb = inject(FormBuilder);

  protected readonly STATUS_LABELS = STATUS_LABELS;
  protected readonly TYPE_LABELS = TYPE_LABELS;

  protected form = this.fb.group({
    from: ['', Validators.required],
    to: ['', Validators.required],
  });

  protected loading = signal(false);
  protected error = signal('');
  protected report = signal<SummaryReport | null>(null);

  load(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.error.set('');
    this.report.set(null);

    const { from, to } = this.form.getRawValue();
    this.http
      .get<SummaryReport>(`/api/v1/reports/summary?from=${from}&to=${to}`)
      .subscribe({
        next: res => {
          this.report.set(res);
          this.loading.set(false);
        },
        error: () => {
          this.error.set('Erro ao gerar relatório.');
          this.loading.set(false);
        },
      });
  }

  entries(obj: Record<string, number>): { key: string; value: number }[] {
    return Object.entries(obj).map(([key, value]) => ({ key, value }));
  }

  statusLabel(key: string): string {
    return STATUS_LABELS[key as ManifestationStatus] ?? key;
  }

  typeLabel(key: string): string {
    return TYPE_LABELS[key] ?? key;
  }

  statusClass(key: string): string {
    return `badge-status-${key}`;
  }
}
