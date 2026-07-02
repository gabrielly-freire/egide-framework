import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { ManifestationService } from '../../../services/manifestation.service';
import {
  ManifestationResponse,
  ManifestationStatus,
  STATUS_LABELS,
  TYPE_LABELS,
} from '../../../models/manifestation.model';

@Component({
  selector: 'app-manifestation-detail',
  imports: [RouterLink, DatePipe],
  templateUrl: './manifestation-detail.html',
  styleUrl: './manifestation-detail.css',
})
export class ManifestationDetailPage implements OnInit {
  private readonly manifestationService = inject(ManifestationService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  protected readonly STATUS_LABELS = STATUS_LABELS;
  protected readonly TYPE_LABELS = TYPE_LABELS;

  protected manifestation = signal<ManifestationResponse | null>(null);
  protected loading = signal(true);
  protected error = signal('');
  protected deleting = signal(false);

  protected readonly statuses: ManifestationStatus[] = [
    'REGISTERED',
    'IN_REVIEW',
    'RESOLVED',
    'CLOSED',
  ];

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.manifestationService.findById(id).subscribe({
      next: m => {
        this.manifestation.set(m);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Manifestação não encontrada.');
        this.loading.set(false);
      },
    });
  }

  updateStatus(status: ManifestationStatus): void {
    const m = this.manifestation();
    if (!m || m.status === status) return;

    this.manifestationService.update(m.id, { title: m.title, description: m.description, type: m.type as any }).subscribe({
      next: updated => this.manifestation.set(updated),
    });
  }

  delete(): void {
    if (!confirm('Confirma a exclusão desta manifestação?')) return;

    this.deleting.set(true);
    this.manifestationService.delete(this.manifestation()!.id).subscribe({
      next: () => this.router.navigate(['/manifestacoes']),
      error: () => this.deleting.set(false),
    });
  }

  statusClass(status: string): string {
    return `badge-status-${status}`;
  }
}
