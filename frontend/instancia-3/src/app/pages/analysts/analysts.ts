import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { AnalystService } from '../../services/analyst.service';
import { AnalystResponse } from '../../models/analyst.model';

@Component({
  selector: 'app-analysts',
  imports: [ReactiveFormsModule, DatePipe],
  templateUrl: './analysts.html',
  styleUrl: './analysts.css',
})
export class AnalystsPage implements OnInit {
  private readonly analystService = inject(AnalystService);
  private readonly fb = inject(FormBuilder);

  protected analysts = signal<AnalystResponse[]>([]);
  protected loading = signal(true);
  protected error = signal('');

  protected showModal = signal(false);
  protected editingId = signal<number | null>(null);
  protected saving = signal(false);
  protected modalError = signal('');

  protected form = this.fb.group({
    name: ['', [Validators.required, Validators.maxLength(255)]],
    specialty: ['', [Validators.required, Validators.maxLength(100)]],
    region: ['', [Validators.required, Validators.maxLength(100)]],
    email: ['', [Validators.required, Validators.email]],
  });

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.analystService.list().subscribe({
      next: list => {
        this.analysts.set(list);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Erro ao carregar analistas.');
        this.loading.set(false);
      },
    });
  }

  openCreate(): void {
    this.editingId.set(null);
    this.form.reset();
    this.modalError.set('');
    this.showModal.set(true);
  }

  openEdit(analyst: AnalystResponse): void {
    this.editingId.set(analyst.id);
    this.form.patchValue({
      name: analyst.name,
      specialty: analyst.specialty,
      region: analyst.region,
      email: '',
    });
    this.modalError.set('');
    this.showModal.set(true);
  }

  closeModal(): void {
    this.showModal.set(false);
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving.set(true);
    this.modalError.set('');

    const value = this.form.getRawValue() as any;
    const id = this.editingId();

    const req$ = id
      ? this.analystService.update(id, value)
      : this.analystService.create(value);

    req$.subscribe({
      next: () => {
        this.closeModal();
        this.load();
        this.saving.set(false);
      },
      error: () => {
        this.modalError.set('Erro ao salvar. Verifique os dados e tente novamente.');
        this.saving.set(false);
      },
    });
  }

  delete(analyst: AnalystResponse): void {
    if (!confirm(`Excluir o analista "${analyst.name}"?`)) return;
    this.analystService.delete(analyst.id).subscribe({
      next: () => this.load(),
      error: () => this.error.set('Erro ao excluir analista.'),
    });
  }
}
