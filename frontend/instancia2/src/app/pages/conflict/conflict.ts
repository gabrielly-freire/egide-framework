import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  ConflictCheck,
  Manifestation,
  ManifestationService,
  Party
} from '../../services/manifestation/manifestation.service';

@Component({
  selector: 'app-conflict',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div style="max-width: 860px; margin: 0 auto;">
      <span style="font-size:12px; color:#94a3b8; text-transform:uppercase;">Painel Administrativo</span>
      <h1 style="margin:4px 0 20px;">Conflito de Interesse</h1>

      @if (message()) {
        <p style="padding:12px 16px; border-radius:8px; background:#eef2ff; color:#3730a3;">{{ message() }}</p>
      }

      <!-- 1) Cadastrar parte com unidade -->
      <div class="card">
        <h3>1. Cadastrar pessoa (nome + unidade)</h3>
        <div class="row">
          <input [(ngModel)]="partyName" placeholder="Nome (ex.: Ana)" class="inp" />
          <input [(ngModel)]="partyUnit" placeholder="Unidade (ex.: DIMAP)" class="inp" />
          <button (click)="createParty()" class="btn">Cadastrar</button>
        </div>
        @if (parties().length) {
          <table class="tbl">
            <tr><th>ID</th><th>Nome</th><th>Unidade</th></tr>
            @for (p of parties(); track p.id) {
              <tr><td>{{ p.id }}</td><td>{{ p.name }}</td><td>{{ p.unit }}</td></tr>
            }
          </table>
        }
      </div>

      <!-- 2) Vincular denunciado a uma manifestação -->
      <div class="card">
        <h3>2. Marcar o denunciado de uma manifestação</h3>
        <div class="row">
          <select [(ngModel)]="accManifestationId" class="inp">
            <option [ngValue]="undefined">Selecione a manifestação</option>
            @for (man of manifestations(); track man.id) {
              <option [ngValue]="man.id">{{ man.protocolNumber }} — {{ man.title }}</option>
            }
          </select>
          <select [(ngModel)]="accPartyId" class="inp">
            <option [ngValue]="undefined">Selecione o denunciado</option>
            @for (p of parties(); track p.id) {
              <option [ngValue]="p.id">{{ p.name }} ({{ p.unit }})</option>
            }
          </select>
          <button (click)="addAccusation()" class="btn">Vincular</button>
        </div>
      </div>

      <!-- 3) Checar conflito -->
      <div class="card">
        <h3>3. Checar conflito de um analista</h3>
        <div class="row">
          <select [(ngModel)]="chkManifestationId" class="inp">
            <option [ngValue]="undefined">Selecione a manifestação</option>
            @for (man of manifestations(); track man.id) {
              <option [ngValue]="man.id">{{ man.protocolNumber }} — {{ man.title }}</option>
            }
          </select>
          <select [(ngModel)]="chkAnalystId" class="inp">
            <option [ngValue]="undefined">Selecione o analista</option>
            @for (p of parties(); track p.id) {
              <option [ngValue]="p.id">{{ p.name }} ({{ p.unit }})</option>
            }
          </select>
          <button (click)="check()" class="btn">Checar</button>
        </div>
        @if (result(); as r) {
          <p style="margin-top:12px; font-size:18px;">
            Resultado:
            <strong [style.color]="r.hasConflict ? '#dc2626' : '#16a34a'">
              {{ r.hasConflict ? 'HÁ CONFLITO (mesma unidade)' : 'SEM CONFLITO' }}
            </strong>
          </p>
        }
      </div>
    </div>
    <style>
      .card { background:#fff; border:1px solid #e2e8f0; border-radius:10px; padding:18px; margin-bottom:16px; }
      .card h3 { margin:0 0 12px; }
      .row { display:flex; gap:10px; flex-wrap:wrap; align-items:center; }
      .inp { flex:1; min-width:180px; padding:10px 12px; border:1px solid #cbd5e1; border-radius:8px; }
      .btn { padding:10px 18px; border:none; border-radius:8px; background:#6366f1; color:#fff; cursor:pointer; }
      .tbl { width:100%; margin-top:12px; border-collapse:collapse; }
      .tbl th, .tbl td { text-align:left; padding:6px 8px; border-bottom:1px solid #eef2f7; font-size:14px; }
    </style>
  `
})
export class ConflictPage implements OnInit {
  private readonly service = inject(ManifestationService);

  readonly parties = signal<Party[]>([]);
  readonly manifestations = signal<Manifestation[]>([]);
  readonly message = signal<string | null>(null);
  readonly result = signal<ConflictCheck | null>(null);

  partyName = '';
  partyUnit = '';
  accManifestationId?: number;
  accPartyId?: number;
  chkManifestationId?: number;
  chkAnalystId?: number;

  ngOnInit(): void {
    this.service.list(0, 50).subscribe({ next: data => this.manifestations.set(data) });
  }

  createParty(): void {
    if (!this.partyName.trim() || !this.partyUnit.trim()) {
      return;
    }
    this.service.createParty({ name: this.partyName.trim(), unit: this.partyUnit.trim() }).subscribe({
      next: party => {
        this.parties.update(list => [...list, party]);
        this.message.set(`Parte cadastrada: ${party.name} (${party.unit}), id ${party.id}.`);
        this.partyName = '';
        this.partyUnit = '';
      },
      error: () => this.message.set('Erro ao cadastrar a parte.')
    });
  }

  addAccusation(): void {
    if (!this.accManifestationId || !this.accPartyId) {
      return;
    }
    this.service.addAccusation(this.accManifestationId, this.accPartyId).subscribe({
      next: () => this.message.set('Denunciado vinculado à manifestação.'),
      error: () => this.message.set('Erro ao vincular o denunciado.')
    });
  }

  check(): void {
    if (!this.chkManifestationId || !this.chkAnalystId) {
      return;
    }
    this.result.set(null);
    this.service.checkConflict(this.chkManifestationId, this.chkAnalystId).subscribe({
      next: res => this.result.set(res),
      error: () => this.message.set('Erro ao checar o conflito.')
    });
  }
}
