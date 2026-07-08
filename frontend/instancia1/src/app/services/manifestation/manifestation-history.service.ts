import { Injectable } from '@angular/core';

export interface ManifestationHistoryEntry {
  protocolNumber: string;
  title: string;
  createdAt: string;
}

// Histórico local (por navegador) dos protocolos que o próprio usuário registrou.
// O Core não vincula manifestação -> autor (é anônimo por design), então não há
// como listar "minhas manifestações" via backend — isso substitui essa listagem.
@Injectable({ providedIn: 'root' })
export class ManifestationHistoryService {
  private readonly storageKey = 'egide.my-protocols';

  add(entry: ManifestationHistoryEntry): void {
    const list = this.list().filter(e => e.protocolNumber !== entry.protocolNumber);
    list.unshift(entry);
    localStorage.setItem(this.storageKey, JSON.stringify(list.slice(0, 20)));
  }

  list(): ManifestationHistoryEntry[] {
    try {
      const raw = localStorage.getItem(this.storageKey);
      return raw ? JSON.parse(raw) : [];
    } catch {
      return [];
    }
  }
}
