export type NotificationType = 'MANIFESTATION_CREATED' | 'STATUS_CHANGED' | 'ASSIGNED';

export interface NotificationResponse {
  id: number;
  manifestationId: number | null;
  type: NotificationType;
  message: string;
  read: boolean;
  createdAt: string;
}

export function notificationTypeLabel(type: NotificationType | string): string {
  switch (type) {
    case 'MANIFESTATION_CREATED': return 'Nova manifestação';
    case 'STATUS_CHANGED': return 'Status alterado';
    case 'ASSIGNED': return 'Responsável atribuído';
    default: return type;
  }
}
