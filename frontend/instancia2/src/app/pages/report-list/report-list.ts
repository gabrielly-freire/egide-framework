import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { Manifestation, ManifestationService } from '../../services/manifestation/manifestation.service';

@Component({
  selector: 'app-report-list',
  standalone: true,
  imports: [CommonModule, RouterModule, MatIconModule],
  templateUrl: './report-list.html',
  styleUrl: './report-list.css'
})
export class ReportList implements OnInit {
  reports = signal<Manifestation[]>([]);

  reportsInAnalysis = computed(
    () => this.reports().filter(r => r.status?.toUpperCase() === 'IN_REVIEW').length
  );

  reportsCompleted = computed(
    () => this.reports().filter(r => {
      const s = r.status?.toUpperCase();
      return s === 'RESOLVED' || s === 'CLOSED';
    }).length
  );

  constructor(private readonly manifestationService: ManifestationService) {}

  ngOnInit(): void {
    this.manifestationService.list(0, 50).subscribe({
      next: data => this.reports.set(data),
      error: err => console.error('Erro na listagem:', err)
    });
  }

  getStatusClass(status: string | undefined): string {
    const s = status?.toUpperCase();
    if (s === 'RESOLVED' || s === 'CLOSED') return 'status-completed';
    return 'status-analysis';
  }
}
