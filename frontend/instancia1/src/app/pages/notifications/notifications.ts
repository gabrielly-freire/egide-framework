import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { finalize } from 'rxjs/operators';
import { forkJoin } from 'rxjs';
import { NotificationResponse, notificationTypeLabel } from '../../models/notification.model';
import { NotificationService } from '../../services/notification/notification.service';

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [CommonModule, MatIconModule],
  templateUrl: './notifications.html',
  styleUrl: './notifications.css',
})
export class Notifications implements OnInit {
  loading = signal(false);
  notifications = signal<NotificationResponse[]>([]);

  unreadCount = computed(() => this.notifications().filter(n => !n.read).length);

  constructor(private readonly notificationService: NotificationService) {}

  ngOnInit(): void {
    this.refresh();
  }

  refresh(): void {
    this.loading.set(true);
    this.notificationService
      .list()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: data => this.notifications.set(data ?? []),
        error: err => console.error('Erro ao carregar notificações:', err),
      });
  }

  markAsRead(notification: NotificationResponse): void {
    if (!notification?.id || notification.read) return;

    this.notificationService.markAsRead(notification.id).subscribe({
      next: () => {
        this.notifications.update(list =>
          list.map(n => (n.id === notification.id ? { ...n, read: true } : n))
        );
      },
      error: err => console.error('Erro ao marcar como lida:', err),
    });
  }

  // Não há endpoint de "marcar todas" no backend; faz uma chamada por notificação não lida.
  markAllAsRead(): void {
    const unread = this.notifications().filter(n => !n.read);
    if (unread.length === 0) return;

    forkJoin(unread.map(n => this.notificationService.markAsRead(n.id))).subscribe({
      next: () => {
        this.notifications.update(list => list.map(n => ({ ...n, read: true })));
      },
      error: err => console.error('Erro ao marcar todas como lidas:', err),
    });
  }

  typeLabel(type: string): string {
    return notificationTypeLabel(type);
  }
}
