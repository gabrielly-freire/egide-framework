import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { User } from '../../models/usuario.model';
import { UserService } from '../../services/user/user';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './user-management.html',
  styleUrl: './user-management.css'
})
export class UserManagement implements OnInit {
  readonly users = signal<User[]>([]);
  readonly loading = signal(false);
  readonly saving = signal(false);
  readonly error = signal<string | null>(null);
  readonly success = signal<string | null>(null);
  readonly showForm = signal(false);

  readonly roleOptions: Array<{ value: User['role']; label: string }> = [
    { value: 'REMONSTRANT', label: 'Reclamante' },
    { value: 'LISTENER', label: 'Ouvidor' },
    { value: 'GENERAL_LISTENER', label: 'Ouvidor Geral' },
    { value: 'MANAGER', label: 'Gestor' },
    { value: 'ADMIN', label: 'Administrador' }
  ];

  readonly userForm;

  constructor(
    private readonly fb: FormBuilder,
    private readonly userService: UserService
  ) {
    this.userForm = this.fb.nonNullable.group({
      name: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
      role: ['REMONSTRANT' as User['role'], [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading.set(true);
    this.error.set(null);

    this.userService
      .listAll()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: users => this.users.set(users),
        error: err => this.error.set(this.formatError(err))
      });
  }

  openCreateForm(): void {
    this.showForm.set(true);
    this.success.set(null);
    this.userForm.reset({ name: '', email: '', username: '', role: 'REMONSTRANT', password: '' });
  }

  closeForm(): void {
    this.showForm.set(false);
    this.userForm.reset({ name: '', email: '', username: '', role: 'REMONSTRANT', password: '' });
  }

  saveUser(): void {
    if (this.userForm.invalid) {
      this.userForm.markAllAsTouched();
      return;
    }

    this.saving.set(true);
    this.error.set(null);
    this.success.set(null);

    const value = this.userForm.getRawValue();
    const payload: User = {
      name: value.name.trim(),
      email: value.email.trim(),
      username: value.username.trim(),
      role: value.role,
      password: value.password.trim()
    };

    this.userService
      .create(payload)
      .pipe(finalize(() => this.saving.set(false)))
      .subscribe({
        next: () => {
          this.success.set('Usuário criado com sucesso.');
          this.closeForm();
          this.loadUsers();
        },
        error: err => this.error.set(this.formatError(err))
      });
  }

  roleLabel(role: User['role']): string {
    return this.roleOptions.find(option => option.value === role)?.label ?? role;
  }

  private formatError(error: unknown): string {
    const message = (error as any)?.message;
    if (typeof message === 'string') {
      return message;
    }
    return 'Não foi possível concluir a operação de usuários.';
  }
}
