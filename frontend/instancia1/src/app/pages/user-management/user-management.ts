import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { Department } from '../../models/department.model';
import { Role, User, UserResponse } from '../../models/usuario.model';
import { DepartmentService } from '../../services/department/department';
import { UserService } from '../../services/user/user';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './user-management.html',
  styleUrl: './user-management.css'
})
export class UserManagement implements OnInit {
  readonly users = signal<UserResponse[]>([]);
  readonly loading = signal(false);
  readonly saving = signal(false);
  readonly error = signal<string | null>(null);
  readonly success = signal<string | null>(null);
  readonly showForm = signal(false);
  readonly departments = signal<Department[]>([]);
  readonly loadingDepartments = signal(false);

  readonly roleOptions: Array<{ value: Role; label: string }> = [
    { value: 'REMONSTRANT', label: 'Reclamante' },
    { value: 'LISTENER', label: 'Ouvidor' },
    { value: 'MANAGER', label: 'Gestor' },
    { value: 'GENERAL_LISTENER', label: 'Ouvidor Geral' },
    { value: 'ADMIN', label: 'Administrador' }
  ];

  readonly userForm;

  constructor(
    private readonly fb: FormBuilder,
    private readonly userService: UserService,
    private readonly departmentService: DepartmentService
  ) {
    this.userForm = this.fb.nonNullable.group({
      name: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
      role: ['REMONSTRANT' as Role, [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      departmentId: [0, [Validators.required, Validators.min(1)]]
    });
  }

  ngOnInit(): void {
    this.loadDepartments();
    this.loadUsers();
  }

  loadDepartments(): void {
    this.loadingDepartments.set(true);
    this.departmentService
      .listAll()
      .pipe(finalize(() => this.loadingDepartments.set(false)))
      .subscribe({
        next: departments => this.departments.set(departments),
        error: err => this.error.set(this.formatError(err))
      });
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
    this.userForm.reset({
      name: '',
      email: '',
      username: '',
      role: 'REMONSTRANT',
      password: '',
      departmentId: 0
    });
    if (this.departments().length === 0) {
      this.loadDepartments();
    }
  }

  closeForm(): void {
    this.showForm.set(false);
  }

  saveUser(): void {
    if (this.userForm.invalid) {
      this.userForm.markAllAsTouched();
      return;
    }

    this.saving.set(true);
    this.error.set(null);
    this.success.set(null);

    const formValue = this.userForm.getRawValue();
    const payload: User = {
      name: formValue.name.trim(),
      email: formValue.email.trim(),
      username: formValue.username.trim(),
      role: formValue.role,
      password: formValue.password.trim(),
      departmentId: Number(formValue.departmentId)
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

  roleLabel(role: Role): string {
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
