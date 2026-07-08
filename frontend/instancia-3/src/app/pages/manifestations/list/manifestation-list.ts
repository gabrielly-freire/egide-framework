import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { ManifestationService } from '../../../services/manifestation.service';
import { AuthService } from '../../../services/auth.service';
import { ManifestationResponse, STATUS_LABELS, TYPE_LABELS } from '../../../models/manifestation.model';

@Component({
  selector: 'app-manifestation-list',
  imports: [RouterLink, FormsModule, DatePipe],
  templateUrl: './manifestation-list.html',
  styleUrl: './manifestation-list.css',
})
export class ManifestationListPage implements OnInit {
  private readonly manifestationService = inject(ManifestationService);
  private readonly authService = inject(AuthService);

  protected readonly STATUS_LABELS = STATUS_LABELS;
  protected readonly TYPE_LABELS = TYPE_LABELS;

  protected items = signal<ManifestationResponse[]>([]);
  protected loading = signal(true);
  protected error = signal('');

  protected currentPage = signal(0);
  protected totalPages = signal(0);
  protected totalElements = signal(0);
  protected pageSize = 10;

  protected searchProtocol = '';
  protected readonly isAdmin = this.authService.isAdmin();

  ngOnInit(): void {
    this.load();
  }

  load(page = 0): void {
    this.loading.set(true);
    const page$ = this.isAdmin
      ? this.manifestationService.list(page, this.pageSize)
      : this.manifestationService.listMine(page, this.pageSize);

    page$.subscribe({
      next: res => {
        this.items.set(res.content);
        this.currentPage.set(res.number);
        this.totalPages.set(res.totalPages);
        this.totalElements.set(res.totalElements);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Erro ao carregar manifestações.');
        this.loading.set(false);
      },
    });
  }

  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages()) {
      this.load(page);
    }
  }

  pages(): number[] {
    return Array.from({ length: this.totalPages() }, (_, i) => i);
  }

  statusClass(status: string): string {
    return `badge-status-${status}`;
  }
}
