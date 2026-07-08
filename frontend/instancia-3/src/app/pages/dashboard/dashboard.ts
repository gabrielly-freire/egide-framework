import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ManifestationService } from '../../services/manifestation.service';
import { AuthService } from '../../services/auth.service';
import { ManifestationResponse, STATUS_LABELS } from '../../models/manifestation.model';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-dashboard',
  imports: [RouterLink, DatePipe],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class DashboardPage implements OnInit {
  private readonly manifestationService = inject(ManifestationService);
  private readonly authService = inject(AuthService);

  protected readonly STATUS_LABELS = STATUS_LABELS;

  protected total = signal(0);
  protected byStatus = signal<Record<string, number>>({});
  protected recent = signal<ManifestationResponse[]>([]);
  protected loading = signal(true);

  ngOnInit(): void {
    const page$ = this.authService.isAdmin()
      ? this.manifestationService.list(0, 5)
      : this.manifestationService.listMine(0, 5);

    page$.subscribe({
      next: page => {
        this.total.set(page.totalElements);
        this.recent.set(page.content);

        const counts: Record<string, number> = {
          REGISTERED: 0,
          IN_REVIEW: 0,
          RESOLVED: 0,
          CLOSED: 0,
        };
        page.content.forEach(m => counts[m.status]++);
        this.byStatus.set(counts);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  statusClass(status: string): string {
    return `badge-status-${status}`;
  }
}
