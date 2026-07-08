import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { ManifestationService } from '../../services/manifestation/manifestation.service';
import { ManifestationResponse, manifestationStatusLabel } from '../../models/manifestation.model';

@Component({
  selector: 'app-report-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './report-list.html',
  styleUrl: './report-list.css'
})
export class ReportList implements OnInit {
  readonly pageSize = 10;

  manifestations = signal<ManifestationResponse[]>([]);
  loading = signal(false);
  page = signal(0);
  totalPages = signal(0);
  totalElements = signal(0);

  constructor(private readonly manifestationService: ManifestationService) {}

  ngOnInit(): void {
    this.loadPage();
  }

  loadPage(): void {
    this.loading.set(true);
    this.manifestationService
      .listPaged(this.page(), this.pageSize)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: page => {
          this.manifestations.set(page.content);
          this.totalPages.set(page.totalPages);
          this.totalElements.set(page.totalElements);
        },
        error: err => console.error('Erro ao carregar manifestações:', err)
      });
  }

  previousPage(): void {
    if (this.page() === 0 || this.loading()) return;
    this.page.update(v => v - 1);
    this.loadPage();
  }

  nextPage(): void {
    if (this.page() + 1 >= this.totalPages() || this.loading()) return;
    this.page.update(v => v + 1);
    this.loadPage();
  }

  statusLabel(status: string): string {
    return manifestationStatusLabel(status);
  }

  statusClass(status: string): string {
    switch (status) {
      case 'RESOLVED':
      case 'CLOSED':
        return 'status-completed';
      default:
        return 'status-analysis';
    }
  }
}
