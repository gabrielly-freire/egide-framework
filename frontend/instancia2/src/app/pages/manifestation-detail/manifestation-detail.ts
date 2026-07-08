import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { Manifestation, ManifestationService } from '../../services/manifestation/manifestation.service';

@Component({
  selector: 'app-manifestation-detail',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div style="max-width: 820px; margin: 0 auto;">
      <a routerLink="/manifestacoes" style="color:#6366f1; text-decoration:none;">&larr; Voltar à lista</a>

      @if (m(); as man) {
        <h1 style="margin:12px 0 4px;">{{ man.title }}</h1>
        <p style="color:#64748b; margin:0 0 20px;">Protocolo <strong>{{ man.protocolNumber }}</strong></p>

        <div style="display:grid; grid-template-columns:repeat(4,1fr); gap:12px; margin-bottom:20px;">
          <div class="chip"><span>Status</span><strong>{{ man.status }}</strong></div>
          <div class="chip"><span>Tipo</span><strong>{{ man.type }}</strong></div>
          <div class="chip"><span>Categoria</span><strong>{{ man.category || '—' }}</strong></div>
          <div class="chip"><span>Risco</span><strong>{{ man.riskLevel || '—' }}</strong></div>
        </div>

        <h3 style="margin:0 0 6px;">Descrição (após anonimização)</h3>
        <p style="background:#f8fafc; border:1px solid #e2e8f0; border-radius:8px; padding:16px; white-space:pre-wrap;">{{ man.description }}</p>

        <h3 style="margin:24px 0 8px;">Workflow</h3>
        <div style="display:flex; gap:12px;">
          <button (click)="advance()" style="padding:10px 18px; border:none; border-radius:8px; background:#6366f1; color:#fff; cursor:pointer;">Avançar</button>
          <button (click)="appeal()" style="padding:10px 18px; border:1px solid #6366f1; border-radius:8px; background:#fff; color:#6366f1; cursor:pointer;">Interpor recurso</button>
        </div>

        @if (message()) {
          <p style="margin-top:16px; padding:12px 16px; border-radius:8px; background:#eef2ff; color:#3730a3;">{{ message() }}</p>
        }
      } @else {
        <p style="margin-top:24px;">Carregando...</p>
      }
    </div>
    <style>
      .chip { display:flex; flex-direction:column; gap:2px; background:#fff; border:1px solid #e2e8f0; border-radius:8px; padding:10px 12px; }
      .chip span { font-size:11px; color:#94a3b8; text-transform:uppercase; }
    </style>
  `
})
export class ManifestationDetail implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly service = inject(ManifestationService);

  readonly m = signal<Manifestation | null>(null);
  readonly message = signal<string | null>(null);
  private id = 0;

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.load();
  }

  private load(): void {
    this.service.getById(this.id).subscribe({
      next: man => this.m.set(man),
      error: () => this.message.set('Não foi possível carregar a manifestação.')
    });
  }

  advance(): void {
    this.service.advance(this.id).subscribe({
      next: man => { this.m.set(man); this.message.set('Avançou para: ' + man.status); },
      error: err => this.message.set(this.errorText(err))
    });
  }

  appeal(): void {
    this.service.appeal(this.id).subscribe({
      next: man => { this.m.set(man); this.message.set('Recurso aceito — reabriu a mediação: ' + man.status); },
      error: err => this.message.set(this.errorText(err))
    });
  }

  private errorText(err: any): string {
    return err?.error?.detail || err?.error?.title || 'Ação não permitida neste status.';
  }
}
